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
package com.elytradev.thermionics.api;

/**
 * Represents a block, entity, or item which initiates, transmits, or stores a redstone signal in extremely
 * finely-grained steps. This Capability does NOT cause a block to interact with vanilla redstone!
 */
public interface ISignalStorage {
	/** Get the current signal level in this block. This typically ranges from 0..15 inclusive, but mod blocks can add
	 * much stronger signal levels. */
	public float getSignal();
	
	/** Get the maximum signal level this block can accommodate. A redstone-analogue would report 15 even if it had a
	 * much longer transmission distance. */
	public float getMaxSignal();
	
	/**
	 * Reports whether or not this block will respond to vanilla signals. Imagine a redstone torch or block was placed
	 * next to this block. If it would report a signal in response, then it's not insulated.
	 * @return true if this block is sensitive/responsive to vanilla redstone blocks around it, otherwise false.
	 */
	public boolean isInsulated();
}
