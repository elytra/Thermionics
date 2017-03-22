package com.elytradev.thermionics.api.impl;

import com.elytradev.thermionics.api.ISignalStorage;

public class SignalStorage implements ISignalStorage {
	private final float max;
	private final boolean insulated;
	private float signal;
	
	public SignalStorage() {
		this(16f, false);
	}
	
	public SignalStorage(float max, boolean insulated) {
		this.max = 16f;
		this.insulated = insulated;
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
		return this.signal;
	}
}
