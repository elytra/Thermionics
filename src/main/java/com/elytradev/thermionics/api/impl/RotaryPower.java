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
