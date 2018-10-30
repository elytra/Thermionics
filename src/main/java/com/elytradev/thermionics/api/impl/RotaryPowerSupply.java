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

import com.elytradev.thermionics.api.IRotaryPowerSupply;

public class RotaryPowerSupply implements IRotaryPowerSupply {
	public static final float TRANSMISSION_SPEED = 0.1f;
	private ArrayList<Runnable> listeners = new ArrayList<>();
	private int cap = 20;
	private float buffer = 0;
	private float torque = 20;
	
	public RotaryPowerSupply() {
		this.cap = 20;
	}

	@Override
	public void insertPower(float amount) {
		buffer += amount;
		if (buffer>cap) buffer=cap;
		markDirty();
	}

	@Override
	public float extractPower(float amount) {
		float toExtract = Math.min(amount, buffer);
		buffer -= toExtract;
		markDirty();
		return toExtract;
	}

	@Override
	public float getBufferedPower() {
		return buffer;
	}

	@Override
	public float getMaxBufferedPower() {
		return cap;
	}

	@Override
	public float getTorqueSetting() {
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
	public void setTorqueSetting(float setting) {
		torque = setting;
		markDirty();
	}
	
	/**
	 * 
	 */
	public void autoSetTorqueSetting(float load) {
		//System.out.println("Adjusting towards load "+load);
		if (load>torque) {
			//This could lock the machine at 0RPM forever if load >= cap. This is intended!
			if (torque!=cap) {
				setTorqueSetting(cap);
			}
			return;
		} else {
			if (torque>load) {
				float target = torque - TRANSMISSION_SPEED;
				if (target<load) target = load; //ACTUALLY prevent torque overshoot locking up the drive shaft.
				if (target<0) target = 0;
				
				setTorqueSetting(target);
				//if (torque<0) torque = 0;
				//if (torque<load) torque = load; //Prevent overshoot slamming us back up to cap
			} else {
				//We're running at optimal speed! ^_^
			}
		}
	}
}
