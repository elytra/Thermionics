/**
 * MIT License
 *
 * Copyright (c) 2017 The Elytra Team
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

package io.github.elytra.thermionics.tileentity;

import io.github.elytra.thermionics.CapabilityProvider;
import io.github.elytra.thermionics.data.RelativeDirection;
import net.minecraft.block.BlockDirectional;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityMachine extends TileEntity {
	protected CapabilityProvider capabilities = new CapabilityProvider();
	
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
}
