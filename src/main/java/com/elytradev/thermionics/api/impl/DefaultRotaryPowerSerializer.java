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
