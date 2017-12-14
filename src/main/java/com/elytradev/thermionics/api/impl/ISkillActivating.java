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

import com.elytradev.thermionics.api.IWeaponSkillInfo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * Represents a tool or weapon which can activate a WeaponSkill when attacking, or armor which can activate a
 * WeaponSkill when the wearer is hit.
 */
public interface ISkillActivating {
	/** Called *serverside* when a piece of equipment is used in a way that matches the weaponskill activation criteria:
	 *  <li> The shared-weaponskill-cooldown is inactive
	 *  <li> {@code attacker} was hit by an entity and their armor is ISkillActivating, or {@code attacker} attacked
	 *       (left-clicked) while an ISkillActivating was in their primary hand
	 * 
	 */
	public int activateSkill(IWeaponSkillInfo info, EntityLivingBase attacker, ItemStack item, DamageSource source, EntityLivingBase opponent);
}
