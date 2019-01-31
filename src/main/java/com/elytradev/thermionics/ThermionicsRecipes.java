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

package com.elytradev.thermionics;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map.Entry;

import com.elytradev.concrete.recipe.FluidIngredient;
import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.InventoryGridRecipe;
import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.IRotaryRecipe;
import com.elytradev.thermionics.api.Spirits;
import com.elytradev.thermionics.api.impl.RotaryOreRecipe;
import com.elytradev.thermionics.api.impl.RotaryRecipe;
import com.elytradev.thermionics.block.BlockBase;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.EnumDyeSource;
import com.elytradev.thermionics.data.EnumServerDyeColor;
import com.elytradev.thermionics.data.InspectableShapedInventoryRecipe;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.data.MashTunRecipe;
import com.elytradev.thermionics.data.PotStillRecipe;
import com.elytradev.thermionics.data.SergerRecipe;
import com.elytradev.thermionics.data.WildcardNBTIngredient;
import com.elytradev.thermionics.item.EnumAllomanticPowder;
import com.elytradev.thermionics.item.EnumIngredient;
import com.elytradev.thermionics.item.ItemHammer;
import com.elytradev.thermionics.item.Spirit;
import com.elytradev.thermionics.item.ThermionicsItems;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.SyntaxError;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ThermionicsRecipes {
	//public static final Logger LOG = LogManager.getLogger(ThermionicsRecipes.class);
	
	@SubscribeEvent
	public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
		Thermionics.LOG.info("Loading recipes...");
		
		//LOG.info("Registering recipes");
		IForgeRegistry<IRecipe> r = event.getRegistry();
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:ingredients"), new ItemStack(ThermionicsItems.INGREDIENT, 1, EnumIngredient.GEAR_BRASS.ordinal()),
				" b ", "bib", " b ", 'b', "ingotBrass", 'i', "ingotIron"));
		
		for(EnumServerDyeColor dye : EnumServerDyeColor.values()) {
			ItemStack result = new ItemStack(ThermionicsItems.FABRIC_SQUARE);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("Color", dye.getColorValue());
			result.setTagCompound(tag);
			
			ShapelessRecipes recipe = new ShapelessRecipes("thermionics:ingredientDye",
					result,
					NonNullList.from(null,
							Ingredient.fromItem(ThermionicsItems.FABRIC_SQUARE),
							new OreIngredient("dye"+capitalize(dye.getDyeColorName()))
							//Ingredient.fromStacks(new ItemStack(Items.DYE, 1, dye.getDyeDamage()))
					));
			recipe(r, recipe);
		}
		
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.CABLE_RF,8),
				"wlw", 'w', new ItemStack(Blocks.WOOL,1,OreDictionary.WILDCARD_VALUE), 'l', "ingotLead"));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.SCAFFOLD_BASIC,4),
				"x x", " x ", "x x", 'x', "ingotIron"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.FIREBOX,1),
				"xxx", "x x", "xxx", 'x', "ingotIron"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.OVEN,1),
				"xxx", "x x", "xcx", 'x', "ingotIron", 'c', "ingotCopper"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.MASH_TUN,1),
				"s s", "s s", "scs", 's', "cobblestone", 'c', "ingotCopper"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.POT_STILL,1),
				"bbb", "b b", "bcb", 'b', "ingotBrass", 'c', "ingotCopper"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.MOTOR_CONVECTION),
				"ici", "ctc", "isi",
				'i', "ingotIron",
				'c', "ingotCopper",
				't', new ItemStack(Blocks.REDSTONE_TORCH),
				's', "ingotSilver"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.MOTOR_RF),
				"igi", "gtg", "isi",
				'i', "ingotIron",
				'g', "gearBrass",
				't', new ItemStack(Blocks.REDSTONE_TORCH),
				's', "ingotSilver"));
		
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.GENERATOR_ROTARY),
				"gcg", "cRc", "gsg",
				'g', "gearBrass",
				'c', "ingotCopper",
				'R', new ItemStack(Blocks.REDSTONE_BLOCK),
				's', "ingotSilver"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.GEARBOX),
				"igi", "g g", "igi", 'g', "gearBrass", 'i', "ingotIron"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.SERGER),
				"iii", " ig", "bbi", 'i', "ingotIron", 'b', "ingotBrass", 'g', "gearBrass"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.HAMMER_MILL),
				"IiI", "ifi", "IsI", 'I', "blockIron", 'i', "ingotIron", 's', "ingotSilver", 'f', new ItemStack(Items.FLINT)));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.BATTERY_LEAD,1),
				" c ", "pLp", " r ", 'L', "blockLead", 'c', "ingotCopper", 'r', new ItemStack(Items.REDSTONE), 'p', new ItemStack(Items.PAPER)));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.AXLE_WOOD,4),
				"w", "w", "w", 'w', new ItemStack(Blocks.PLANKS)
				));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.AXLE_IRON,4),
				"i", "i", "i", 'i', "ingotIron"
				));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.CABLE_HEAT,4),
				"c", "c", "c", 'c', "ingotCopper"
				));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.OMNI_DUCT,4),
				"sss", "lrc", "sss", 's', "stone", 'l', "ingotLead", 'c', "ingotCopper", 'r', "dustRedstone"
				));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:tools"), new ItemStack(ThermionicsItems.RESCUE_TOOL),
				"iin", "is ", " s ", 'i', "ingotIron", 's', "stickWood", 'n', "nuggetIron"
				));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotIron"),
				"I", "s", "s", 'I', "blockIron", 's', "stickWood"
				));
		
		recipe(r, new ShapelessOreRecipe(new ResourceLocation("thermionics:spiritbottle"),
				new ItemStack(ThermionicsItems.EMPTY_SPIRIT_BOTTLE),
				new ItemStack(Items.GLASS_BOTTLE)
				));
		
		recipe(r, new ShapelessOreRecipe(new ResourceLocation("thermionics:spiritbottle"),
				new ItemStack(Items.GLASS_BOTTLE),
				new ItemStack(ThermionicsItems.EMPTY_SPIRIT_BOTTLE)
				));
		
		//NEW TOOL RECIPES
		/*
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotGold"),
				"I", "s", "s", 'I', "blockGold", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("gemDiamond"),
				"I", "s", "s", 'I', "blockDiamond", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotCopper"),
				"I", "s", "s", 'I', "blockCopper", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotLead"),
				"I", "s", "s", 'I', "blockLead", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotInvar"),
				"I", "s", "s", 'I', "blockInvar", 's', "stickWood"
				));*/
		
		//LEGACY TOOL RECIPES
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), new ItemStack(ThermionicsItems.HAMMER_GOLD,1),
				"I", "s", "s", 'I', "blockGold", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), new ItemStack(ThermionicsItems.HAMMER_DIAMOND,1),
				"I", "s", "s", 'I', "blockDiamond", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), new ItemStack(ThermionicsItems.HAMMER_COPPER,1),
				"I", "s", "s", 'I', "blockCopper", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), new ItemStack(ThermionicsItems.HAMMER_LEAD,1),
				"I", "s", "s", 'I', "blockLead", 's', "stickWood"
				));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), new ItemStack(ThermionicsItems.HAMMER_INVAR,1),
				"I", "s", "s", 'I', "blockInvar", 's', "stickWood"
				));
		
		//Allomancy
		recipe(r, new ShapelessRecipes("thermionics:items",
				new ItemStack(ThermionicsItems.MISTCLOAK, 1, 1), //Full mistcloak with allomantic invisibility
				NonNullList.from(null,
						Ingredient.fromStacks(new ItemStack(ThermionicsItems.MISTCLOAK, 1, 0)), //Tasselcloak with no special powers +
						Ingredient.fromStacks(new ItemStack(ThermionicsItems.ALLOMANTIC_POWDER, 1, EnumAllomanticPowder.COPPER.ordinal()))//Allomantic copper
				)
			)); 
		
		GameRegistry.addSmelting(Blocks.GRAVEL, new ItemStack(ThermionicsBlocks.ROAD), 0);
		craftingCircle(r, ThermionicsBlocks.ROAD);
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:compression"),
				new ItemStack(ThermionicsBlocks.ROAD_COMPRESSED),
				"xxx", "xgx", "xxx",
				'x', new ItemStack(ThermionicsBlocks.ROAD, 1, OreDictionary.WILDCARD_VALUE),
				'g', "ingotGold"));
		
		//Create a second list for compressed road and make the crafting cycle again
		craftingCircle(r, ThermionicsBlocks.ROAD_COMPRESSED);
		
		//Ore->2xDust, Ingot->1xDust
		millRecipes("Iron");
		millRecipes("Gold");
		millRecipes("Copper");
		millRecipes("Tin");
		millRecipes("Silver");
		millRecipes("Lead");
		millRecipes("Nickel");
		millRecipes("Zinc");
		millRecipes("Platinum");
		millRecipes("Mithril");
		millRecipes("Electrum");
		millRecipes("Brass");
		millRecipes("Bronze");
		millRecipes("Invar");
		millRecipes("Steel");
		millRecipes("Uranium");
		millRecipes("Cobalt");
		millRecipes("Ardite");
		millRecipes("Aluminum");
		millRecipes("Duralumin");
		
		//HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreCoal",     new ItemStack(Items.COAL,3),     8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreRedstone", new ItemStack(Items.REDSTONE,6), 8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreDiamond",  new ItemStack(Items.DIAMOND,2), 10f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreEmerald",  new ItemStack(Items.EMERALD,2),  8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreLapis",    new ItemStack(Items.DYE, 10, 4),  8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Items.FLINT,2), 2f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND,4), 8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND,1), 8f, 20f));
		//HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL,1), 8f, 20f));
		//Special case: Let silvered lead be ground back down into silvered lead dust so it can be separated
		HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(ThermionicsItems.INGOT_SILVERED_LEAD),     new ItemStack(ThermionicsItems.DUST_SILVERED_LEAD),     8f, 20f));
		
		for(EnumDyeSource dyeSource : EnumDyeSource.values()) {
			HammerMillRecipes.registerRecipe(new RotaryRecipe(dyeSource.getExemplar(), dyeSource.createOutputStack(), 2f, 20f)); 
		}
		
		//ItemIngredient potato    = ItemIngredient.of(Items.POTATO);
		//ItemIngredient leather   = ItemIngredient.of("leather");
		//ItemIngredient ingotIron = ItemIngredient.of("ingotIron");
		//ItemIngredient ingotGold = ItemIngredient.of("ingotGold");
		//ItemIngredient diamond   = ItemIngredient.of("gemDiamond");
		ItemIngredient string    = ItemIngredient.of(Items.STRING);
		//ItemIngredient ribbon    = ItemIngredient.of(new ItemStack(ThermionicsItems.INGREDIENT,1, EnumIngredient.RIBBON.ordinal()));
		//ItemIngredient fabric    = ItemIngredient.of(ThermionicsItems.FABRIC_SQUARE);
		ItemIngredient anyFabric = new WildcardNBTIngredient(ThermionicsItems.FABRIC_SQUARE);
		ItemIngredient anyScarf  = new WildcardNBTIngredient(ThermionicsItems.SCARF);
		
		
		//Dust Alloying
		dustAlloy(r, ThermionicsItems.DUST_BRASS, "dustCopper", "dustCopper", "dustCopper", "dustZinc");
		dustAlloy(r, ThermionicsItems.DUST_INVAR, "dustIron", "dustIron", "dustIron", "dustNickel");
		dustAlloy(r, ThermionicsItems.DUST_DURALUMIN, "dustAluminum", "dustAluminum", "dustAluminum", "dustCopper");
		dustAlloy(r, ThermionicsItems.DUST_ELECTRUM, "dustSilver", "dustGold");
		//dustAlloy(r, ThermionicsItems.DUST_SILVERED_LEAD, "dustLead", "dustLead", "dustLead", "dustSilver"); //disabled; not really helping
		
		final float DUST_SMELT_XP = 0.35f;
		GameRegistry.addSmelting(ThermionicsItems.DUST_IRON,     new ItemStack(Items.IRON_INGOT), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_GOLD,     new ItemStack(Items.GOLD_INGOT), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_COPPER,   new ItemStack(ThermionicsItems.INGOT_COPPER), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_SILVER,   new ItemStack(ThermionicsItems.INGOT_SILVER), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_LEAD,     new ItemStack(ThermionicsItems.INGOT_LEAD), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_NICKEL,   new ItemStack(ThermionicsItems.INGOT_NICKEL), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_ZINC,     new ItemStack(ThermionicsItems.INGOT_ZINC), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_ELECTRUM, new ItemStack(ThermionicsItems.INGOT_ELECTRUM), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_BRASS,    new ItemStack(ThermionicsItems.INGOT_BRASS), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_INVAR,    new ItemStack(ThermionicsItems.INGOT_INVAR), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_ALUMINUM, new ItemStack(ThermionicsItems.INGOT_ALUMINUM), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_SILVERED_LEAD, new ItemStack(ThermionicsItems.INGOT_SILVERED_LEAD), DUST_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.DUST_DURALUMIN, new ItemStack(ThermionicsItems.INGOT_DURALUMIN), DUST_SMELT_XP);
		
		final float GRAVEL_SMELT_XP = 0.25f;
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_PYRITE,       new ItemStack(Items.IRON_INGOT), GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_SYLVANITE,    new ItemStack(Items.GOLD_INGOT), GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_PENTLANDITE,  new ItemStack(ThermionicsItems.INGOT_NICKEL),   GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_CHALCOPYRITE, new ItemStack(ThermionicsItems.INGOT_COPPER),   GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_GALENA,       new ItemStack(ThermionicsItems.INGOT_LEAD),     GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_BAUXITE,      new ItemStack(ThermionicsItems.INGOT_ALUMINUM), GRAVEL_SMELT_XP);
		GameRegistry.addSmelting(ThermionicsItems.GRAVEL_SPHALERITE,   new ItemStack(ThermionicsItems.INGOT_ZINC),     GRAVEL_SMELT_XP);
		
		
		String[] defaultRecipes = {
				"{ 'type': 'thermionics:serger', 'torque':  6, 'revolutions': 10, 'result': 'thermionics:fabricsquare', 'pattern': [ 'ss', 'ss' ], 'key': { 's': 'minecraft:string' } }",
				"{ 'type': 'thermionics:serger', 'torque': 10, 'revolutions': 30, 'result': 'minecraft:saddle', 'pattern': [ 'lll', 'i i' ], 'key': { 'l': 'leather', 'i': 'ingotIron' } }",
				"{ 'type': 'thermionics:serger', 'torque': 15, 'revolutions': 30, 'flippable': true, 'result': 'minecraft:diamond_horse_armor', 'pattern': [ '  d', 'ddd', 'ddd' ], 'key': { 'd': 'gemDiamond' } }",
				"{ 'type': 'thermionics:serger', 'torque':  6, 'revolutions': 30, 'flippable': true, 'result': 'minecraft:golden_horse_armor', 'pattern': [ '  d', 'ddd', 'ddd' ], 'key': { 'd': 'ingotGold' } }",
				"{ 'type': 'thermionics:serger', 'torque': 15, 'revolutions': 30, 'flippable': true, 'result': 'minecraft:iron_horse_armor', 'pattern': [ '  d', 'ddd', 'ddd' ], 'key': { 'd': 'ingotIron' } }",
				"{ 'type': 'thermionics:serger', 'torque':  6, 'revolutions': 10, 'result': { 'item': 'thermionics:ingredient', 'meta':0 }, 'pattern': [ 'sss' ], 'key': { 's': 'minecraft:string' } }",
				"{ 'type': 'thermionics:serger', 'torque':  6, 'revolutions': 10, 'result': 'thermionics:scarf', 'pattern': [ 'rfr' ], 'key': { 'r': { 'item': 'thermionics:ingredient', 'meta':0 }, 'f': { 'item': 'thermionics:fabricsquare', 'ignore_nbt': true } } }",
				"{ 'type': 'thermionics:serger', 'torque':  6, 'revolutions': 10, 'result': 'thermionics:bauble.cloak', 'pattern': [ 'f f', 'fff', 'rrr' ], 'key': { 'r': { 'item': 'thermionics:ingredient', 'meta':0 }, 'f': { 'item': 'thermionics:fabricsquare', 'ignore_nbt': true } } }",
				
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:coal', 'count': 3 }, 'ingredient': 'oreCoal' }",
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:redstone', 'count': 6 }, 'ingredient': 'oreRedstone' }",
				"{ 'type': 'thermionics:hammer_mill', 'torque': 10, 'revolutions': 20, 'result': { 'item': 'minecraft:diamond', 'count': 2 }, 'ingredient': 'oreDiamond' }",
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:emerald', 'count': 2 }, 'ingredient': 'oreEmerald' }",
				"{ 'type': 'thermionics:hammer_mill', 'torque': 10, 'revolutions':  4, 'result': { 'item': 'minecraft:dye', 'count': 10, 'meta': 4 }, 'ingredient': 'oreLapis' }",
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:sand', 'count': 4 }, 'ingredient': { 'item': 'minecraft:sandstone', 'meta': '*' } }", //Sandstone -> Sand
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:sand' }, 'ingredient': 'minecraft:gravel' }", //Gravel -> Sand
				"{ 'type': 'thermionics:hammer_mill', 'torque':  8, 'revolutions': 20, 'result': { 'item': 'minecraft:gravel' }, 'ingredient': 'minecraft:cobblestone' }", //Cobblestone -> Gravel
		};
		Jankson jankson = Jankson.builder().build();
		
		File recipesFolder = new File(Thermionics.CONFIG_FOLDER, "recipes");
		if (recipesFolder.exists()) {
			//Do nothing
		} else {
			if (recipesFolder.mkdir()) {
				
				//Pour all the default recipes down into files
				for(int i=0; i<defaultRecipes.length; i++) {
					String recipeName = "defaultRecipe";
					JsonObject obj;
					try {
						obj = jankson.load(defaultRecipes[i]);
						JsonElement resultElem = obj.get("result");
						if (resultElem instanceof JsonPrimitive) {
							String resultString = ((JsonPrimitive) resultElem).asString();
							if (resultString.indexOf(':')>=0) {
								recipeName = new ResourceLocation(resultString).getPath();
							} else recipeName = resultString;
						} else {
							ItemStack stack = MachineRecipes.itemStackFromJson(resultElem);
							if (stack==null || stack.isEmpty()) throw new SyntaxError("can't parse result.");
							recipeName = stack.getItem().getRegistryName().getPath();
						}
					} catch (SyntaxError ex) {
						ex.printStackTrace();
						continue;
					}
					
					File recipeFile = new File(recipesFolder, recipeName+i+".json");
					try (FileWriter out = new FileWriter(recipeFile)) {
						out.write(obj.toJson(false, true));
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		for(File f : recipesFolder.listFiles()) {
			String recipeName = f.getName();
			if (recipeName.endsWith(".json") || recipeName.endsWith(".jkson")) {
				try {
					JsonObject jsonRecipe = jankson.load(f);
					
					String recipeType = jsonRecipe.get(String.class, "type");
					if (recipeType==null) {
						Thermionics.LOG.warn("Can't load recipe \""+recipeName+"\": No type defined!");
						continue;
					}
					switch(recipeType) {
					case "thermionics:serger": {
						SergerRecipe recipe = SergerRecipe.fromJson(jsonRecipe, recipeName);
						if (recipe!=null) MachineRecipes.register(recipe);
						break;
					}
					case "thermionics:hammer_mill": {
						IRotaryRecipe recipe = RotaryRecipe.fromJson(jsonRecipe, recipeName);
						HammerMillRecipes.registerRecipe(recipe);
						break;
					}
					default:
						Thermionics.LOG.warn("Can't load recipe \""+recipeName+"\": Can't load recipes of type \""+recipeType+"\".");
					}
				} catch (IOException ioex) {
					ioex.printStackTrace();
				} catch (SyntaxError ex) {
					System.out.println("While parsing \""+recipeName+"\": "+ex.getCompleteMessage());
				}
			}
		}
		
		/*
		int i = 0;
		for(String s : defaultRecipes) {
			try {
				JsonObject jsonRecipe = jankson.load(s);
				String recipeName = "defaultRecipe"+i+".json";
				
				String recipeType = jsonRecipe.get(String.class, "type");
				if (recipeType==null) {
					Thermionics.LOG.warn("Can't load recipe \""+recipeName+"\": No type defined!");
					i++;
					continue;
				}
				switch(recipeType) {
				case "thermionics:serger": {
					SergerRecipe recipe = SergerRecipe.fromJson(jsonRecipe, recipeName);
					if (recipe!=null) MachineRecipes.register(recipe);
					break;
				}
				case "thermionics:hammer_mill": {
					IRotaryRecipe recipe = RotaryRecipe.fromJson(jsonRecipe, recipeName);
					HammerMillRecipes.registerRecipe(recipe);
					break;
				}
				default:
					Thermionics.LOG.warn("Can't load recipe \""+recipeName+"\": Can't load recipes of type \""+recipeType+"\".");
				}
			} catch (SyntaxError ex) {
				Thermionics.LOG.warn(ex.getCompleteMessage());
			}
			i++;
		}*/
		/*
		SergerRecipe saddleRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
						new ItemStack(Items.SADDLE), //Output
						3, 3,                        //3x3 grid
						3, 2,                        //3x2 recipe
						false,                       //recipe is already horizontally symmetrical
						leather,   leather, leather,
						ingotIron, null,    ingotIron
				),
				10, //Leather requires a steeper torque than the default 8 for cloth
				30  //Done after 30 complete axle rotations
				);
		MachineRecipes.register(saddleRecipe);
		*/
		/*
		SergerRecipe diamondBardingRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
						new ItemStack(Items.DIAMOND_HORSE_ARMOR),
						3, 3,                        //3x3 grid
						3, 3,                        //3x3 recipe
						true,                        //or flipped
						null,    null,    diamond,
						diamond, diamond, diamond,
						diamond, diamond, diamond
				),
				15, //Lots of torque for hard materials
				30  //Done after 30 complete axle rotations
				);
		MachineRecipes.register(diamondBardingRecipe);*/
		/*
		SergerRecipe goldBardingRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
						new ItemStack(Items.GOLDEN_HORSE_ARMOR),
						3, 3,                        //3x3 grid
						3, 3,                        //3x3 recipe
						true,                        //or flipped
						null,      null,      ingotGold,
						ingotGold, ingotGold, ingotGold,
						ingotGold, ingotGold, ingotGold
				),
				6,  //Nothing to it
				30  //Done after 30 complete axle rotations
				);
		MachineRecipes.register(goldBardingRecipe);*/
		/*
		SergerRecipe ironBardingRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
						new ItemStack(Items.IRON_HORSE_ARMOR),
						3, 3,                        //3x3 grid
						3, 3,                        //3x3 recipe
						true,                        //or flipped
						null,      null,      ingotIron,
						ingotIron, ingotIron, ingotIron,
						ingotIron, ingotIron, ingotIron
				),
				15, //Lots of torque for hard materials
				30  //Done after 30 complete axle rotations
				);
		MachineRecipes.register(ironBardingRecipe);*/
		/*
		SergerRecipe ribbonRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.INGREDIENT, 1, EnumIngredient.RIBBON.ordinal()),
					3, 3,  //3x3 serger grid
					3, 1,  //3x1 recipe
					false, //symmetrical
					string, string, string
				),
				6,  //soft fabric crafting
				10  //*fast*
				);
		MachineRecipes.register(ribbonRecipe);*/
		/*
		String fabricRecipeJson = "{ 'type': 'thermionics:serger', 'torque': 6, 'revolutions': 10, 'result': 'thermionics:fabricsquare', 'pattern': [ \"ss\" \"ss\" ], 'key': { 's': 'minecraft:string' } }";
		SergerRecipe fabricRecipe;
		try {
			fabricRecipe = SergerRecipe.fromJson(Jankson.builder().build().load(fabricRecipeJson), "testRecipe.json");
			MachineRecipes.register(fabricRecipe);
		} catch (SyntaxError e) {
			e.printStackTrace();
		}*/
		/*
		SergerRecipe fabricRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.FABRIC_SQUARE, 1),
					3, 3,  //3x3 serger grid
					2, 2,  //2x2 recipe
					false, //symmetrical
					string, string,
					string, string
				),
				6,  //soft fabric crafting
				10  //*fast*
				);*/
		
		
		/*
		SergerRecipe scarfRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.SCARF, 1),
					3, 3,  //3x3 serger grid
					3, 1,  //3x1 recipe
					false, //symmetrical
					ribbon, anyFabric, ribbon
				),
				6,  //soft fabric crafting
				10  //*fast*
				);
		MachineRecipes.register(scarfRecipe);*/
		
		SergerRecipe leftScarfRecipe = new ScarfConstructRecipe(
				new InspectableShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.SCARF, 1),
					3, 3,  //3x3 serger grid
					3, 1,  //3x1 recipe
					false, //intentionally asymmetrical
					anyFabric, string, anyScarf
				),
				6,  //soft fabric crafting
				10, //*fast*
				true
				);
		MachineRecipes.register(leftScarfRecipe);
		
		SergerRecipe rightScarfRecipe = new ScarfConstructRecipe(
				new InspectableShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.SCARF, 1),
					3, 3,  //3x3 serger grid
					3, 1,  //3x1 recipe
					false, //intentionally asymmetrical
					anyScarf, string, anyFabric
				),
				6,  //soft fabric crafting
				10, //*fast*
				false
				);
		MachineRecipes.register(rightScarfRecipe);
		/*
		SergerRecipe tasselcloakRecipe = new SergerRecipe(
				new InspectableShapedInventoryRecipe(
						new ItemStack(ThermionicsItems.MISTCLOAK, 1),
						3, 3,  //3x3 serger grid
						3, 3,  //3x3 recipe
						false, //symmetrical
						anyFabric,   null, anyFabric,
						anyFabric, anyFabric, anyFabric,
						ribbon, ribbon, ribbon
					),
					6,  //soft fabric crafting
					10  //*fast*
				);
		MachineRecipes.register(tasselcloakRecipe);*/
		
		//### MASH TUN and POT STILL###
		for(Entry<ResourceLocation, Spirit> entry : Spirits.REGISTRY.getEntries()) {
			
			//MASH TUN
			
			NBTTagCompound mashTag = new NBTTagCompound();
			mashTag.setString("Spirit", entry.getKey().toString());
			FluidStack mashFluid = new FluidStack(ThermionicsBlocks.FLUID_HOOTCH, 1000, mashTag);
			
			MashTunRecipe mashRecipe = new MashTunRecipe(mashFluid, 1000, entry.getValue().getMashBase(), 16);
			MachineRecipes.register(mashRecipe);
			
			//POT STILL
			
			NBTTagCompound stillInputTag = new NBTTagCompound();
			stillInputTag.setString("Spirit", entry.getKey().toString());
			FluidStack stillInput = new FluidStack(ThermionicsBlocks.FLUID_HOOTCH, 4, stillInputTag);
			
			
			NBTTagCompound stillOutputTag = new NBTTagCompound();
			stillOutputTag.setString("Spirit", entry.getKey().toString());
			FluidStack stillOutput = new FluidStack(ThermionicsBlocks.FLUID_SPIRITS, 3, stillOutputTag);
			
			PotStillRecipe distillation = new PotStillRecipe(stillOutput, FluidIngredient.of(stillInput));
			MachineRecipes.register(distillation);
		}
		
	}
	
	/**
	 * Auto-registry-name is only good for recipes which have only one version!
	 */
	public static <T extends IRecipe> T recipe(IForgeRegistry<IRecipe> registry, T t) {
		//LOG.info("Recipe:"+t.toString());
		//LOG.info("OutputItemStack:"+t.getRecipeOutput());
		//LOG.info("OutputItem:"+t.getRecipeOutput().getItem().getRegistryName());
		String registryName = makeUnique(registry, t.getRecipeOutput().getItem().getRegistryName()+"_"+t.getRecipeOutput().getItemDamage());
		t.setRegistryName(new ResourceLocation(registryName));
		registry.register(t);
		return t;
	}
	
	public static void dustAlloy(IForgeRegistry<IRecipe> registry, Item output, String... ingredients) {
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(new ResourceLocation("thermionics", "dust_alloy"), new ItemStack(output, ingredients.length, 0), (Object[])ingredients);
		recipe.setRegistryName(makeUnique(registry, "dust_alloy."+output.getRegistryName()));
		registry.register(recipe);
	}
	
	public static String makeUnique(IForgeRegistry<IRecipe> registry, String baseName) {
		String result = baseName.replace(':', '.');
		if (!registry.containsKey(new ResourceLocation("thermionics",result))) return "thermionics:"+result;
		
		int i=0;
		while (registry.containsKey(new ResourceLocation("thermionics",result+"."+i))) i++;
		
		return "thermionics:"+result+"."+i;
	}
	
	public static void craftingCircle(IForgeRegistry<IRecipe> registry, BlockBase block) {
		NonNullList<ItemStack> variants = NonNullList.create();
		block.getVariants(ItemBlock.getItemFromBlock(block), variants);
		ItemStack first = variants.remove(0);
		ItemStack prev = first;
		int i = 0;
		for(ItemStack item : variants) {
			ShapelessRecipes recipe = new ShapelessRecipes("thermionics:chisel",
					item.copy(),
					NonNullList.from(null,
							Ingredient.fromStacks(prev.copy())
					));
			recipe.setRegistryName(block.getRegistryName()+"_"+i);
			registry.register(recipe);
			prev = item;
			i++;
		}
		
		ShapelessRecipes recipe = new ShapelessRecipes("thermionics:chisel",
				first.copy(),
				NonNullList.from(null,
						Ingredient.fromStacks(prev.copy())
				));
		recipe.setRegistryName(block.getRegistryName()+"_"+i);
		registry.register(recipe);
	}
	
	public static void millRecipes(String key) {
		if (OreDictionary.doesOreNameExist("dust"+key) && !OreDictionary.getOres("dust"+key).isEmpty()) {
			//System.out.println("Found dust for "+key+". Registering mill recipes.");
			NonNullList<ItemStack> dusts = OreDictionary.getOres("dust"+key);
			if (!dusts.isEmpty()) {
				ItemStack oneDust = dusts.get(0).copy();
				ItemStack twoDust = oneDust.copy(); twoDust.setCount(2);
				if (OreDictionary.doesOreNameExist("ore"+key) && !OreDictionary.getOres("ore"+key).isEmpty()) {
					HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ore"+key, twoDust, 10f, 30f));
				}
				HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ingot"+key, oneDust, 10f, 10f));
			}
		}
	}
	
	private static class ScarfConstructRecipe extends SergerRecipe {
		private boolean left = false;
		
		public ScarfConstructRecipe(InventoryGridRecipe plan, float torque, float revolutions, boolean left) {
			super(plan, torque, revolutions);
			this.left = left;
		}
		
		@Override
		public ItemStack getOutput(IItemHandler inventory) {
			ItemStack out = plan.getOutput(inventory).copy();
			//Find the scarf and the fabric stack
			ItemStack scarf = null;
			ItemStack fabric = null;
			for(int i=0; i<9; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack!=null && stack.getItem()==ThermionicsItems.FABRIC_SQUARE) {
					fabric = stack;
				} else if (stack!=null && stack.getItem()==ThermionicsItems.SCARF) {
					scarf = stack;
				}
			}
			
			if (scarf!=null && fabric!=null) {
				NBTTagCompound tag = null;
				if (scarf.getTagCompound()!=null) {
					tag = scarf.getTagCompound().copy();
				} else {
					tag = new NBTTagCompound();
				}
				out.setTagCompound(tag);
				String sideKey = (left) ? "LeftScarf" : "RightScarf";
				
				NBTTagList sideList = tag.getTagList(sideKey, 10); //list of compound
				NBTTagCompound fabricTag = null;
				if (fabric.hasTagCompound()) {
					fabricTag = fabric.getTagCompound().copy();
				} else {
					fabricTag = new NBTTagCompound();
					fabricTag.setInteger("Color", 0xFFFFFF);
				}
				sideList.appendTag(fabricTag);
				tag.setTag(sideKey, sideList);
			}
			return out;
		}
	}
	
	private static String capitalize(String s) {
		if (s.length()<1) return "";
		return (""+s.charAt(0)).toUpperCase(Locale.ROOT) + s.substring(1);
	}
}
