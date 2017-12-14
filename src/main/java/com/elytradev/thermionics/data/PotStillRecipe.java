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

import com.elytradev.concrete.recipe.FluidIngredient;
import com.elytradev.concrete.recipe.ICustomRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class PotStillRecipe implements ICustomRecipe<PotStillRecipe, FluidStack> {
	protected ResourceLocation registryName;
	protected FluidStack output;
	protected ItemStack outputItem;
	protected float itemChance = 0.0f;
	protected FluidIngredient recipe;
	
	public PotStillRecipe(FluidStack output, FluidIngredient input) {
		this.recipe = input;
		this.output = output;
	}
	
	
	@Override
	public PotStillRecipe setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	@Override
	public Class<PotStillRecipe> getRegistryType() {
		return PotStillRecipe.class;
	}

	@Override
	public FluidStack getOutput() {
		return output;
	}
	
	public boolean matches(FluidStack input) {
		return recipe.apply(input);
	}
	
	public boolean matches(FluidTank input) {
		if (input==null) return false;
		return recipe.apply(input.getFluid());
	}

	public boolean consumeIngredients(FluidTank input) {
		if (!recipe.apply(input.getFluid())) return false;
		return input.drain(recipe.getAmount(), true)!=null;
	}
	
	/* If there was any justice in the world this would be an extension method instead */
	private String fluidStackToString(FluidStack stack) {
		if (stack==null) return "null";
		if (stack.getFluid()==null) return "fluid_null";
		String tagString = "null";
		if (stack.tag != null) tagString = "{"+stack.tag.toString()+"}";
		return "{fluid:"+stack.getFluid().getName()+", tag:"+tagString+", amount:"+stack.amount+"}";
	}
	
	public String toString() {
		return "{input:"+recipe.toString()+", output:"+fluidStackToString(output)+"}";
	}
}
