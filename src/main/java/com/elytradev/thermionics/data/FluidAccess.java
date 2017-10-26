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
