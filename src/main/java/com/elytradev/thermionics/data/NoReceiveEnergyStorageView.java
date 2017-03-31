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

import net.minecraftforge.energy.IEnergyStorage;

public class NoReceiveEnergyStorageView implements IEnergyStorage, ITransferRate {
	private final IEnergyStorage delegate;
	
	public NoReceiveEnergyStorageView(IEnergyStorage delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return delegate.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return delegate.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return delegate.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return delegate.canExtract();
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public int getMaxTransfer() {
		if (delegate instanceof ITransferRate) return ((ITransferRate)delegate).getMaxTransfer();
		return delegate.getMaxEnergyStored();
	}

	@Override
	public float getCurTransfer() {
		if (delegate instanceof ITransferRate) return ((ITransferRate)delegate).getMaxTransfer();
		return 0;
	}
}
