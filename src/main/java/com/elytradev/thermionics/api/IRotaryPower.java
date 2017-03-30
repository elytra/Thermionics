package com.elytradev.thermionics.api;

/**
 * An IRotaryPower gains some benefit from a rotating axle, or provides rotation to an axle.
 * <ul>
 * <li> Rotary power travels along non-tile-entity axle blocks, up to 16 blocks in a straight line. Axles can be pushed
 * by pistons.
 * <li> Any rotary device can place a torque load on the system. A device will halt if it does not receive enough torque.
 * <li> If the power supply greatly exceeds the torque requirement of a rotary network, devices can elect to run faster.
 * <li> Gearboxes have somewhat silent input faces and output faces. When they operate, they collect the power from all
 * their input faces, and measure the combined torque demand on their output faces.
 * </ul>
 * 
 * <p>It is recommended that rotary machines:
 * <ul>
 * <li> Set a maximum operating velocity, and make violation visible by emitting smoke and/or sparks.
 * <li> Severely overdriven machines may break. Repair on an anvil if possible.
 * <li> Allow extreme over-torque. If locked, the torque isn't enough to break the machine. If not locked, it's not
 * hurting the machine either. If you *must* break the machine, do it in the transition from extremely high torque to
 * lock, as this is when a real machine would break.
 * <li> Change their requiredTorque based on the item in the processing slot. Grinding basalt is harder than flowers!
 * <li> Use the kind of power that is most natural to its work. For melting, consider heat instead.
 * </ul>
 */
public interface IRotaryPower {
	/**
	 * Gets the minimum amount of torque that needs to be applied for this device to operate at all. Anything less will
	 * cause this device, and potentially the whole network, to lock up in a stationary position.
	 */
	public int getRequiredTorque();
	
	/**
	 * Gets the amount of power currently buffered - this bleeds away very quickly!
	 * @return
	 */
	int getBufferedVelocity();
	
	/**
	 * Gets the actual velocity the machine is operating at. Sometimes this isn't the same as the input velocity, for
	 * instance, when the machine has an internal flywheel.
	 */
	public int getActualVelocity();
	
	/**
	 * Returns true if this device is able to consume power.
	 */
	public boolean consumesRotaryPower();
	
	/**
	 * Returns true if this device is able to apply torque.
	 */
	public boolean producesRotaryPower();
	
	/**
	 * Notify the device that it is receiving a power packet. The recipient can choose to ignore the packet, or lock,
	 * but force is applied whether or not the endpoint is rotating. Generally this gets buffered for one tick only.
	 * 
	 * <p>The total power applied is <code>torque * velocity</code>
	 * 
	 * @param torque	amount of power devoted to lever force
	 * @param velocity	speed the host differential is tuned to
	 */
	public void supplyPower(int torque, int velocity);
	
	/**
	 * Is the device torque-locked? Generally returns true if getTorqueDemand > getBufferedTorque at the moment before
	 * the device has consumed its power packet, preventing it from performing work.
	 */
	public boolean isLocked();
}
