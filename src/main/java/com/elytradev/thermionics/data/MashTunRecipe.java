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

package com.elytradev.thermionics.data;

import com.elytradev.concrete.recipe.ICustomRecipe;
import com.elytradev.concrete.recipe.ItemIngredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class MashTunRecipe implements ICustomRecipe<MashTunRecipe, FluidStack> {
	private ResourceLocation registryName;
	private ItemIngredient item;
	private int count;
	private int water;
	private FluidStack output;
	
	public MashTunRecipe(FluidStack output, int water, ItemIngredient item, int count) {
		this.output = output;
		this.water = water;
		this.item = item;
		this.count = count;
	}
	
	@Override
	public MashTunRecipe setRegistryName(ResourceLocation name) {
		registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<MashTunRecipe> getRegistryType() {
		return MashTunRecipe.class;
	}
	
	@Override
	public FluidStack getOutput() {
		return output;
	}
	
	/** Applies the recipe to the storage provided, determining whether or not the output should be produced. Optionally
	 * consumes the items.
	 */
	public boolean apply(FluidTank tank, IItemHandler inventory, boolean consume) {
		if (consume && !apply(tank, inventory, false)) return false; //Always dry-run before destructive ops
		if (tank.getFluid()==null) return false;
		//Next line shouldn't happen but it pays to plan for the impossible
		if (tank.getFluid().getFluid() != FluidRegistry.WATER) return false;
		if (tank.getFluidAmount()<water) return false;

		FluidStack fluidExtracted = tank.drainInternal(water, consume);
		if (fluidExtracted.amount<water) return false;
		
		int remaining = count;
		for(int i=0; i<inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (item.apply(stack)) {
				ItemStack extracted = inventory.extractItem(i, remaining, !consume);
				if (extracted.isEmpty()) continue;
				remaining -= extracted.getCount();
			}
		}
		return remaining<=0;
	}
	
	public boolean matches(FluidTank tank, IItemHandler inventory) {
		return apply(tank, inventory, false);
	}
	
	public void consumeIngredients(FluidTank tank, IItemHandler inventory) {
		apply(tank, inventory, true);
	}
	
	@Override
	public String toString() {
		return item.toString()+"->"+output.getLocalizedName();
	}
}
