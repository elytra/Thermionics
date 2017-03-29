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
import com.elytradev.thermionics.block.BlockMachineBase;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.data.RelativeDirection;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityMachine extends TileEntity {
	protected CapabilityProvider capabilities = new CapabilityProvider();
	
	public TileEntityMachine() {
		if (ProbeDataSupport.PROBE_PRESENT) {
			capabilities.registerForAllSides(ProbeDataSupport.PROBE_CAPABILITY, ()->new ProbeDataSupport.MachineInspector(this));
		}
	}
	
	public EnumFacing getFacing() {
		try {
			return this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING);
		} catch (Throwable t) {
			return EnumFacing.NORTH;
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side) {
		if (capabilities.canProvide(RelativeDirection.of(getFacing(), side), cap)) return true;
		else return super.hasCapability(cap, side);
	}
	
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side) {
		//If it's going to throw an exception here in my code, I'd rather it did.
		T result = capabilities.provide(RelativeDirection.of(getFacing(), side), cap);
		
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
	
	public void markActive(boolean active) {
		IBlockState cur = world.getBlockState(pos);
		if (cur.getValue(BlockMachineBase.ACTIVE)==active) return;
		world.setBlockState(pos, cur.withProperty(BlockMachineBase.ACTIVE,active));
	}
}
