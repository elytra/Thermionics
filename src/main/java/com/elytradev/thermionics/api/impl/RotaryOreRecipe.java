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

import com.elytradev.thermionics.api.IRotaryRecipe;

import net.minecraft.item.ItemStack;

public class RotaryOreRecipe implements IRotaryRecipe {
	private final String input;
	private final ItemStack output;
	private float torque;
	private float revolutions;
	
	public RotaryOreRecipe(String input, ItemStack output, float torque, float revolutions) {
		this.input = input;
		this.output = output;
		this.torque = torque;
		this.revolutions = revolutions;
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

	@Override
	public String toString() {
		return "{'input':"+input+", output:"+output+", torque:"+torque+" revolutions:"+revolutions+"}";
	}
	
	public String getInput() {
		return input;
	}
}
