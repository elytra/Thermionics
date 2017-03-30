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
package com.elytradev.thermionics.tileentity;

import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.IMachineProgress;
import com.elytradev.thermionics.data.MachineItemStorageView;
import com.elytradev.thermionics.data.ObservableItemStorage;
import com.elytradev.thermionics.transport.HeatTransport;
import com.elytradev.thermionics.Thermionics;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityFirebox extends TileEntityMachine implements ITickable, IMachineProgress {
	public static final int HEAT_EFFICIENCY = 1;
	
	private HeatStorage heatStorage;
	private ObservableItemStorage itemStorage;
	private int furnaceTicks = 0;
	private int maxFurnaceTicks = 0;
	private static final int MAX_COLD = 20;
	private int timeCold = MAX_COLD;
	
	public TileEntityFirebox() {
		heatStorage = new HeatStorage(200);
		itemStorage = new ObservableItemStorage(2);
		
		heatStorage.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->HeatStorageView.extractOnlyOf(heatStorage), ()->heatStorage);
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				()->new MachineItemStorageView(itemStorage),    ()->itemStorage);
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
		
		if (timeCold<MAX_COLD) timeCold++;
		
		//System.out.println("Furnace ticks @"+pos+": "+furnaceTicks);
		
		if (furnaceTicks>0) {
			timeCold = 0;
			int ticksToConsume = Math.min(HEAT_EFFICIENCY, furnaceTicks); //Consume up to HEAT_EFFICIENCY ticks of furnace time
			if (ticksToConsume<=0) return; //should never happen, but it pays to be prepared
			int consumed = heatStorage.receiveHeat(ticksToConsume, false);
			furnaceTicks -= consumed;
			//System.out.println("Consumed "+consumed+" furnace ticks, receiving "+consumed+"H ("+furnaceTicks+" remaining)");
			
			
			this.markDirty();
		} else {
			ItemStack fuelItem = itemStorage.extractItem(0, 1, true);
			if (!fuelItem.isEmpty()) {
				int ticks = TileEntityFurnace.getItemBurnTime(fuelItem);
				if (ticks>0) {
					furnaceTicks+=ticks;
					maxFurnaceTicks = furnaceTicks;

					
					ItemStack stack = itemStorage.extractItem(0, 1, false); //if we cared we could doublecheck against fuelItem here
					//but we can trust our own inventory. right? right????? (protip: we can't)
					if (stack.getItem().hasContainerItem(stack)) {
						ItemStack result = stack.getItem().getContainerItem(stack);
						if (result!=null && !result.isEmpty()) {
							this.itemStorage.setStackInSlot(1, result);
						}
					}
					this.markDirty(); //To be doubly or triply certain that furnaceTicks is updated.
				}
			}
		}
		
		HeatTransport.diffuse(world, pos, heatStorage);
		
		super.markActive(timeCold<MAX_COLD);
	}

	@Override
	public float getMachineProgress() {
		if (maxFurnaceTicks==0) maxFurnaceTicks = 1;
		if (furnaceTicks>maxFurnaceTicks) maxFurnaceTicks = furnaceTicks; //Typically happens because of a cold boot, maxFurnaceTicks isn't serialized since it doesn't affect the system
		float progress = 1 - (furnaceTicks / (float)maxFurnaceTicks);
		return progress;
	}
}
