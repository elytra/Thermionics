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

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ObservableFluidStorage extends FluidTank {
	private ArrayList<Runnable> listeners = new ArrayList<>();

	public ObservableFluidStorage(int capacity) {
		super(capacity);
		this.setCanFill(true);
		this.setCanDrain(true);
	}
	
	private void markDirty() {
		for(Runnable r : listeners) {
			r.run();
		}
	}
	/*
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (doFill) System.out.println("FILL "+resource);
		return super.fill(resource, doFill);
	}
	
	@Override
	@Nullable
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (doDrain) System.out.println("DRAIN "+resource);
		return super.drain(resource, doDrain);
	}*/
	
	public void listen(@Nonnull Runnable r) {
		listeners.add(r);
	}
	
	protected void onContentsChanged() {
		markDirty();
	}
}
