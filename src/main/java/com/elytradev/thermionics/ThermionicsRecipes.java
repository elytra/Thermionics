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

import java.util.Map.Entry;

import com.elytradev.concrete.recipe.FluidIngredient;
import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.ShapedInventoryRecipe;
import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.Spirits;
import com.elytradev.thermionics.api.impl.RotaryOreRecipe;
import com.elytradev.thermionics.api.impl.RotaryRecipe;
import com.elytradev.thermionics.block.BlockBase;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.EnumDyeSource;
import com.elytradev.thermionics.data.MachineRecipes;
import com.elytradev.thermionics.data.MashTunRecipe;
import com.elytradev.thermionics.data.PotStillRecipe;
import com.elytradev.thermionics.data.SergerRecipe;
import com.elytradev.thermionics.item.EnumAllomanticPowder;
import com.elytradev.thermionics.item.EnumIngredient;
import com.elytradev.thermionics.item.ItemHammer;
import com.elytradev.thermionics.item.Spirit;
import com.elytradev.thermionics.item.ThermionicsItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ThermionicsRecipes {
	//public static final Logger LOG = LogManager.getLogger(ThermionicsRecipes.class);
	
	@SubscribeEvent
	public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
		//LOG.info("Registering recipes");
		IForgeRegistry<IRecipe> r = event.getRegistry();
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.CABLE_RF,8),
				"wlw", 'w', new ItemStack(Blocks.WOOL,1,OreDictionary.WILDCARD_VALUE), 'l', "ingotLead"));
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.SCAFFOLD_BASIC,4),
				"x x", " x ", "x x", 'x', "ingotIron"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.FIREBOX,1),
				"xxx", "x x", "xxx", 'x', "ingotIron"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.OVEN,1),
				"xxx", "x x", "xcx", 'x', "ingotIron", 'c', "ingotCopper"));
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:blocks"), new ItemStack(ThermionicsBlocks.MOTOR_CONVECTION),
				"IcI", "ctc", "IsI",
				'I', "blockIron",
				'c', "ingotCopper",
				't', new ItemStack(Blocks.REDSTONE_TORCH),
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
		
		
		recipe(r, new ShapedOreRecipe(new ResourceLocation("thermionics:hammers"), ItemHammer.createTool("ingotIron"),
				"I", "s", "s", 'I', "blockIron", 's', "stickWood"
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
		
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreCoal",     new ItemStack(Items.COAL,3),     8f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreRedstone", new ItemStack(Items.REDSTONE,6), 8f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreDiamond",  new ItemStack(Items.DIAMOND,2), 10f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreEmerald",  new ItemStack(Items.EMERALD,2),  8f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe("oreLapis",    new ItemStack(Items.DYE, 10, 4),  8f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Items.FLINT,2), 2f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND,4), 8f, 20f));
		HammerMillRecipes.registerRecipe(new RotaryRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND,1), 8f, 20f));
		
		for(EnumDyeSource dyeSource : EnumDyeSource.values()) {
			HammerMillRecipes.registerRecipe(new RotaryRecipe(dyeSource.getExemplar(), dyeSource.createOutputStack(), 2f, 20f)); 
		}
		
		ItemIngredient potato    = ItemIngredient.of(Items.POTATO);
		ItemIngredient leather   = ItemIngredient.of("leather");
		ItemIngredient ingotIron = ItemIngredient.of("ingotIron");
		ItemIngredient ingotGold = ItemIngredient.of("ingotGold");
		ItemIngredient diamond   = ItemIngredient.of("gemDiamond");
		ItemIngredient string    = ItemIngredient.of(Items.STRING);
		
		SergerRecipe saddleRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
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
		
		SergerRecipe diamondBardingRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
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
		MachineRecipes.register(diamondBardingRecipe);
		
		SergerRecipe goldBardingRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
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
		MachineRecipes.register(goldBardingRecipe);
		
		SergerRecipe ironBardingRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
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
		MachineRecipes.register(ironBardingRecipe);
		
		SergerRecipe ribbonRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.INGREDIENT, 1, EnumIngredient.RIBBON.ordinal()),
					3, 3,  //3x3 serger grid
					3, 2,  //3x2 recipe
					false, //symmetrical
					string, string, string,
					string, string, string
				),
				6,  //soft fabric crafting
				10  //*fast*
				);
		MachineRecipes.register(ribbonRecipe);
		
		/*
		SergerRecipe fabricRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
					new ItemStack(ThermionicsItems.FABRIC, 1),
					3, 3,  //3x3 serger grid
					2, 2,  //2x2 recipe
					false, //symmetrical
					ribbon, ribbon,
					ribbon, ribbon
				),
				6,  //soft fabric crafting
				10  //*fast*
				);
		MachineRecipes.register(ribbonRecipe);
		*/
		
		
		/*
		SergerRecipe tasselcloakRecipe = new SergerRecipe(
				new ShapedInventoryRecipe(
						new ItemStack(ThermionicsItems.MISTCLOAK, 1),
						3, 3,  //3x3 serger grid
						3, 3,  //3x3 recipe
						false, //symmetrical
						fabric, fabric, fabric,
						fabric, fabric, fabric,
						ribbon, ribbon, ribbon
					),
					6,  //soft fabric crafting
					10  //*fast*
				);
		MachineRecipes.register(tasselcloakRecipe);
		*/
		
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
		
		t.setRegistryName(new ResourceLocation(t.getRecipeOutput().getItem().getRegistryName()+"_"+t.getRecipeOutput().getItemDamage()));
		registry.register(t);
		return t;
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
				prev.copy(),
				NonNullList.from(null,
						Ingredient.fromStacks(first.copy())
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
				//if (OreDictionary.doesOreNameExist("ore"+key) && !OreDictionary.getOres("ore"+key).isEmpty()) {
					HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ore"+key, twoDust, 10f, 30f));
				//}
				HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ingot"+key, oneDust, 10f, 10f));
			}
		}
	}
}
