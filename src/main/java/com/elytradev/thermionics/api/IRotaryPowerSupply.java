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

package com.elytradev.thermionics.api;

/**
 * An IRotaryPowerSupply provides rotation to IRotaryPowerConsumers.
 * <ul>
 *   <li> Rotary power can travel along axle blocks, up to 16 blocks in a straight line. Axles can be pushed by pistons.
 *   <li> Gearboxes can be used to split or turn axles if you need to follow a non-straight line.
 *   <li> Any rotary device can place a torque load on the system. A device will halt if it does not receive enough torque.
 *   <li> If the power supply greatly exceeds the torque requirement of a rotary network, the motor's internal transmission will step down the torque to provide more speed
 *   <li> Gearboxes and axles are bookkeeping tiles that define network topology but do not participate directly in rotary operations.
 * </ul>
 * 
 * <p>Generally one shouldn't expect power to stay buffered from one tick to the next. A live motor is either rotating
 * its axle or it's locked up pushing against an insurmountable resistance. Work is still happening when the motor
 * pushes ineffectively; it's assumed that transmission fluid is moved and/or heat is diffused into the atmosphere in
 * that eventuality.
 * 
 * <p>The "P Meter" on the motor is inversely proportional to the difference between the transmission's torque stepping
 * and the torque load. So as the torque comes down (or up) towards the required load, the arrows fill.
 */
public interface IRotaryPowerSupply {
	/**
	 * Insert power into this supplier. Generally this is done only by the supplier itself or its deserializer, but
	 * certain kinds of motors can be placed in-series, and will accept buffer power from a side opposite their output
	 * face.
	 */
	public void insertPower(float amount);
	
	/**
	 * Extract power from this supplier. Generally this is done only by the supplier itself, when it supplies
	 * revolutions to consumers.
	 */
	public float extractPower(float amount);
	
	/**
	 * Gets the amount of power currently buffered.
	 */
	public float getBufferedPower();
	
	/**
	 * Gets the maximum power that can be buffered.
	 */
	public float getMaxBufferedPower();
	
	/**
	 * Gets the current transmission setting of this supplier
	 */
	public float getTorqueSetting();
}
