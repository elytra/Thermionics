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

import net.minecraftforge.energy.EnergyStorage;

/**
 * A version of forge energy that can be asked to automatically mark a TileEntity dirty when accessed
 */
public class ObservableEnergyStorage extends EnergyStorage {
	private ArrayList<Runnable> listeners = new ArrayList<>();

	public ObservableEnergyStorage(int capacity) {
		super(capacity);
	}
	
	public ObservableEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}
	
	public ObservableEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}

	private void markDirty() {
		for(Runnable r : listeners) {
			r.run();
		}
	}
	
	public void listen(@Nonnull Runnable r) {
		listeners.add(r);
	}
	
	/* Hook mutator methods only, to send events on a successful mutation */
	
	@Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
		int result = super.receiveEnergy(maxReceive, simulate);
		if (!simulate && result!=0) markDirty();
		return result;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
    	int result = super.extractEnergy(maxExtract, simulate);
    	if (!simulate && result!=0) markDirty();
    	return result;
    }
}
