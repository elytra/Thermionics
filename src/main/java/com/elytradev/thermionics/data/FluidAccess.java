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
package com.elytradev.thermionics.data;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidAccess implements IFluidTank, IFluidHandler {
	private final IFluidTank delegate;
	private boolean canExtract = false;
	private boolean canInsert = false;
	
	private FluidAccess(IFluidTank tank) {
		delegate = tank;
	}
	
	public FluidAccess readOnly(IFluidTank tank) {
		FluidAccess result = new FluidAccess(tank);
		result.canExtract = false;
		result.canInsert = false;
		return result;
	}
	
	public FluidAccess insertOnly(IFluidTank tank) {
		FluidAccess result = new FluidAccess(tank);
		result.canExtract = false;
		result.canInsert = true;
		return result;
	}
	
	public FluidAccess extractOnly(IFluidTank tank) {
		FluidAccess result = new FluidAccess(tank);
		result.canExtract = true;
		result.canInsert = false;
		return result;
	}
	
	public FluidAccess fullAccess(IFluidTank tank) {
		FluidAccess result = new FluidAccess(tank);
		result.canExtract = true;
		result.canInsert = true;
		return result;
	}

	@Override
	public FluidStack getFluid() {
		return delegate.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return delegate.getFluidAmount();
	}

	@Override
	public int getCapacity() {
		return delegate.getCapacity();
	}

	@Override
	public FluidTankInfo getInfo() {
		return delegate.getInfo();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (canInsert) {
			return delegate.fill(resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (canExtract) {
			return delegate.drain(maxDrain, doDrain);
		} else {
			return null; //XXX: As soon as Forge fixes things so that empty fluidStacks aren't null, get rid of the nulls
		}
	}
	
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (delegate.getFluid()!=null && resource!=null && delegate.getFluid().isFluidEqual(resource)) {
			return drain(resource.amount, doDrain);
		} else {
			return null;//XXX: As soon as Forge fixes things so that empty fluidStacks aren't null, get rid of the nulls
		}
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[] {
			new IFluidTankProperties() {

				@Override
				public FluidStack getContents() {
					return delegate.getFluid();
				}

				@Override
				public int getCapacity() {
					return delegate.getCapacity();
				}

				@Override
				public boolean canFill() {
					return canInsert;
				}

				@Override
				public boolean canDrain() {
					return canExtract;
				}

				@Override
				public boolean canFillFluidType(FluidStack fluidStack) {
					if (delegate.getFluid()==null) return true;
					if (fluidStack==null) return false;
					
					return delegate.getFluid().isFluidEqual(fluidStack);
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluidStack) {
					return
							fluidStack!=null &&
							delegate.getFluid()!=null &&
							delegate.getFluid().isFluidEqual(fluidStack);
				}
				
			}
		};
	}
}
