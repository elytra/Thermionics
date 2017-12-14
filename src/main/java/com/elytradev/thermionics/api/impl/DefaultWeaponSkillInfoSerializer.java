package com.elytradev.thermionics.api.impl;

import java.util.Map;

import com.elytradev.thermionics.api.IWeaponSkillInfo;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public class DefaultWeaponSkillInfoSerializer implements Capability.IStorage<IWeaponSkillInfo> {
	private static final int TYPE_NBT_INT = 3;
	private static final int TYPE_NBT_COMPOUND = 10;
	
	@Override
	public NBTBase writeNBT(Capability<IWeaponSkillInfo> capability, IWeaponSkillInfo instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Cooldown",instance.getCooldown());
		tag.setInteger("MaxCooldown", instance.getMaxCooldown());
		NBTTagCompound map = new NBTTagCompound();
		for(Map.Entry<ResourceLocation, Integer> entry : instance.entrySet()) {
			map.setInteger(entry.getKey().toString(), entry.getValue());
		}
		tag.setTag("Resources", map);
		
		return tag;
	}

	@Override
	public void readNBT(Capability<IWeaponSkillInfo> capability, IWeaponSkillInfo instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			if (tag.hasKey("Cooldown",    TYPE_NBT_INT)) instance.setCooldown(tag.getInteger("Cooldown"));
			if (tag.hasKey("MaxCooldown", TYPE_NBT_INT)) instance.setMaxCooldown(tag.getInteger("MaxCoodlown"));
			if (tag.hasKey("Resources",   TYPE_NBT_COMPOUND)) {
				NBTTagCompound map = tag.getCompoundTag("Resources");
				for(String name : map.getKeySet()) {
					ResourceLocation id = new ResourceLocation(name);
					if (map.hasKey(name, TYPE_NBT_INT)) instance.set(id, map.getInteger(name));
				}
			}
		}
	}

}
