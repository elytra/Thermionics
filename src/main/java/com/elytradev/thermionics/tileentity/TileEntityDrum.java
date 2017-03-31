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
import com.elytradev.thermionics.data.ObservableFluidStorage;
import com.elytradev.thermionics.data.RelativeDirection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileEntityDrum extends TileEntity {
	private ObservableFluidStorage storage;
	private CapabilityProvider capabilities = new CapabilityProvider();
	
	public TileEntityDrum() {
		this.storage = new ObservableFluidStorage(16000);
		storage.listen(this::markDirty);
		capabilities.registerForAllSides(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ()->storage);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tagOut = super.writeToNBT(compound);
		NBTBase fluidTag = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.getStorage().writeNBT(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, storage, null);
		tagOut.setTag("fluid", fluidTag);
		return tagOut;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTBase fluidTag = compound.getTag("fluid");
		if (fluidTag!=null) {
			try {
				CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.getStorage().readNBT(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, storage, null, fluidTag);
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
	
	public ObservableFluidStorage getFluidStorage() {
		//return storage;
		return (ObservableFluidStorage) capabilities.provide(RelativeDirection.BOW, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (oldState.getBlock()==newState.getBlock()) return false;
		else return super.shouldRefresh(world, pos, oldState, newState);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("!Fluid Tank!");
	}
	
	@Override
	public void markDirty() {
		//System.out.println("FLUID UPDATE");
		super.markDirty();
	}
}
