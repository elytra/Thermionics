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

package com.elytradev.thermionics.compat;

/*
 * MIT License
 *
 * Copyright (c) 2018 Isaac Ellingson (Falkreon) and contributors
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

import java.util.ArrayList;
import java.util.Locale;

import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.gui.ContainerHammerMill;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.gui.elements.DrawableResource;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
@JEIPlugin
public class JEICompat implements IModPlugin {
	
	@Override
	public void register(IModRegistry registry) {
		/*
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DRAIN_LIFE.ordinal()),
				ItemStack.class,
				spellInfo(
						"drainLife",
						EnumElement.UNDEATH, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalBlocks.RUNIC_ALTAR),
				ItemStack.class,
				"info.magicarsenal.altar"
				);
		
		*/
		System.out.println("Registering HammerMill with JEI. "+MachineRecipes.allHammerMill().size()+" recipes found.");
		registry.addRecipeCatalyst(new ItemStack(ThermionicsBlocks.HAMMER_MILL), "thermionics:hammer_mill");
		registry.addRecipes(MachineRecipes.allHammerMill(), "thermionics:hammer_mill");
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerHammerMill.class, "thermionics:hammer_mill", 0, 1, 2, 36);
		registry.addRecipeClickArea(ConcreteGui.class, 18*4, 18*1, 18, 18, "thermionics:hammer_mill");
		
	}
	
	/*
	private static String[] spellInfo(String spell, EnumElement elem, EnumElement elem2, ResourceLocation resource) {
		ArrayList<String> result = new ArrayList<>();
		result.add(format("info.magicarsenal.label.spell", local("spell.magicarsenal."+spell)));
		result.add(format("info.magicarsenal.label.elements", element(elem), element(elem2)));
		if (resource!=null) {
			result.add(format("info.magicarsenal.label.resource", resource(resource)));
		}
		
		String effectText = local("spell.magicarsenal."+spell+".desc");
		if (effectText!=null && !effectText.equals("spell.magicarsenal."+spell+".desc")) {
			result.add("");
			result.add(format("info.magicarsenal.label.effect", effectText));
		}
		
		return result.toArray(new String[result.size()]);
	}*/
	
	private static String local(String key) {
		return I18n.translateToLocal(key);
	}
	
	private static String format(String key, Object... args) {
		return I18n.translateToLocalFormatted(key, args);
	}
	
	private static String resource(ResourceLocation resource) {
		String domain = resource.getNamespace();
		String resourceName = resource.getPath();
		return local("resource."+domain+"."+resourceName);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new IRecipeCategory<IRotaryRecipe>() {
			//protected int radiance;
			//protected int emc;
			protected float torque = 0;
			protected float revolutions = 0;
			
			@Override
			public String getUid() {
				return "thermionics:hammer_mill";
			}

			@Override
			public String getTitle() {
				return I18n.translateToLocal("tile.thermionics.machine.hammermill.name");
			}

			@Override
			public String getModName() {
				return "magicarsenal";
			}

			@Override
			public IDrawable getBackground() {
				return new DrawableResource(new ResourceLocation("thermionics", "textures/gui/hammermill.png"), 0, 0, 126, 72, 0, 0, 0, 0, 126, 72);
			}

			@Override
			public void setRecipe(IRecipeLayout recipeLayout, IRotaryRecipe recipeWrapper, IIngredients ingredients) {
				int leftMargin = 0;
				int topMargin = 18*1;
				recipeLayout.getItemStacks().init(0, true,  leftMargin + 18*2, topMargin + 18*0);
				recipeLayout.getItemStacks().init(1, false, leftMargin + 18*4, topMargin + 18*0);
				
				recipeLayout.getItemStacks().set(ingredients);
				
				
				//recipeLayout.setRecipeTransferButton(184-16, topMargin + 18*4);
				
				torque = recipeWrapper.getRequiredTorque();
				revolutions = recipeWrapper.getRequiredRevolutions();
			}
			
			@Override
			public void drawExtras(Minecraft minecraft) {
				IRecipeCategory.super.drawExtras(minecraft);
				
				//TODO FIXME: Localize
				//TODO: Format these floats so we only see a few decimal places
				minecraft.fontRenderer.drawString("Torque: "+torque, 10, 18*2 + 8, 0xFF444444);
				minecraft.fontRenderer.drawString("Revolutions: "+revolutions, 10, 18*3 + 4, 0xFF444444);
				
			}
			
		});
	}
}
