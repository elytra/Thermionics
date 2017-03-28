/**
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

import com.elytradev.thermionics.CapabilityProvider;
import com.elytradev.thermionics.data.ObservableEnergyStorage;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.data.RelativeDirection;
import com.elytradev.thermionics.transport.RFTransport;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityCableRF extends TileEntity implements ITickable {
	private static final int MAX_TICK_COUNTER = 1;
	private static final int CAPACITANCE = 6000;
	private int tickCounter = 0;
	CapabilityProvider capabilities = new CapabilityProvider();
	private ObservableEnergyStorage energyStorage = new ObservableEnergyStorage(CAPACITANCE, CAPACITANCE, 800*MAX_TICK_COUNTER).withPollTime(MAX_TICK_COUNTER);
	
	public TileEntityCableRF() {
		capabilities.registerForAllSides(CapabilityEnergy.ENERGY, ()->energyStorage);
		energyStorage.listen(this::markDirty);
		
		
		if (ProbeDataSupport.PROBE_PRESENT) {
			capabilities.registerForAllSides(ProbeDataSupport.PROBE_CAPABILITY, ()->new ProbeDataSupport.RFCableInspector(this));
		}
	}
	
	/*
	public TileEntityCable(boolean rf, boolean redstone) {
		if (rf) capabilities.registerForAllSides(CapabilityEnergy.ENERGY, ()->energyStorage);
		if (redstone) capabilities.registerForAllSides(null, null);
		
		
	}*/
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tagOut = super.writeToNBT(compound);
		NBTBase energyTag = CapabilityEnergy.ENERGY.getStorage().writeNBT(CapabilityEnergy.ENERGY, energyStorage, null);
		tagOut.setTag("energy", energyTag);
		return tagOut;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTBase energyTag = compound.getTag("energy");
		if (energyTag!=null) {
			try {
				CapabilityEnergy.ENERGY.getStorage().readNBT(CapabilityEnergy.ENERGY, energyStorage, null, energyTag);
			} catch (Throwable t) {}
		}
		
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side) {
		if (capabilities.canProvide(RelativeDirection.BOW, cap)) return true;
		else return super.hasCapability(cap, side);
	}
	
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side) {
		//If it's going to throw an exception here in my code, I'd rather it did.
		T result = capabilities.provide(RelativeDirection.BOW, cap);
		
		//I'd rather return null (which is valid according to the contract) than throw an exception down here.
		if (result==null) {
			try {
				return super.getCapability(cap, side);
			} catch (Throwable t) {}
		}
		return result;
	}

	@Override
	public void update() {
		this.energyStorage.tick();
		tickCounter++;
		if (tickCounter>=MAX_TICK_COUNTER) {
			tickCounter=0;
			RFTransport.diffuse(world, pos, energyStorage);
		}
	}
}
