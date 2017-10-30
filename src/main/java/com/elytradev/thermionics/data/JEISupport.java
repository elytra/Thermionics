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

import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.api.impl.RotaryOreRecipe;
import com.elytradev.thermionics.api.impl.RotaryRecipe;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;

@JEIPlugin
public class JEISupport implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		
		
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		
		
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeCategories();
		
		registry.handleRecipes(IRotaryRecipe.class, new RotaryWrapperFactory(), "HammerMill");
		
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		
		
	}
	
	
	public static class HammerMillRecipeCategory implements IRecipeCategory<RotaryRecipeWrapper> {

		@Override
		public String getUid() {
			return "HammerMill";
		}

		@SuppressWarnings("deprecation")
		@Override
		public String getTitle() {
			return I18n.translateToLocal("tile.thermionics.machine.hammermill.name");
		}

		@Override
		public IDrawable getBackground() {
			
			return null;
		}

		@Override
		public IDrawable getIcon() {
			
			return null;
		}

		@Override
		public void drawExtras(Minecraft minecraft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<String> getTooltipStrings(int mouseX, int mouseY) {
			return new ArrayList<>();
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, RotaryRecipeWrapper recipeWrapper, IIngredients ingredients) {
			
		}

		@Override
		public String getModName() {
			return "thermionics";
		}
		
	}
	
	
	public static class RotaryWrapperFactory implements IRecipeWrapperFactory<IRotaryRecipe> {

		@Override
		public IRecipeWrapper getRecipeWrapper(IRotaryRecipe recipe) {
			return new RotaryRecipeWrapper(recipe);
		}
		
	}
	
	public static class RotaryRecipeWrapper implements IRecipeWrapper {
		private IRotaryRecipe recipe;
		
		public RotaryRecipeWrapper(IRotaryRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			if (recipe instanceof RotaryRecipe) {
				ingredients.setInput(ItemStack.class, ((RotaryRecipe)recipe).getInput());
			} else if (recipe instanceof RotaryOreRecipe) {
				String input = ((RotaryOreRecipe)recipe).getInput();
				List<ItemStack> slot = OreDictionary.getOres(input, false);
				ArrayList<List<ItemStack>> slots = new ArrayList<>();
				slots.add(slot);
				ingredients.setInputLists(ItemStack.class, slots);
			}
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		}

		@Override
		public List<String> getTooltipStrings(int mouseX, int mouseY) {
			return new ArrayList<String>();
		}

		@Override
		public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
			return false;
		}
		
	}
}
