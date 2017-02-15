/**
 * MIT License
 *
 * Copyright (c) 2017 The Isaac Ellingson (Falkreon) and contributors
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
package io.github.elytra.thermionics.tileentity;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.api.impl.HeatStorage;
import io.github.elytra.thermionics.api.impl.HeatStorageView;
import net.minecraft.util.ITickable;

public class TileEntityHeatStorage extends TileEntityMachine  implements ITickable {
	private HeatStorage heatStorage;
	
	public TileEntityHeatStorage() {
		heatStorage = new HeatStorage(200);
		
		heatStorage.listen((it)->markDirty());
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.of(heatStorage));
	}
	
	public TileEntityHeatStorage(int capacity) {
		heatStorage = new HeatStorage(capacity);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.of(heatStorage));
	}
	/*
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("heatStorage", Thermionics.CAPABILITY_HEATSTORAGE.getStorage().writeNBT(Thermionics.CAPABILITY_HEATSTORAGE, heatStorage, null));
		
		return super.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("heatStorage")) {
			NBTBase heatTag = tag.getTag("heatStorage");
			Thermionics.CAPABILITY_HEATSTORAGE.getStorage().readNBT(Thermionics.CAPABILITY_HEATSTORAGE, heatStorage, null, heatTag);
		}
	}*/

	@Override
	public void update() {
		//TODO: trigger diffusion
	}
}
