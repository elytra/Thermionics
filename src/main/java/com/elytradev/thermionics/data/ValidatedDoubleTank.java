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
