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

import java.util.ArrayList;
import java.util.List;

import com.elytradev.concrete.recipe.ICustomRecipe;
import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.InventoryGridRecipe;
import com.elytradev.concrete.recipe.impl.ItemStackIngredient;
import com.elytradev.concrete.recipe.impl.OreItemIngredient;
import com.elytradev.concrete.recipe.impl.ShapedInventoryRecipe;
import com.elytradev.concrete.recipe.impl.ShapelessInventoryRecipe;

import blue.endless.jankson.JsonObject;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class SergerRecipe implements IRecipeWrapper {
	//protected transient ResourceLocation registryName;
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
	
	/*
	@Override
	public SergerRecipe setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}*/

	//@Override
	public ItemStack getOutput() {
		return plan.getOutput();
	}
	
	public ItemStack getOutput(IInventory inventory) {
		return plan.getOutput(inventory);
	}
	
	public ItemStack getOutput(IItemHandler inventory) {
		return plan.getOutput(inventory);
	}
	/*
	@Override
	public Class<SergerRecipe> getRegistryType() {
		return SergerRecipe.class;
	}*/
	
	public boolean matches(IItemHandler inventory) {
		return plan.matches(inventory);
	}
	
	public void consumeIngredients(IItemHandler inventory) {
		plan.consumeIngredients(inventory, true);
	}
	
	public float getRevolutions() { return revolutions; }
	public float getTorque() { return torque; }
	
	@Override
	@Optional.Method(modid = "jei")
	public void getIngredients(IIngredients ingredients) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		
		if (this.plan instanceof InspectableShapedInventoryRecipe) {
			for(ItemIngredient ingredient : ((InspectableShapedInventoryRecipe)this.plan).getIngredients()) {
				ArrayList<ItemStack> items = new ArrayList<>();
				if (ingredient==null) {
					//Add nothing
				} else if (ingredient instanceof OreItemIngredient) {
					String ore = ((OreItemIngredient)ingredient).getKey();
					if (OreDictionary.doesOreNameExist(ore)) {
						items.addAll(OreDictionary.getOres(ore));
					} else {
						//TODO: Add error ingredient
					}
				} else if (ingredient instanceof ItemStackIngredient) {
					ItemStack proxy = ((ItemStackIngredient)ingredient).getItem().copy();
					if (proxy.getMetadata()==OreDictionary.WILDCARD_VALUE) proxy.setItemDamage(0);
					items.add(proxy);
				} else if (ingredient instanceof WildcardNBTIngredient) {
					items.add(((WildcardNBTIngredient)ingredient).getStack().copy());
				} else {
					//TODO: Add error ingredient
				}
				
				inputs.add(items);
			}
		} else {
			//TODO: This is an unexpected error condition but might still be worth adding 9 error ingredients for
		}
		
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, plan.getOutput().copy());
	}
	
	public static SergerRecipe fromJson(JsonObject json, String recipeName) {
		Float revolutions = json.get(Float.class, "revolutions"); if (revolutions==null) revolutions = 300f;
		Float torque = json.get(Float.class, "torque"); if (torque==null) torque = 8f;
		InventoryGridRecipe plan = null;
		if (json.containsKey("pattern")) {
			plan = MachineRecipes.shapedFromJson(json, recipeName, 3, 3);
			if (plan==null) return null;
		} else if (json.containsKey("ingredients")) {
			plan = MachineRecipes.shapelessFromJson(json, recipeName);
			if (plan==null) return null;
		} else {
			return null;
		}
		
		SergerRecipe recipe = new SergerRecipe(plan, torque, revolutions);
		return recipe;
	}
}
