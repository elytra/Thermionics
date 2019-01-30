package com.elytradev.thermionics.tileentity;

import java.util.HashSet;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.RotaryPowerSupply;
import com.elytradev.thermionics.data.NoExtractEnergyStorageView;
import com.elytradev.thermionics.data.ObservableEnergyStorage;
import com.elytradev.thermionics.transport.RFTransport;
import com.elytradev.thermionics.transport.RotaryTransport;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityRFMotor extends TileEntityMachine implements ITickable {
	public static int MAX_TRANSFER = 800;
	protected RotaryPowerSupply rotaryPower = new RotaryPowerSupply();
	protected float torqueLoad = 0;
	protected ObservableEnergyStorage rf = new ObservableEnergyStorage(8000);
	
	public TileEntityRFMotor() {
		rotaryPower.listen(this::markDirty);
		rf.listen(this::markDirty);
		
		capabilities.registerForAllSides(Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY, ()->rotaryPower);
		final NoExtractEnergyStorageView energyCap = new NoExtractEnergyStorageView(rf);
		capabilities.registerForAllSides(CapabilityEnergy.ENERGY, ()->energyCap);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("rotarysupply", Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY.writeNBT(rotaryPower, null));
		tagOut.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(rf, null));
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("rotarysupply")) {
			Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY.readNBT(rotaryPower, null, tag.getTag("rotarysupply"));
		}
		if (tag.hasKey("energy")) {
			CapabilityEnergy.ENERGY.readNBT(rf, null, tag.getTag("energy"));
		}
	}

	@Override
	public void update() {
		if (world.isRemote) return; 
		
		rf.tick();
		if (rf.getEnergyStored()<=0) {
			this.markActive(false);
			return;
		}
		
		// # Discover the torque load
		HashSet<BlockPos> searchedNodes = new HashSet<>();
		HashSet<BlockPos> consumers = new HashSet<>();
		for(EnumFacing f : EnumFacing.values()) {
			RotaryTransport.searchForConsumers(world, pos, f, searchedNodes, consumers);
		}
		
		torqueLoad = RotaryTransport.getTorqueLoad(world, consumers);
		if (torqueLoad>0) {
			//If there's torque applied to the motor, draw power and insert it into the motor
			//I guess we're pretending it's a stepper motor with some kind of coil-current feedback sensor, and negligible current is applied to sense torque
			
			/* So. Our goal, as an RF motor, is to try and punch out 3000RPM if possible. That's 2.5
			 * revolutions per tick. Let's draw current to satisfy up to 20 torque.
			 */
			float powerDraw = Math.min(torqueLoad, 20) * 2.5f;
			int rfDraw = (int)Math.ceil(powerDraw * TileEntityRotaryGenerator.RF_PER_POWER);
			int rfDrawn = rf.extractEnergy(rfDraw, false);
			float powerDrawn = rfDrawn * 1.0f/TileEntityRotaryGenerator.RF_PER_POWER;
			
			rotaryPower.insertPower(powerDrawn);
		} else {
			this.markActive(false);
			rotaryPower.autoSetTorqueSetting(0);
			return;
		}
		
		// # Step power towards torque load
		rotaryPower.autoSetTorqueSetting(torqueLoad);
		if (rotaryPower.getTorqueSetting()<torqueLoad) {
			//We're locked. Kill the power and stop.
			rotaryPower.extractPower(Integer.MAX_VALUE);
			this.markActive(false);
			return;
		} else {
			this.markActive(true);
		}
		
		if (rotaryPower.getTorqueSetting()<=0) return;
		float revolutionsThisTick = rotaryPower.getBufferedPower() / rotaryPower.getTorqueSetting();
		if (revolutionsThisTick<=0) return;
		// # distribute power
		for(BlockPos consumer : consumers) {
			RotaryTransport.deliverRevolutions(world, consumer, revolutionsThisTick);
		}
	}
}
