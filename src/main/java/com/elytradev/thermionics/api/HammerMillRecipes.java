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

package com.elytradev.thermionics.api;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public class HammerMillRecipes {
	private static ArrayList<IRotaryRecipe> recipes = new ArrayList<>();
	
	public static void registerRecipe(IRotaryRecipe recipe) {
		recipes.add(0, recipe);
	}
	
	@Nullable
	public static IRotaryRecipe forInput(ItemStack input) {
		for(IRotaryRecipe recipe : recipes) {
			if (recipe.matches(input)) {
				//System.out.println(recipe);
				return recipe;
			}
		}
		return null;
	}

	public static Collection<IRotaryRecipe> all() {
		return recipes;
	}
}
