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
import java.util.List;

import com.elytradev.thermionics.api.IRotaryRecipe;

import net.minecraft.item.ItemStack;

public class RotaryRecipe implements IRotaryRecipe {
	private final float torque;
	private final float revolutions;
	private final ItemStack input;
	private final ItemStack output;
	
	/**
	 * Creates a recipe requiring 10T of force and 300 revolutions to complete
	 * @param input the machine input
	 * @param output the result of this recipe
	 */
	public RotaryRecipe(ItemStack input, ItemStack output) {
		torque = 10;
		revolutions = 300;
		this.input = input;
		this.output = output;
	}
	
	public RotaryRecipe(ItemStack input, ItemStack output, float torque, float revolutions) {
		this.torque = torque;
		this.revolutions = revolutions;
		this.input = input;
		this.output = output;
	}
	
	
	@Override
	public float getRequiredTorque() {
		return torque;
	}

	@Override
	public float getRequiredRevolutions() {
		return revolutions;
	}

	@Override
	public boolean matches(ItemStack input) {
		return OreItems.matches(this.input, input);
	}
	
	
	@Override
	public ItemStack getOutput(ItemStack input) {
		return output;
	}

	public ItemStack getInput() {
		return input;
	}
}
