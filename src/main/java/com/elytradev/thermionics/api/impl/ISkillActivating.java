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
