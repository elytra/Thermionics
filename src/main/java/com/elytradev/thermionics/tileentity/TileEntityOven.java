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


import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.StandardMachineSlots;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.IMachineProgress;
import com.google.common.primitives.Ints;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityOven extends TileEntityMachine implements ITickable, IMachineProgress, IContainerInventoryHolder {
	private static final int H_PER_SMELT = 200;
	private static final int HEAT_EFFICIENCY = 2;
	
	private ConcreteItemStorage itemStorage;
	private HeatStorage heatStorage;
	private int progress = 0;
	
	public TileEntityOven() {
		heatStorage = new HeatStorage(600);
		itemStorage = new ConcreteItemStorage(3)
				.withName("tile.thermionics.machine.oven.name")
				.withValidators(Validators.SMELTABLE, Validators.NOTHING, Validators.NOTHING)
				.setCanExtract(0, false)
				.setCanExtract(1, true)
				.setCanExtract(2, false);
		
		itemStorage.listen(this::markDirty);
		heatStorage.listen(this::markDirty);
		
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				()->new ValidatedItemHandlerView(itemStorage), ()->itemStorage);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->HeatStorageView.insertOnlyOf(heatStorage), ()->heatStorage);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("heatstorage", Thermionics.CAPABILITY_HEATSTORAGE.writeNBT(heatStorage, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));
		
		tagOut.setInteger("progress", progress);
		
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
		
		if (tag.hasKey("progress")) progress = tag.getInteger("progress");
	}
	
	@Override
	public void update() {
		if (progress>=H_PER_SMELT) {
			//We're finished. Don't do *anything* until we can kick the item into the output slot.
			//Note: By now the "work-slot" has always already been replaced by a smelting result.
			
			if (itemStorage.getStackInSlot(StandardMachineSlots.WORK).isEmpty()) {
				//Invalid state! Kick us back into a valid state.
				progress = 0;
				this.markDirty();
				return;
			}
			
			//System.out.println("Smelt end step. workslot:"+itemStorage.getStackInSlot(SLOT_WORK)+" outputslot:"+itemStorage.getStackInSlot(MachineItemStorageView.SLOT_MACHINE_OUTPUT));
			
			ItemStack result = itemStorage.insertItem(StandardMachineSlots.OUTPUT, itemStorage.getStackInSlot(StandardMachineSlots.WORK), false);
			itemStorage.setStackInSlot(StandardMachineSlots.WORK, result);
			//System.out.println("End End step. workslot:"+itemStorage.getStackInSlot(SLOT_WORK)+" outputslot:"+itemStorage.getStackInSlot(MachineItemStorageView.SLOT_MACHINE_OUTPUT));
			
			if (result==null || result.isEmpty()) {
				//Good! Kill our progress. We can resume work next tick
				progress = 0;
				this.markDirty();
				this.markActive(false);
			} else {
				//We're still stalled :(
				
				//System.out.println("Continue stall.");
			}
			return;
		}
		
		int heatNeeded = H_PER_SMELT - progress;
		
		int heatAvailable = heatStorage.getHeatStored();
		int heatToConsume = Ints.min(heatAvailable, HEAT_EFFICIENCY, heatNeeded);
		
		if (itemStorage.getStackInSlot(StandardMachineSlots.INPUT).isEmpty()) {
			if (progress>0) {
				progress = 0;
				markDirty();
			}
			return;
		}
		
		if (heatToConsume<=0) {
			//Backpedal progress
			if (progress>0) {
				progress--;
				markDirty();
			}
		} else {
			//Apply progress
			progress += heatStorage.extractHeat(heatToConsume, false);
			markActive(true);
			if (progress>=H_PER_SMELT) {
				doSmelt();
			}
		}
	}

	@Override
	public float getMachineProgress() {
		return progress/(float)H_PER_SMELT;
	}
	
	private void doSmelt() {
		if (itemStorage.getStackInSlot(StandardMachineSlots.WORK).isEmpty()) {
			ItemStack input = itemStorage.extractItem(StandardMachineSlots.INPUT, 1, false);
			if (input.isEmpty()) return;
			
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input).copy();
			itemStorage.setStackInSlot(StandardMachineSlots.WORK, result);
		}
	}
	
	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(itemStorage);
		
		if (!this.world.isRemote) return result
				.withField(0, ()->(int)(this.getMachineProgress()*100))
				.withField(1, ()->100)
				.withField(2, heatStorage::getHeatStored)
				.withField(3, heatStorage::getMaxHeatStored);
		
		return result;
	}

	/*
	public static boolean validateItemInsert(int slot, ItemStack item) {
		if (slot==MachineItemStorageView.SLOT_MACHINE_INPUT) {
			return !FurnaceRecipes.instance().getSmeltingResult(item).isEmpty();
		} else {
			return true;
		}
	}*/
}
