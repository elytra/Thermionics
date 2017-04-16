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
package com.elytradev.thermionics.api.impl;

import java.util.ArrayList;

import com.elytradev.thermionics.api.IRotaryPowerSupply;

public class RotaryPowerSupply implements IRotaryPowerSupply {
	private ArrayList<Runnable> listeners = new ArrayList<>();
	private int cap = 20;
	private int buffer = 0;
	private int torque = 20;
	
	public RotaryPowerSupply() {
		this.cap = 20;
	}

	@Override
	public void insertPower(int amount) {
		buffer += amount;
		if (buffer>cap) buffer=cap;
		markDirty();
	}

	@Override
	public int extractPower(int amount) {
		int toExtract = Math.min(amount, buffer);
		buffer -= toExtract;
		markDirty();
		return toExtract;
	}

	@Override
	public int getBufferedPower() {
		return buffer;
	}

	@Override
	public int getMaxBufferedPower() {
		return cap;
	}

	@Override
	public int getTorqueSetting() {
		return torque;
	}
	
	/* Listener parts */
	
	public void listen(Runnable r) {
		listeners.add(r);
	}
	
	protected void markDirty() {
		for(Runnable r : listeners) r.run();
	}
	
	/* Administrative parts */
	
	/**
	 * Sets the transmission stepping
	 */
	public void setTorqueSetting(int setting) {
		torque = setting;
		markDirty();
	}
	
	/**
	 * 
	 */
	public void autoSetTorqueSetting(int load) {
		if (load>torque) {
			//This could lock the machine at 0RPM forever if load >= cap. This is intended!
			if (torque!=cap) {
				setTorqueSetting(cap);
			}
			return;
		} else {
			if (torque>load) {
				setTorqueSetting(torque-1);
			} else {
				//We're running at optimal speed! ^_^
			}
		}
	}
}
