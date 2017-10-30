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

package com.elytradev.thermionics.api.impl;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import com.elytradev.thermionics.api.ISignalStorage;

public class SignalStorage implements ISignalStorage {
	private final float max;
	private final boolean insulated;
	private float signal;
	private ArrayList<Runnable> listeners = new ArrayList<>();
	
	public SignalStorage() {
		this(16f, false);
	}
	
	public SignalStorage(float max, boolean insulated) {
		this.max = 16f;
		this.insulated = insulated;
	}
	
	private void markDirty() {
		for(Runnable listener : listeners) {
			listener.run();
		}
	}
	
	public void listen(@Nonnull Runnable r) {
		Validate.notNull(r);
		
		listeners.add(r);
	}
	
	@Override
	public float getSignal() {
		return signal;
	}

	@Override
	public float getMaxSignal() {
		return max;
	}

	@Override
	public boolean isInsulated() {
		return insulated;
	}
	
	/**
	 * Sets the signal level of this block
	 * @param signal the new level to set
	 * @return the level which was actually set. This may be less than the requested amount.
	 */
	public float setSignal(float signal) {
		this.signal = Math.min(signal, this.max);
		markDirty();
		return this.signal;
	}
}
