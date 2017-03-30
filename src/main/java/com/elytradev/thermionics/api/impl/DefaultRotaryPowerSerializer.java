package com.elytradev.thermionics.api.impl;

import com.elytradev.thermionics.api.IRotaryPower;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DefaultRotaryPowerSerializer implements Capability.IStorage<IRotaryPower> {

	@Override
	public NBTBase writeNBT(Capability<IRotaryPower> capability, IRotaryPower instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("velocityBuffer", instance.getBufferedVelocity());
		tag.setInteger("velocityActual", instance.getActualVelocity());
		tag.setInteger("torque", instance.getRequiredTorque());
		tag.setBoolean("locked", instance.isLocked());
		return tag;
	}

	@Override
	public void readNBT(Capability<IRotaryPower> capability, IRotaryPower instance, EnumFacing side, NBTBase nbt) {
		if ((nbt instanceof NBTTagCompound) && (instance instanceof RotaryPower)) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			RotaryPower storage = (RotaryPower)instance;
			storage.setBufferedVelocity(tag.getInteger("velocityBuffer"));
			storage.setActualVelocity(tag.getInteger("velocityActual"));
			storage.setTorque(tag.getInteger("torque"));
			storage.setIsLocked(tag.getBoolean("locked"));
		}
	}

}
