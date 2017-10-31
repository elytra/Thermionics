package com.elytradev.thermionics.tileentity;

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.ValidatedDoubleTank;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityPotStill extends TileEntityMachine {
	private ConcreteFluidTank inputTank = new ConcreteFluidTank(8000);
	private ConcreteFluidTank outputTank = new ConcreteFluidTank(8000);
	private ValidatedDoubleTank cap = new ValidatedDoubleTank(inputTank, outputTank);
	private boolean tanksLocked;
	
	private ConcreteItemStorage itemStorage = new ConcreteItemStorage(1);
	
	private HeatStorage heat = new HeatStorage(8000);
	
	public TileEntityPotStill() {
		inputTank.listen(this::markDirty);
		outputTank.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
		heat.listen(this::markDirty);
		
		//Always
		inputTank.setCanDrain(false);
		outputTank.setCanFill(false);
		
		//Most of the time
		setTanksLocked(false);
		
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->new ValidatedItemHandlerView(itemStorage), ()->itemStorage);
		capabilities.registerForAllSides(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ()->cap);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.insertOnlyOf(heat));
	}
	
	public void setTanksLocked(boolean locked) {
		inputTank.setCanFill(!locked);
		outputTank.setCanDrain(!locked);
		tanksLocked = locked;
	}
	
	public boolean areTanksLocked() {
		return tanksLocked;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		tagOut.setTag("inputtank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(inputTank, null));
		tagOut.setTag("outputtank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(outputTank, null));
		tagOut.setTag("heatstorage", Thermionics.CAPABILITY_HEATSTORAGE.writeNBT(heat, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));
		
		//tagOut.setInteger("progress", progress);
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("inputtank")) {
			CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(inputTank, null, tag.getTag("inputtank"));
		}
		if (tag.hasKey("outputtank")) {
			CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(outputTank, null, tag.getTag("outputtank"));
		}
		if (tag.hasKey("heatstorage")) {
			Thermionics.CAPABILITY_HEATSTORAGE.readNBT(heat, null, tag.getTag("heatstorage"));
		}
		if (tag.hasKey("inventory")) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStorage, null, tag.getTag("inventory"));
		}
		
		//if (tag.hasKey("progress")) progress = tag.getInteger("progress");
	}
}
