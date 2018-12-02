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

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.data.IMachineProgress;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.data.SergerRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntitySerger extends TileEntityMachine implements ITickable, IContainerInventoryHolder, IMachineProgress {
	private float revolutionsNeeded = 0f;
	private float revolutionsProcessed = 0f;
	//private int rpm = 0;
	private SergerRecipe lastRecipe = null;
	
	private RotaryPowerConsumer power = new RotaryPowerConsumer();
	private ConcreteItemStorage itemStorage = new ConcreteItemStorage(10)
			.withName("tile.thermionics.machine.serger.name")
			.withValidators(
					//Crafting Grid
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					//Output Slot
					Validators.NOTHING
					)
			.setCanExtract(0, false).setCanExtract(1, false).setCanExtract(2, false)
			.setCanExtract(3, false).setCanExtract(4, false).setCanExtract(5, false)
			.setCanExtract(6, false).setCanExtract(7, false).setCanExtract(8, false)
			.setCanExtract(9, true);
	
	public TileEntitySerger() {
		power.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
		
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ()->new ValidatedItemHandlerView(itemStorage));
		capabilities.registerForAllSides(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, ()->power);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("rotaryconsumer", Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.writeNBT(power, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));
		
		tagOut.setFloat("revolutionsprocessed", revolutionsProcessed);
		tagOut.setFloat("revolutionsneeded", revolutionsNeeded);
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("rotaryconsumer")) {
			Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.readNBT(power, null, tag.getTag("rotaryconsumer"));
		}
		if (tag.hasKey("inventory")) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStorage, null, tag.getTag("inventory"));
		}
		
		if (tag.hasKey("revolutionsprocessed")) revolutionsProcessed = tag.getInteger("revolutinsprocessed");
		if (tag.hasKey("revolutionsneeded")) revolutionsNeeded = tag.getInteger("revolutionsneeded");
	}
	
	@Override
	public void update() {
		if (world.isRemote) return;
		
		if (lastRecipe==null || !lastRecipe.matches(itemStorage)) {
			//Dump progress and pick a new recipe if possible
			power.clearRevolutions();
			revolutionsProcessed = 0;
			
			lastRecipe = MachineRecipes.getSerger(itemStorage);
			//lastRecipe = SergerRecipes.forInput(itemStorage);
			if (lastRecipe!=null) {
				
				revolutionsNeeded = lastRecipe.getRevolutions();
				power.setRequiredTorque(lastRecipe.getTorque());
				
				//System.out.println("recipe switched to "+lastRecipe);
				
			}
			this.markDirty();
		} else {
			//Continue processing current recipe if there's power
			float toConsume = power.getBufferedRevolutions();
			if (toConsume>2.5) toConsume = 2.5f;
			power.clearRevolutions();
			revolutionsProcessed += toConsume;
			//System.out.println("Working on recipe "+lastRecipe);
			if (revolutionsProcessed>=revolutionsNeeded) {
				revolutionsProcessed = revolutionsNeeded;
				power.setRequiredTorque(0);
				if (itemStorage.getStackInSlot(9).isEmpty()) {
					ItemStack output = lastRecipe.getOutput(itemStorage);
					lastRecipe.consumeIngredients(itemStorage);
					itemStorage.setStackInSlot(9, output.copy());
					//itemStorage.setStackInSlot(9, lastRecipe.performCraft(itemStorage).copy()); //Copy is unnecessary, but defensive as hell.
					revolutionsProcessed = 0;
					lastRecipe = null;
				}
			} else {
				power.setRequiredTorque(lastRecipe.getTorque());
			}
			this.markDirty();
		}
		
	}


	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(itemStorage);
		
		if (!this.world.isRemote) return result
				.withField(0, ()->(int)(this.getMachineProgress()*100))
				.withField(1, ()->100);
		
		return result;
	}


	@Override
	public float getMachineProgress() {
		//System.out.println("Progress: "+revolutionsProcessed+" / "+revolutionsNeeded+" -> "+ (revolutionsProcessed/revolutionsNeeded));
		return revolutionsProcessed / revolutionsNeeded;
		
	}
}
