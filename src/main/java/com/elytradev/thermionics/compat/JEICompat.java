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

import java.util.ArrayList;

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

import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.data.SergerRecipe;
import com.elytradev.thermionics.gui.ContainerHammerMill;
import com.elytradev.thermionics.gui.ContainerSerger;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
@JEIPlugin
public class JEICompat implements IModPlugin {
	
	@Override
	public void register(IModRegistry registry) {
		//TODO: Localize
		registry.addIngredientInfo(new ItemStack(ThermionicsBlocks.FIREBOX), ItemStack.class,
				"Burns any furnace fuel (or lava) to create Heat. Heat can be consumed by machines like the Oven or Convection Motor."
				);
		
		registry.addIngredientInfo(new ItemStack(ThermionicsBlocks.CABLE_HEAT), ItemStack.class,
				"Heat diffuses very efficiently through these hollow copper tubes. Use them to bring heat to machines that need it."
				);
		
		registry.addIngredientInfo(new ItemStack(ThermionicsBlocks.OVEN), ItemStack.class,
				"Cooks or smelts any item a Furnace would. Doesn't waste part of a coal block - if there's too much, the heat is just stored."
				);
		
		registry.addIngredientInfo(new ItemStack(ThermionicsBlocks.MOTOR_CONVECTION), ItemStack.class,
				"Converts Heat into rotational energy, which can then be delivered directly to an adjacent machine, or indirectly through Axles and Gearboxes.",
				"Motors run more slowly when they encounter more resistance (torque load). Resistance depends on how many machines are attached and what they're processing!"
				);
		
		ArrayList<ItemStack> axles = new ArrayList<>();
		axles.add(new ItemStack(ThermionicsBlocks.AXLE_WOOD));
		axles.add(new ItemStack(ThermionicsBlocks.AXLE_IRON));
		registry.addIngredientInfo(axles, ItemStack.class,
				"Axles deliver rotational energy from Motors to Gearboxes or rotary machines. Axles must be placed in a straight line between two rotary devices so that they visually connect in order to deliver rotation.",
				"In order to 'turn' or move power more than 16 blocks, you need to use a Gearbox."
				);
		
		registry.addIngredientInfo(new ItemStack(ThermionicsBlocks.GEARBOX), ItemStack.class,
				"Gearboxes turn, split, and extend any rotary power delivered to them. If split, the torque load on the motor(s) is the *sum* of the loads on the entire system! This can slow things to a crawl, and make running multiple machines from a weak motor undesirable."
				);
		
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
		//System.out.println("Registering HammerMill with JEI. "+MachineRecipes.allHammerMill().size()+" recipes found.");
		registry.addRecipeCatalyst(new ItemStack(ThermionicsBlocks.HAMMER_MILL), "thermionics:hammer_mill");
		registry.addRecipes(MachineRecipes.allHammerMill(), "thermionics:hammer_mill");
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerHammerMill.class, "thermionics:hammer_mill", 0, 1, 2, 36);
		registry.addRecipeClickArea(ConcreteGui.class, 18*4, 18*1, 18, 18, "thermionics:hammer_mill");
		
		registry.addRecipeCatalyst(new ItemStack(ThermionicsBlocks.SERGER), "thermionics:serger");
		registry.addRecipes(MachineRecipes.allSerger(), "thermionics:serger");
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSerger.class, "thermionics:serger", 0, 9, 10, 36);
		//registry.addRecipeClickArea(ConcreteGui.class, 18*4, 18*1, 18, 18, "thermionics:serger"); //Need a separate GUI subclass per machine for JEI to work properly!
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
				return "thermionics";
			}

			@Override
			public IDrawable getBackground() {
				return new JEIDrawableImage(new ResourceLocation("thermionics", "textures/gui/hammermill.png"), 0, 0, 126, 72, 0, 0, 0, 0, 126, 72);
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
		
		registry.addRecipeCategories(new IRecipeCategory<SergerRecipe>() {
			//protected int radiance;
			//protected int emc;
			protected float torque = 0;
			protected float revolutions = 0;
			
			@Override
			public String getUid() {
				return "thermionics:serger";
			}

			@Override
			public String getTitle() {
				return I18n.translateToLocal("tile.thermionics.machine.serger.name");
			}

			@Override
			public String getModName() {
				return "thermionics";
			}

			@Override
			public IDrawable getBackground() {
				return new JEIDrawableImage(new ResourceLocation("thermionics", "textures/gui/serger.png"), 0, 0, 108, 72, 0, 0, 0, 0, 108, 72);
			}

			@Override
			public void setRecipe(IRecipeLayout recipeLayout, SergerRecipe recipeWrapper, IIngredients ingredients) {
				int leftMargin = 0;
				int topMargin  = 0;
				recipeLayout.getItemStacks().init(0, true,  leftMargin + 18*0, topMargin + 18*0);
				recipeLayout.getItemStacks().init(1, true,  leftMargin + 18*1, topMargin + 18*0);
				recipeLayout.getItemStacks().init(2, true,  leftMargin + 18*2, topMargin + 18*0);
				recipeLayout.getItemStacks().init(3, true,  leftMargin + 18*0, topMargin + 18*1);
				recipeLayout.getItemStacks().init(4, true,  leftMargin + 18*1, topMargin + 18*1);
				recipeLayout.getItemStacks().init(5, true,  leftMargin + 18*2, topMargin + 18*1);
				recipeLayout.getItemStacks().init(6, true,  leftMargin + 18*0, topMargin + 18*2);
				recipeLayout.getItemStacks().init(7, true,  leftMargin + 18*1, topMargin + 18*2);
				recipeLayout.getItemStacks().init(8, true,  leftMargin + 18*2, topMargin + 18*2);
				
				recipeLayout.getItemStacks().init(9, false, leftMargin + 18*4, topMargin + 18*1);
				
				recipeLayout.getItemStacks().set(ingredients);
				
				
				//recipeLayout.setRecipeTransferButton(184-16, topMargin + 18*4);
				
				torque = recipeWrapper.getTorque();
				revolutions = recipeWrapper.getRevolutions();
			}
			
			@Override
			public void drawExtras(Minecraft minecraft) {
				IRecipeCategory.super.drawExtras(minecraft);
				
				//TODO FIXME: Localize
				//TODO: Format these floats so we only see a few decimal places
				minecraft.fontRenderer.drawString("Torque: "+torque, 10, 18*3 + 8, 0xFF444444);
				minecraft.fontRenderer.drawString("Revolutions: "+revolutions, 10, 18*4 + 4, 0xFF444444);
				
			}
			
		});
	}
}
