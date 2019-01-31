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

package com.elytradev.thermionics.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.ItemStackIngredient;
import com.elytradev.concrete.recipe.impl.OreItemIngredient;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.item.ThermionicsItems;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

public class RotaryRecipe implements IRotaryRecipe {
	private final float torque;
	private final float revolutions;
	private final ItemStack input;
	private final ItemStack output;
	
	/**
	 * Creates a recipe requiring 10T of force and 300 revolutions to complete
	 * @param input the machine input
	 * @param output the result of this recipe
	 */
	public RotaryRecipe(ItemStack input, ItemStack output) {
		torque = 10;
		revolutions = 300;
		this.input = input;
		this.output = output;
	}
	
	public RotaryRecipe(ItemStack input, ItemStack output, float torque, float revolutions) {
		this.torque = torque;
		this.revolutions = revolutions;
		this.input = input;
		this.output = output;
	}
	
	
	@Override
	public float getRequiredTorque() {
		return torque;
	}

	@Override
	public float getRequiredRevolutions() {
		return revolutions;
	}

	@Override
	public boolean matches(ItemStack input) {
		return OreItems.matches(this.input, input);
	}
	
	
	@Override
	public ItemStack getOutput(ItemStack input) {
		return output;
	}

	public ItemStack getInput() {
		return input;
	}

	@Override
	@Optional.Method(modid = "jei")
	public void getIngredients(IIngredients ingredients) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		
		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(input.copy());
		inputs.add(items);
		
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output.copy());
	}

	public static IRotaryRecipe fromJson(JsonObject jsonRecipe, String recipeName) {
		Float torque = jsonRecipe.get(Float.class, "torque");
		Float revolutions = jsonRecipe.get(Float.class, "revolutions");
		if (torque==null) torque = 10f;
		if (revolutions==null) revolutions = 300f;
		
		if (!jsonRecipe.containsKey("result")) {
			Thermionics.LOG.warn("Problem reading result of recipe \""+recipeName+"\": result was missing!");
			return null;
		}
		ItemStack output = MachineRecipes.itemStackFromJson(jsonRecipe.get("result"));
		if (output==null || output.isEmpty()) {
			Thermionics.LOG.warn("Problem reading result of recipe \""+recipeName+"\": can't create an item from "+jsonRecipe.get("result").toJson(false, false));
			return null;
		}
		
		JsonElement elem = jsonRecipe.get("ingredient");
		ItemIngredient ingredient = null;
		if (elem==null || elem instanceof JsonNull) {
			Thermionics.LOG.warn("Problem reading ingredient for recipe \""+recipeName+"\": ingredient was missing or null!");
			return null;
		} else if (elem instanceof JsonPrimitive) {
			ingredient = MachineRecipes.ingredientFromString(((JsonPrimitive)elem).asString());
		} else if (elem instanceof JsonObject) {
			ingredient = MachineRecipes.ingredientFromObject((JsonObject) elem);
		}
		
		if (ingredient==null) {
			Thermionics.LOG.warn("Problem reading ingredient for recipe \""+recipeName+"\": Couldn't build an ingredient from \""+((JsonPrimitive)elem).asString()+"\"");
			return null;
		}
		if (ingredient instanceof ItemStackIngredient) {
			ItemStack stack = ((ItemStackIngredient)ingredient).getItem();
			return new RotaryRecipe(stack, output, torque, revolutions);
		} else if (ingredient instanceof OreItemIngredient) {
			String key = ((OreItemIngredient)ingredient).getKey();
			return new RotaryOreRecipe(key, output, torque, revolutions);
		} else {
			Thermionics.LOG.warn("Problem reading ingredient for recipe \""+recipeName+"\": A strange internal error happened!");
			return null;
		}
	}
}
