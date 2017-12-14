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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.elytradev.thermionics.api.IWeaponSkillInfo;

import net.minecraft.util.ResourceLocation;

public class WeaponSkillInfo implements IWeaponSkillInfo {
	private HashMap<ResourceLocation, Integer> resources = new HashMap<>();
	private int maxCD = 0;
	private int cd = 0;
	
	@Override
	public int getResource(ResourceLocation id) {
		return (resources.containsKey(id)) ? resources.get(id) : 0;
	}
	
	@Override
	public int spend(ResourceLocation id, int amount) {
		int available = getResource(id);
		if (available<amount) return 0;
		int result = available-amount;
		resources.put(id, result);
		return result;
	}
	@Override
	public int getMaxCooldown() {
		return maxCD;
	}
	@Override
	public int getCooldown() {
		return cd;
	}

	@Override
	public Set<Entry<ResourceLocation, Integer>> entrySet() {
		return resources.entrySet();
	}

	@Override
	public void set(ResourceLocation id, int amount) {
		resources.put(id, amount);
	}

	@Override
	public void setMaxCooldown(int max) {
		this.maxCD = max;
	}

	@Override
	public void setCooldown(int cd) {
		this.cd = cd;
	}
}
