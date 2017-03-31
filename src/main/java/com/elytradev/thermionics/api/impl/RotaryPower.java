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

import com.elytradev.thermionics.api.IRotaryPower;

public class RotaryPower implements IRotaryPower {
	private int drag = 5; //How quickly 
	private int maxTorque = 0;
	private int requiredTorque = 0;
	private int bufferedVelocity = 0;
	private int velocity = 0;
	private boolean producesTorque = false;
	private boolean locked = false;
	
	@Override
	public int getRequiredTorque() {
		return requiredTorque;
	}

	@Override
	public int getBufferedVelocity() {
		return bufferedVelocity;
	}
	
	@Override
	public int getActualVelocity() {
		return velocity;
	}
	
	@Override
	public boolean consumesRotaryPower() {
		return requiredTorque>0;
	}

	@Override
	public boolean producesRotaryPower() {
		return producesTorque;
	}

	@Override
	public void supplyPower(int torque, int velocity) {
		if (torque>requiredTorque) {
			bufferedVelocity = velocity;
		}
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	/** To be used internally to deserialize the capability data and for machine operation. */
	public void setBufferedVelocity(int v) {
		bufferedVelocity = v;
	}
	
	/** To be used internally to deserialize the capability data and for machine operation. */
	public void setActualVelocity(int v) {
		velocity = v;
	}
	
	public void setTorque(int t) {
		requiredTorque = t;
	}
	
	/** To be used internally to deserialize the capability data and for machine operation. */
	public void setIsLocked(boolean locked) {
		this.locked = locked;
	}
}
