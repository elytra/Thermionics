/*
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

package io.github.elytra.thermionics.api.impl;

import io.github.elytra.thermionics.api.IHeatStorage;

/**
 * Safe, stripped-down view of an underlying IHeatStorage.
 */

public class HeatStorageView implements IHeatStorage {
	private final IHeatStorage target;
	private final boolean canReceive;
	private final boolean canExtract;
	
	private HeatStorageView(IHeatStorage target, boolean receive, boolean extract) {
		this.target = target;
		this.canReceive = receive;
		this.canExtract = extract;
	}
	
	public static HeatStorageView of(IHeatStorage target) {
		return new HeatStorageView(target, true, true);
	}
	
	public static HeatStorageView extractOnlyOf(IHeatStorage target) {
		return new HeatStorageView(target, false, true);
	}
	
	public static HeatStorageView insertOnlyOf(IHeatStorage target) {
		return new HeatStorageView(target, true, false);
	}
	
	public static HeatStorageView readOnlyOf(IHeatStorage target) {
		return new HeatStorageView(target, false, false);
	}
	
	@Override
	public int getHeatStored() {
		return target.getHeatStored();
	}

	@Override
	public int getMaxHeatStored() {
		return target.getMaxHeatStored();
	}

	@Override
	public boolean canReceiveHeat() {
		return canReceive && target.canReceiveHeat();
	}

	@Override
	public boolean canExtractHeat() {
		return canExtract && target.canExtractHeat();
	}

	@Override
	public int receiveHeat(int amount, boolean simulate) {
		if (!canReceive) return 0;
		return target.receiveHeat(amount, simulate);
	}

	@Override
	public int extractHeat(int amount, boolean simulate) {
		if (!canExtract) return 0;
		return target.extractHeat(amount, simulate);
	}
	
}
