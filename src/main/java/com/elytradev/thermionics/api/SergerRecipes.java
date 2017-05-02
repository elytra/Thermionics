package com.elytradev.thermionics.api;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraftforge.items.IItemHandler;

public class SergerRecipes {
	private static ArrayList<IRotaryGridRecipe> recipes = new ArrayList<>();
	
	public static void registerRecipe(IRotaryGridRecipe recipe) {
		recipes.add(recipe);
	}
	
	@Nullable
	public static IRotaryGridRecipe forInput(IItemHandler input) {
		for(IRotaryGridRecipe recipe : recipes) {
			if (recipe.matches(input)) {
				return recipe;
			}
		}
		return null;
	}
}
