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
import com.elytradev.concrete.inventory.ValidatedFluidTankWrapper;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.data.MashTunRecipe;
import com.elytradev.thermionics.data.ValidatedDoubleTank;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityMashTun extends TileEntityMachine implements ITickable, IContainerInventoryHolder {
	public static final int MAX_COOLDOWN = 20*5; //5 seconds per mash
	public static final int HEAT_COST = 200; //1 coal = 8 operations, just like a furnace
	
	protected int cooldown;
	
	protected ConcreteFluidTank inputTank = new ConcreteFluidTank(8000)
			.withFillValidator((it)->it.getFluid()==FluidRegistry.WATER);
	protected ConcreteFluidTank outputTank = new ConcreteFluidTank(8000)
			.withFillValidator(Validators.NO_FLUID);
	protected ValidatedDoubleTank cap = new ValidatedDoubleTank(inputTank, outputTank);
	
	protected ConcreteItemStorage items = new ConcreteItemStorage(3)
			.withValidators(Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING)
			.setCanExtract(0, true)
			.setCanExtract(1, true)
			.setCanExtract(2, true)
			.withName("tile.thermionics.machine.mash_tun.name");
	
	protected HeatStorage heat = new HeatStorage(1600);
	
	
	public TileEntityMashTun() {
		inputTank.setCanDrain(false);
		outputTank.setCanDrain(true);
		
		inputTank.listen(this::markDirty);
		outputTank.listen(this::markDirty);
		heat.listen(this::markDirty);
		items.listen(this::markDirty);
		
		capabilities.registerForAllSides(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ()->cap);
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->new ValidatedItemHandlerView(items));
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.insertOnlyOf(heat));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		tagOut.setTag("inputtank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(inputTank, null));
		tagOut.setTag("outputtank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(outputTank, null));
		tagOut.setTag("heatstorage", Thermionics.CAPABILITY_HEATSTORAGE.writeNBT(heat, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(items, null));
		
		tagOut.setTag("cooldown", new NBTTagInt(cooldown));
		
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
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(items, null, tag.getTag("inventory"));
		}
		
		if (tag.hasKey("cooldown")) {
			cooldown = tag.getInteger("cooldown");
		}
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		
		if (cooldown>0) cooldown--;
		if (cooldown>0) {
			this.markDirty();
			return;
		}
		
		MashTunRecipe recipe = MachineRecipes.getMashTun(inputTank, items);
		if (recipe!=null) {
			System.out.println("Recipe: "+recipe.toString());
			
			//Preflight any conditions we didn't verify in the recipe, like whether there's room for output
			int accepted = outputTank.fillInternal(recipe.getOutput(), false);
			System.out.println("Accepted:"+accepted+"/"+recipe.getOutput().amount);
			if (accepted<recipe.getOutput().amount) return;
			System.out.println("Stored:"+heat.getHeatStored()+"/"+HEAT_COST);
			if (heat.getHeatStored()<HEAT_COST) return;
			
			System.out.println("Applying...");
			
			//Apply the recipe
			recipe.consumeIngredients(inputTank, items);
			outputTank.fillInternal(recipe.getOutput(), true);
			cooldown = MAX_COOLDOWN;
		} else {
			System.out.println("No recipe available.");
			cooldown = MAX_COOLDOWN;
		}
	}
	
	public ConcreteFluidTank getInputTank() {
		return inputTank;
	}
	
	public ConcreteFluidTank getOutputTank() {
		return outputTank;
	}

	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(items);
		if (world.isRemote) {
			return result;
		} else {
			return result
					.withField(0, heat::getHeatStored)
					.withField(1, heat::getMaxHeatStored);
		}
	}
}
