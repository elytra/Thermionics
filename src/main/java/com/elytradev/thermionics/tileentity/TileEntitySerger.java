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
import com.elytradev.concrete.inventory.Validators;
import com.elytradev.thermionics.api.IRotaryGridRecipe;
import com.elytradev.thermionics.api.SergerRecipes;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;

import net.minecraft.util.ITickable;

public class TileEntitySerger extends TileEntityMachine implements ITickable {
	private float revolutionsNeeded = 0f;
	private float revolutionsProcessed = 0f;
	private int rpm = 0;
	private IRotaryGridRecipe lastRecipe = null;
	
	private RotaryPowerConsumer power = new RotaryPowerConsumer();
	private ConcreteItemStorage itemStorage = new ConcreteItemStorage(10)
			.withValidators(
					//Crafting Grid
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					Validators.ANYTHING, Validators.ANYTHING, Validators.ANYTHING,
					//Output Slot
					Validators.NOTHING
					);
	
	public TileEntitySerger() {
		power.listen(this::markDirty);
		itemStorage.listen(this::markDirty);
	}
	
	
	@Override
	public void update() {
		if (lastRecipe==null || !lastRecipe.matches(itemStorage)) {
			//Dump progress and pick a new recipe if possible
			power.clearRevolutions();
			revolutionsProcessed = 0;
			
			lastRecipe = SergerRecipes.forInput(itemStorage);
			if (lastRecipe!=null) {
				revolutionsNeeded = lastRecipe.getRequiredRevolutions();
				power.setRequiredTorque(lastRecipe.getRequiredTorque());
			}
		} else {
			//Continue processing current recipe if there's power
			float toConsume = power.getBufferedRevolutions();
			if (toConsume>2.5) toConsume = 2.5f;
			power.clearRevolutions();
			revolutionsProcessed += toConsume;
		}
		
	}
}
