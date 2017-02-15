/**
 * MIT License
 *
 * Copyright (c) 2017 The Elytra Team
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
import io.github.elytra.thermionics.data.ObservableItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityFirebox extends TileEntityMachine implements ITickable {
	private HeatStorage heatStorage;
	private ObservableItemHandler itemStorage;
	private int furnaceTicks = 0;
	
	public TileEntityFirebox() {
		heatStorage = new HeatStorage(200);
		itemStorage = new ObservableItemHandler(1);
		
		heatStorage.listen((it)->markDirty());
		itemStorage.listen((it)->markDirty());
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.of(heatStorage));
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->itemStorage);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("heatstorage", Thermionics.CAPABILITY_HEATSTORAGE.writeNBT(heatStorage, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));
		
		tagOut.setInteger("furnaceticks", furnaceTicks);
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("heatstorage")) {
			Thermionics.CAPABILITY_HEATSTORAGE.readNBT(heatStorage, null, tag.getTag("heatstorage"));
		}
		if (tag.hasKey("inventory")) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStorage, null, tag.getTag("inventory"));
		}
		
		if (tag.hasKey("furnaceticks")) furnaceTicks = tag.getInteger("furnaceticks");
	}
	
	@Override
	public void update() {
		if (world.isRemote) return; //don't run furnace ticks on the client
		if (furnaceTicks>0) {
			int ticksToConsume = Math.min(1, furnaceTicks);
			if (ticksToConsume<=0) return; //should never happen, but it pays to be prepared
			furnaceTicks -= Math.min(ticksToConsume, heatStorage.receiveHeat(ticksToConsume, false));
			this.markDirty();
		} else {
			ItemStack fuelItem = itemStorage.extractItem(0, 1, true);
			if (!fuelItem.isEmpty()) {
				int ticks = TileEntityFurnace.getItemBurnTime(fuelItem);
				if (ticks>0) {
					furnaceTicks+=ticks;
					itemStorage.extractItem(0, 1, false); //if we cared we could doublecheck against fuelItem here
					//but we can trust our own inventory. right? right????? (protip: we can't)
				}
			}
		}
	}
	
	
}
