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
import com.elytradev.thermionics.block.BlockBattery;
import com.elytradev.thermionics.data.NoExtractEnergyStorageView;
import com.elytradev.thermionics.data.NoReceiveEnergyStorageView;
import com.elytradev.thermionics.data.ObservableEnergyStorage;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.data.RelativeDirection;
import com.elytradev.thermionics.transport.RFTransport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityBattery extends TileEntity implements ITickable {
	protected CapabilityProvider capabilities = new CapabilityProvider();
	protected ObservableEnergyStorage energyStorage = new ObservableEnergyStorage(BlockBattery.CAPACITY, 800, 800);
	
	public TileEntityBattery() {
		//Batteries have a bit of a complicated relationship with their neighbors.
		capabilities.registerForSides(CapabilityEnergy.ENERGY, ()->new NoExtractEnergyStorageView(energyStorage),
				RelativeDirection.TOP, RelativeDirection.BOTTOM, RelativeDirection.PORT, RelativeDirection.STARBOARD, RelativeDirection.STERN);
		capabilities.registerForSides(CapabilityEnergy.ENERGY, ()->new NoReceiveEnergyStorageView(energyStorage),
				RelativeDirection.BOW);
		capabilities.registerForSides(CapabilityEnergy.ENERGY, ()->energyStorage,
				RelativeDirection.WITHIN);
		
		energyStorage.listen(this::markDirty);
		
		ProbeDataSupport.registerRFInspector(this, capabilities);
	}
	
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
		EnumFacing front = EnumFacing.UP;
		IBlockState state = world.getBlockState(pos);
		if (state.getProperties().keySet().contains(BlockBattery.FACING)) {
			front = state.getValue(BlockBattery.FACING);
		}
		
		if (capabilities.canProvide(RelativeDirection.of(front, side), cap)) return true;
		else return super.hasCapability(cap, side);
	}
	
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side) {
		EnumFacing front = EnumFacing.UP;
		IBlockState state = world.getBlockState(pos);
		if (state.getProperties().keySet().contains(BlockBattery.FACING)) {
			front = state.getValue(BlockBattery.FACING);
		}
		
		//If it's going to throw an exception here in my code, I'd rather it did.
		T result = capabilities.provide(RelativeDirection.of(front, side), cap);
		
		//I'd rather return null (which is valid according to the contract) than throw an exception down here.
		if (result==null) {
			try {
				return super.getCapability(cap, side);
			} catch (Throwable t) {}
		}
		return result;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.getBlock()==newState.getBlock()) return false;
		else return super.shouldRefresh(world, pos, oldState, newState);
	}
	
	@Override
	public void update() {
		this.energyStorage.tick();
		IBlockState cur = world.getBlockState(pos);
		EnumFacing facing = cur.getValue(BlockBattery.FACING);
		IEnergyStorage target = RFTransport.getStorage(world, pos.offset(facing), facing.getOpposite());
		if (target.canReceive()) {
			int toPush = Math.min(energyStorage.getEnergyStored(), 800);
			int received = target.receiveEnergy(toPush, false);
			if (received>0) energyStorage.extractEnergy(received, false);
		}
	}
}
