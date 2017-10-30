/*
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.thermionics.tileentity;

import java.util.HashSet;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.api.impl.RotaryPowerSupply;
import com.elytradev.thermionics.data.RelativeDirection;
import com.elytradev.thermionics.transport.RotaryTransport;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityConvectionMotor extends TileEntityMachine implements ITickable, IContainerInventoryHolder {
	private static final float DELIVERY_RATE = 0.5f;
	
	private ConcreteItemStorage containerStorage = new ConcreteItemStorage(0).withName("tile.thermionics.machine.convectionmotor.name");
	private HeatStorage heatStorage = new HeatStorage(1000);
	private RotaryPowerSupply rotaryPower = new RotaryPowerSupply();
	private float torqueLoad = 0;
	
	public TileEntityConvectionMotor() {
		this.heatStorage.listen(this::markDirty);
		this.rotaryPower.listen(this::markDirty);
		
		capabilities.registerForSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->HeatStorageView.insertOnlyOf(heatStorage),
				RelativeDirection.ALL_SIDES
				);
		capabilities.registerForSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->heatStorage,
				RelativeDirection.WITHIN);
		
		capabilities.registerForAllSides(Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY, ()->rotaryPower);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("rotarysupply", Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY.writeNBT(rotaryPower, null));
		tagOut.setTag("heatstorage", Thermionics.CAPABILITY_HEATSTORAGE.writeNBT(heatStorage, null));
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("rotarysupply")) {
			Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY.readNBT(rotaryPower, null, tag.getTag("rotarysupply"));
		}
		if (tag.hasKey("heatstorage")) {
			Thermionics.CAPABILITY_HEATSTORAGE.readNBT(heatStorage, null, tag.getTag("heatstorage"));
		}
	}
	
	@Override
	public void update() {
		if (world.isRemote) return;
		//Clear out any existing power.
		//TODO: Kill this once we're sure we clear out our power after a tick, to support injection from chained machines
		rotaryPower.extractPower(Integer.MAX_VALUE);
		
		
		// # Check for redstone shutoff
		if (world.isBlockIndirectlyGettingPowered(pos)!=0) {
			this.markActive(false);
			this.rotaryPower.autoSetTorqueSetting(0f);
			return;
		}
		
		// # Trade heat for a power pulse
		/* Notes on conversion ratio: one coal = 1600H (because H == furnaceTicks)
		 * H -> P is a 3/4 conversion ratio
		 * That means 1600H (one coal) is worth 1200P
		 * 300P is needed to grind one iron ore into 2 iron grit
		 * Given that a hammer mill requires 10 torque to complete an operation, that means 30 revolutions need to be
		 * buffered into the machine in order to complete an operation.
		 * 
		 * 2.5 revolutions per tick is 3000RPM. This is the general cap for most machines.
		 * 
		 * This means that, driven by a monstrous imperative force, a hammer mill could tear through iron ore once each
		 * 12 ticks.
		 * 
		 * An *air convection* motor is unlikely to provide more than 1.5 revolutions per tick, or 1800RPM, under
		 * typical 10 torque load.
		 * 
		 * That's 15P/t. We have our motor folks.
		 * 
		 */
		
		//drawing 15P means we want 20H
		int extracted = heatStorage.extractHeat((int)(20*DELIVERY_RATE), false);
		if (extracted==0) {
			this.markActive(false);
			return;
		}
		rotaryPower.insertPower((int)(extracted*3f/4f));
		
		// # Discover the torque load
		HashSet<BlockPos> searchedNodes = new HashSet<>();
		HashSet<BlockPos> consumers = new HashSet<>();
		for(EnumFacing f : EnumFacing.values()) {
			RotaryTransport.searchForConsumers(world, pos, f, searchedNodes, consumers);
		}
		
		torqueLoad = RotaryTransport.getTorqueLoad(world, consumers);
		//torqueLoad = 0;
		
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
		//TODO: Everything
		for(BlockPos consumer : consumers) {
			RotaryTransport.deliverRevolutions(world, consumer, revolutionsThisTick);
		}
		
		
	}
	
	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(containerStorage);
		
		if (!this.world.isRemote) return result
				.withField(0, heatStorage::getHeatStored)
				.withField(1, heatStorage::getMaxHeatStored)
				.withField(2, ()->(int)rotaryPower.getTorqueSetting())
				.withField(3, ()->(int)rotaryPower.getMaxBufferedPower())
				.withField(4, ()->(int)torqueLoad);
		//Todo: Figure out how we can find the torque load
		
		return result;
	}
	
}
