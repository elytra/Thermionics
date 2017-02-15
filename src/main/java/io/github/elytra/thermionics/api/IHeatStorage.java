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
package io.github.elytra.thermionics.api;

/**
 * Represents a device with some amount of specific heat. One "H" (H is for enthalpy, but you can call it heat to make
 * things simple) is equal to the energy released by burning one piece of coal for one tick. The access pattern roughly
 * follows what you'd expect for ForgeUnits.
 */
public interface IHeatStorage {
	
	/** Get the amount of heat energy stored in the subject */
	public int getHeatStored();
	
	/** Get the subject's specific heat. In other words, the total amount of heat energy the subject can hold. */
	public int getMaxHeatStored();
	
	/** Returns true if the subject allows heat energy to be added to it */
	public boolean canReceiveHeat();
	
	/** Returns true if the subject allows heat energy to be removed from it. */
	public boolean canExtractHeat();
	
	/**
	 * Try to store some heat energy into the subject.
	 * @param amount The amount of heat energy to store
	 * @param simulate if true, no heat will be added, and the return value will reflect what *would* happen
	 * @return the amount of heat energy accepted, or if simulate is true, the amount that would be accepted in a
	 * non-simulate call.
	 */
	public int receiveHeat(int amount, boolean simulate);
	
	/**
	 * Try to remove some heat energy from the subject.
	 * @param amount The amount of heat energy to remove
	 * @param simulate if true, no heat will be removed, and the return value will reflect what *would* happen
	 * @return the amount of heat energy removed, or if simulate is true, the amount that would be removed in a
	 * non-simulate call.
	 */
	public int extractHeat(int amount, boolean simulate);
}
