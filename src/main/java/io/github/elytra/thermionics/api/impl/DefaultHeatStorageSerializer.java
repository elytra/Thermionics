package io.github.elytra.thermionics.api.impl;

import io.github.elytra.thermionics.api.IHeatStorage;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DefaultHeatStorageSerializer implements Capability.IStorage<IHeatStorage> {

	@Override
	public NBTBase writeNBT(Capability<IHeatStorage> capability, IHeatStorage instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("enthalpy", instance.getHeatStored());
		return tag;
	}

	@Override
	public void readNBT(Capability<IHeatStorage> capability, IHeatStorage instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
		
			int toReceive = tag.getInteger("enthalpy") - instance.getHeatStored();
			instance.receiveHeat(toReceive, false);
		}
	}
}
