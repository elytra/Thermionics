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
import com.elytradev.concrete.recipe.impl.InventoryGridRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class SergerRecipe implements ICustomRecipe<SergerRecipe, ItemStack> {
	protected ResourceLocation registryName;
	protected InventoryGridRecipe plan;
	protected float revolutions = 300;
	protected float torque = 8;
	
	public SergerRecipe(InventoryGridRecipe plan) {
		this.plan = plan;
	}
	
	public SergerRecipe(InventoryGridRecipe plan, float torque, float revolutions) {
		this.plan = plan;
		this.torque = torque;
		this.revolutions = revolutions;
	}
	
	@Override
	public SergerRecipe setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	@Override
	public ItemStack getOutput() {
		return plan.getOutput();
	}
	
	public ItemStack getOutput(IInventory inventory) {
		return plan.getOutput(inventory);
	}
	
	public ItemStack getOutput(IItemHandler inventory) {
		return plan.getOutput(inventory);
	}

	@Override
	public Class<SergerRecipe> getRegistryType() {
		return SergerRecipe.class;
	}
	
	public boolean matches(IItemHandler inventory) {
		return plan.matches(inventory);
	}
	
	public void consumeIngredients(IItemHandler inventory) {
		plan.consumeIngredients(inventory, true);
	}
	
	public float getRevolutions() { return revolutions; }
	public float getTorque() { return torque; }
}
