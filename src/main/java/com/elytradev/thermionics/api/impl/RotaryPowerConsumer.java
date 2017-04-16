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

import com.elytradev.thermionics.api.IRotaryPowerConsumer;

public class RotaryPowerConsumer implements IRotaryPowerConsumer {
	private ArrayList<Runnable> listeners = new ArrayList<>();
	private float torqueRequired = 0;
	private float bufferedRevolutions = 0;
	
	public RotaryPowerConsumer() {}
	
	@Override
	public float getRequiredTorque() {
		return torqueRequired;
	}

	@Override
	public void supplyRevolutions(float revolutions) {
		bufferedRevolutions += revolutions;
	}
	
	public void setRequiredTorque(float torque) {
		torqueRequired = torque;
	}
	
	public void clearRevolutions() {
		bufferedRevolutions = 0;
	}
	
	public float getBufferedRevolutions() {
		return bufferedRevolutions;
	}
	
	public void setBufferedRevolutions(float revolutions) {
		bufferedRevolutions = revolutions;
	}

	
	public void listen(Runnable r) {
		listeners.add(r);
	}
}
