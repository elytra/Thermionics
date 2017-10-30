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

import net.minecraft.item.ItemStack;

/**
 * A recipe for any rotary machine that takes one solid input and produces one solid output.
 */
public interface IRotaryRecipe {
	/**
	 * Gets the amount of wrench force that must be applied for any work to be done towards this recipe
	 */
	public float getRequiredTorque();
	
	/**
	 * Gets the number of revolutions (full, 360-degree turns of an axle) required to complete this recipe
	 */
	public float getRequiredRevolutions();
	
	/**
	 * Returns true if the provided input will trigger this recipe
	 */
	public boolean matches(ItemStack input);
	
	/**
	 * Gets the result of this recipe. DO NOT HAND OUT THE RESULT OF THIS RECIPE! Make a copy!
	 */
	public ItemStack getOutput(ItemStack input);
}
