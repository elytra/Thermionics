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

package com.elytradev.thermionics.tileentity;

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.ValidatedDoubleTank;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityPotStill extends TileEntityMachine implements ITickable, IContainerInventoryHolder {
	public static final int SLOT_PRECIPITATE        = 0;
	public static final int SLOT_FULL_BUCKET_IN     = 1;
	public static final int SLOT_EMPTY_BUCKET_OUT   = 2;
	public static final int SLOT_EMPTY_BUCKET_IN    = 3;
	public static final int SLOT_FULL_BUCKET_OUT    = 4;
	
	private ConcreteFluidTank inputTank = new ConcreteFluidTank(8000);
	private ConcreteFluidTank outputTank = new ConcreteFluidTank(8000);
	private ValidatedDoubleTank cap = new ValidatedDoubleTank(inputTank, outputTank);
	private boolean tanksLocked = false;
	private boolean lastTickPower = false;
	
	private ConcreteItemStorage itemStorage = new ConcreteItemStorage(5)
			.withValidators(Validators.NOTHING, Validators.FLUID_CONTAINERS, Validators.NOTHING, Validators.FLUID_CONTAINERS, Validators.NOTHING)
			.setCanExtract(0, true)
			.setCanExtract(1, false)
			.setCanExtract(2, true)
			.setCanExtract(3, false)
			.setCanExtract(4, true)
			.withName("tile.thermionics.machine.pot_still.name");
	
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
		itemStorage.setCanExtract(0, !locked);
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
		
		tagOut.setTag("locked", new NBTTagByte((byte) (tanksLocked ? 1 : 0)));
		
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
		
		if (tag.hasKey("locked")) {
			tanksLocked = tag.getByte("locked")!=0;
		}
	}

	@Override
	public void update() {
		boolean curTickPower = world.isBlockIndirectlyGettingPowered(pos)!=0;
		
		if (!tanksLocked) {
			if (curTickPower & !lastTickPower) {
				//Lock the tanks on a rising current edge.
				setTanksLocked(true);
			}
		} else {
			FluidStack in = inputTank.getFluid();
			if (in==null) {
				//Batch is done...?
				setTanksLocked(false);
			} else {
				//Find a recipe, let's go :D
				
				
			}
		}
		
		lastTickPower = curTickPower;
	}
	
	/** Returns 1mB of whatever distilling this fluid would produce. */
	public FluidStack exampleOutput(FluidStack in) {
		return null;
	}

	public ConcreteFluidTank getInputTank() {
		return inputTank;
	}
	
	public ConcreteFluidTank getOutputTank() {
		return outputTank;
	}

	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(itemStorage);
		if (world.isRemote) {
			return result;
		} else {
			return result
					.withField(0, heat::getHeatStored)
					.withField(1, heat::getMaxHeatStored);
		}
	}
}
