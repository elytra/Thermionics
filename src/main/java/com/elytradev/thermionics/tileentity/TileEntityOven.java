package com.elytradev.thermionics.tileentity;


import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.IMachineProgress;
import com.elytradev.thermionics.data.MachineItemStorageView;
import com.elytradev.thermionics.data.ObservableItemStorage;
import com.google.common.primitives.Ints;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityOven extends TileEntityMachine implements ITickable, IMachineProgress {
	private static final int SLOT_WORK = 2;
	private static final int H_PER_SMELT = 200;
	private static final int HEAT_EFFICIENCY = 2;
	
	private ObservableItemStorage itemStorage;
	private HeatStorage heatStorage;
	private int progress = 0;
	
	public TileEntityOven() {
		itemStorage = new ObservableItemStorage(3);
		heatStorage = new HeatStorage(600);
		
		itemStorage.listen(this::markDirty);
		heatStorage.listen(this::markDirty);
		
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->new MachineItemStorageView(itemStorage), ()->itemStorage);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.insertOnlyOf(heatStorage), ()->heatStorage);
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
			
			if (itemStorage.getStackInSlot(SLOT_WORK).isEmpty()) {
				//Invalid state! Kick us back into a valid state.
				progress = 0;
				this.markDirty();
				return;
			}
			
			//System.out.println("Smelt end step. workslot:"+itemStorage.getStackInSlot(SLOT_WORK)+" outputslot:"+itemStorage.getStackInSlot(MachineItemStorageView.SLOT_MACHINE_OUTPUT));
			
			ItemStack result = itemStorage.insertItem(MachineItemStorageView.SLOT_MACHINE_OUTPUT, itemStorage.getStackInSlot(SLOT_WORK), false);
			itemStorage.setStackInSlot(SLOT_WORK, result);
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
		
		if (itemStorage.getStackInSlot(MachineItemStorageView.SLOT_MACHINE_INPUT).isEmpty()) {
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
		if (itemStorage.getStackInSlot(SLOT_WORK).isEmpty()) {
			ItemStack input = itemStorage.extractItem(MachineItemStorageView.SLOT_MACHINE_INPUT, 1, false);
			if (input.isEmpty()) return;
			
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input).copy();
			itemStorage.setStackInSlot(SLOT_WORK, result);
		}
	}

}
