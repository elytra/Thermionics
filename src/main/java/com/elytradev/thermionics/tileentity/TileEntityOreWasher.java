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
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.data.IMachineProgress;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidRegistry;

public class TileEntityOreWasher extends TileEntityMachine implements ITickable, IMachineProgress, IContainerInventoryHolder {
	
	protected ConcreteFluidTank waterTank = new ConcreteFluidTank(8000)
			.withFillValidator((it)->it.getFluid()==FluidRegistry.WATER);
	protected ConcreteFluidTank acidTank = new ConcreteFluidTank(8000)
			.withFillValidator(it->false);
	protected HeatStorage heat = new HeatStorage(1600);
	protected RotaryPowerConsumer rotaryPower = new RotaryPowerConsumer();
	protected ConcreteItemStorage itemStorage = new ConcreteItemStorage(5)
			.withName("tile.thermionics.machine.orewasher.name")
			.withValidators(
				Validators.ANYTHING, //Input - TODO: limit to gravels
				Validators.NOTHING,  //Flux Input - TODO: expand to flux kinds
				Validators.NOTHING,  //Work
				Validators.NOTHING,  //Output
				Validators.NOTHING   //Byproduct
			)
			.setCanExtract(3, true)
			.setCanExtract(4, true);
	
	//Slot 0: Gravel Input
	//Slot 1: Flux Input (unused for now)
	//Slot 2: Work Slot (untouchable)
	//Slot 3: Primary Output
	//Slot 4: Byproduct Output
	
	protected int revolutionsProcessed = 0;
	protected int revolutionsNeeded = 0;
	protected int rpm = 0;
	
	//TODO: track which recipe we're in the middle of?
	
	public TileEntityOreWasher() {
		acidTank.setCanDrain(true);
		
		waterTank.listen(this::markDirty);
		acidTank.listen(this::markDirty);
		heat.listen(this::markDirty);
		rotaryPower.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
	}

	@Override
	public void update() {
		
	}

	@Override
	public float getMachineProgress() {
		return 0;
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

	public ConcreteFluidTank getInputTank() {
		return waterTank;
	}
	
	public ConcreteFluidTank getOutputTank() {
		return acidTank;
	}
}
