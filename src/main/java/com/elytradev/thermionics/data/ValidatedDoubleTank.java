package com.elytradev.thermionics.data;

import javax.annotation.Nullable;

import com.elytradev.concrete.inventory.ConcreteFluidTank;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * IFluidHandler capability which provides two tanks.
 */
public class ValidatedDoubleTank implements IFluidHandler {
	private FluidTank a;
	private FluidTank b;
	
	public ValidatedDoubleTank(FluidTank a, FluidTank b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return FluidTankProperties.convert(new FluidTankInfo[]{a.getInfo(), b.getInfo()});
	}
	
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (validateFill(a, resource)) {
			return a.fill(resource, doFill);
		}
		if (validateFill(b, resource)) {
			return b.fill(resource, doFill);
		}
		return 0;
	}
	
	@Override
	@Nullable
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (a.canDrainFluidType(resource)) {
			return a.drain(resource, doDrain);
		}
		
		if (b.canDrainFluidType(resource)) {
			return b.drain(resource, doDrain);
		}
		
		return null;
	}
	
	@Override
	@Nullable
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (a.canDrain()) {
			return a.drain(maxDrain, doDrain);
		}
		
		if (b.canDrain()) {
			return b.drain(maxDrain, doDrain);
		}
		
		return null;
	}
	
	private static boolean validateFill(FluidTank tank, FluidStack stack) {
		if (tank instanceof ConcreteFluidTank) {
			return
					tank.canFill() &&
					tank.canFillFluidType(stack) &&
					((ConcreteFluidTank)tank).getFillValidator().test(stack);
		} else {
			return tank.canFill() && tank.canFillFluidType(stack);
		}
	}
}
