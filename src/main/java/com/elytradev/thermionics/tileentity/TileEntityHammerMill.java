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

import java.util.Collection;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.StandardMachineSlots;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.data.IMachineProgress;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityHammerMill extends TileEntityMachine implements ITickable, IMachineProgress, IContainerInventoryHolder {
	private RotaryPowerConsumer rotaryPower = new RotaryPowerConsumer();
	private ConcreteItemStorage itemStorage = new ConcreteItemStorage(3)
			.withName("tile.thermionics.machine.hammermill.name")
			.withValidators(Validators.ANYTHING, Validators.NOTHING, Validators.NOTHING)
			.setCanExtract(0, false)
			.setCanExtract(1, true)
			.setCanExtract(2, false);
	
	private float revolutionsNeeded = 0f;
	private float revolutionsProcessed = 0f;
	private int rpm = 0;
	private IRotaryRecipe lastRecipe = null;
	
	public TileEntityHammerMill() {
		rotaryPower.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
		
		capabilities.registerForAllSides(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				()->new ValidatedItemHandlerView(itemStorage),
				()->itemStorage);
		capabilities.registerForAllSides(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER,
				()->rotaryPower);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagIn) {
		NBTTagCompound tagOut = super.writeToNBT(tagIn);
		
		tagOut.setTag("rotaryconsumer", Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.writeNBT(rotaryPower, null));
		tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));
		
		tagOut.setFloat("revolutionsprocessed", revolutionsProcessed);
		tagOut.setFloat("revolutionsneeded", revolutionsNeeded);
		
		return tagOut;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("rotaryconsumer")) {
			Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER.readNBT(rotaryPower, null, tag.getTag("rotaryconsumer"));
		}
		if (tag.hasKey("inventory")) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStorage, null, tag.getTag("inventory"));
		}
		
		if (tag.hasKey("revolutionsprocessed")) revolutionsProcessed = tag.getInteger("revolutinsprocessed");
		if (tag.hasKey("revolutionsneeded")) revolutionsNeeded = tag.getInteger("revolutionsneeded");
	}
	
	
	@Override
	public void update() {
		float existingRequirement = rotaryPower.getRequiredTorque();
		
		
		IRotaryRecipe curRecipe = HammerMillRecipes.forInput(itemStorage.getStackInSlot(StandardMachineSlots.INPUT));
		if (curRecipe!=lastRecipe) {
			revolutionsProcessed = 0;
			lastRecipe = curRecipe;
			this.markDirty();
		}
		float newRequirement = 0;
		if (lastRecipe!=null) {
			revolutionsNeeded = lastRecipe.getRequiredRevolutions();
			//rotaryPower.setRequiredTorque(curRecipe.getRequiredTorque());
			newRequirement = lastRecipe.getRequiredTorque();
		}
		if (existingRequirement<newRequirement) {
			//Unfortunately, we need to dump any rotary power delivered prior to this moment
			rotaryPower.clearRevolutions();
			rotaryPower.setRequiredTorque(newRequirement);
			this.markActive(false);
			return;
		} else {
			//Revolutions delivered may exceed the required torque, which is completely fine.
			rotaryPower.setRequiredTorque(newRequirement);
		}
		
		float newRevolutions = rotaryPower.getBufferedRevolutions();
		rotaryPower.clearRevolutions();
		if (newRevolutions>2.5f) newRevolutions = 2.5f; //Hard limit on 3000RPM
		rpm = (int)(newRevolutions * 20 * 60);
		
		//Try to shove the work slot into the output slot
		if (!itemStorage.getStackInSlot(StandardMachineSlots.WORK).isEmpty()) {
			ItemStack result = itemStorage.insertItem(StandardMachineSlots.OUTPUT, itemStorage.getStackInSlot(StandardMachineSlots.WORK), false);
			itemStorage.setStackInSlot(StandardMachineSlots.WORK, result);
			
			if (result==null || result.isEmpty()) {
				//Good! Kill our progress. We can resume work next tick
				revolutionsProcessed = 0;
				this.markDirty();
				this.markActive(false);
			} else {
				//No further processing can be done.
				return;
			}
		}
		
		
		
		if (lastRecipe!=null) {
			//Get some processing done!
			markActive(true);
			revolutionsProcessed += newRevolutions;
			if (revolutionsProcessed>revolutionsNeeded) {
				if (itemStorage.getStackInSlot(StandardMachineSlots.WORK).isEmpty()) {
					ItemStack cur = itemStorage.extractItem(StandardMachineSlots.INPUT, 1, false);
					if (cur.isEmpty()) {
						//BAIL
						return;
					} else {
						itemStorage.setStackInSlot(StandardMachineSlots.WORK, lastRecipe.getOutput(cur).copy());
					}
					
				}
			}
			this.markDirty();
		}
	}

	@Override
	public float getMachineProgress() {
		if (revolutionsNeeded==0f) return 0f;
		return revolutionsProcessed / revolutionsNeeded;
	}

	@Override
	public IInventory getContainerInventory() {
		ValidatedInventoryView result = new ValidatedInventoryView(itemStorage);
		
		if (!this.world.isRemote) return result
				.withField(0, ()->(int)revolutionsProcessed)
				.withField(1, ()->(int)revolutionsNeeded)
				.withField(2, ()->rpm);
		
		return result;
	}
}
