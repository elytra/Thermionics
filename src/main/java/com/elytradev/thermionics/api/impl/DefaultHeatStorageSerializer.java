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

import com.elytradev.thermionics.api.IHeatStorage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DefaultHeatStorageSerializer implements Capability.IStorage<IHeatStorage> {

	@Override
	public NBTBase writeNBT(Capability<IHeatStorage> capability, IHeatStorage instance, EnumFacing side) {
		return new NBTTagInt(instance.getHeatStored());
		//NBTTagCompound tag = new NBTTagCompound();
		//tag.setInteger("enthalpy", instance.getHeatStored());
		//return tag;
	}

	@Override
	public void readNBT(Capability<IHeatStorage> capability, IHeatStorage instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
		
			int toReceive = tag.getInteger("enthalpy") - instance.getHeatStored();
			instance.receiveHeat(toReceive, false);
		}
		
		if (nbt instanceof NBTTagInt) {
			instance.receiveHeat(((NBTTagInt)nbt).getInt(), false);
		}
	}
}
