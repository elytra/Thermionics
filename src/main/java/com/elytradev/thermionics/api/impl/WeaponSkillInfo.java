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
