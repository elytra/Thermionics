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

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.data.NoReceiveEnergyStorageView;
import com.elytradev.thermionics.data.ObservableEnergyStorage;
import com.elytradev.thermionics.transport.RFTransport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityRotaryGenerator extends TileEntityMachine implements ITickable {
	
	/* The baseline RF for a piece of coal is about 40,000RF.
	 * 1 furnace fuel tick is 1H, so 1 coal is worth 1600H.
	 * 3H = 4P, where P is the rotary power unit of *power* (torque * revolutions)
	 * so 1 coal (1600H) is worth 2133.33333P
	 * That drives the crank at 20 torque for 106.666666666 revolutions
	 * so at 375RF per rev, that'll yield 40,000RF per piece of coal. (18.75 RF per P)
	 */
	public static float RF_PER_POWER = 18.75f;
	
	private RotaryPowerConsumer rotaryPower = new RotaryPowerConsumer();
	private ObservableEnergyStorage rf = new ObservableEnergyStorage(8000);
	
	public TileEntityRotaryGenerator() {
		rotaryPower.listen(this::markDirty);
		rf.listen(this::markDirty);
		
		capabilities.registerForAllSides(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, ()->rotaryPower);
		capabilities.registerForAllSides(CapabilityEnergy.ENERGY, ()->new NoReceiveEnergyStorageView(rf));
		
		rotaryPower.setRequiredTorque(20.0f);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("rotaryconsumer", Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.writeNBT(rotaryPower, null));
		
		tagOut.setTag("energy", CapabilityEnergy.ENERGY.getStorage().writeNBT(CapabilityEnergy.ENERGY, rf, null));
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("rotaryconsumer")) {
			Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.readNBT(rotaryPower, null, tag.getTag("rotaryconsumer"));
		}
		
		if (tag.hasKey("energy")) {
			CapabilityEnergy.ENERGY.getStorage().readNBT(CapabilityEnergy.ENERGY, rf, null, tag.getTag("energy"));
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.getBlock()==newState.getBlock()) return false;
		else return super.shouldRefresh(world, pos, oldState, newState);
	}
	
	@Override
	public void update() {
		this.rf.tick();
		
		for(EnumFacing facing : EnumFacing.values()) {
			IEnergyStorage target = RFTransport.getStorage(world, pos.offset(facing), facing.getOpposite());
			if (target.canReceive()) {
				int toPush = Math.min(rf.getEnergyStored(), 800);
				int received = target.receiveEnergy(toPush, false);
				if (received>0) rf.extractEnergy(received, false);
			}
		}
		
		float consumed = rotaryPower.getBufferedRevolutions();
		int generated = (int)(consumed * 20 * RF_PER_POWER);
		rotaryPower.clearRevolutions();
		
		if (generated>0) rf.receiveEnergy(generated, false);
	}
}
