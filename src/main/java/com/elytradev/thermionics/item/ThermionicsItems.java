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

package com.elytradev.thermionics.item;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.block.BlockBase;
import com.elytradev.thermionics.block.BlockBattery;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ThermionicsItems {
	//Unified Hammer
	public static ItemHammer                         HAMMER;
	
	//Legacy Hammers
	public static ItemHammer                         HAMMER_GOLD;
	public static ItemHammer                         HAMMER_DIAMOND;
	public static ItemHammer                         HAMMER_COPPER;
	public static ItemHammer                         HAMMER_LEAD;
	public static ItemHammer                         HAMMER_INVAR;
	
	//Ingredients
	public static ItemSubtyped<EnumIngredient>       INGREDIENT;
	public static ItemSubtyped<EnumAllomanticPowder> ALLOMANTIC_POWDER;
	
	//Cloaks
	public static ItemMistcloak                      MISTCLOAK;
	
	//Creative
	public static ItemChunkUnloader                  CHUNK_UNLOADER;
	
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
		//Thermionics.LOG.info("Registering items");
		
		IForgeRegistry<Item> r = event.getRegistry();
		
		//Blocks first
		for(Block b : Thermionics.instance().needItemRegistration) {
			if (b instanceof BlockBase) {
				item(r, new ItemBlockEquivalentState(b));
			} else if (b instanceof BlockBattery) {
				item(r, new ItemBlockBattery(b));
			} else {
				ItemBlock i = new ItemBlock(b);
				i.setRegistryName(b.getRegistryName());
				item(r, i);
			}
		}
		
		//Unified Hammer
		ThermionicsItems.HAMMER             = item(r, new ItemHammer(ToolMaterial.IRON,    "iron"));
		
		//Legacy Hammers - switchover to neo-hammer isn't done yet, so always register
		//if (CONFIG_ENFORCE_COMPATIBILITY) {
			ThermionicsItems.HAMMER_GOLD    = item(r, new ItemHammer(ToolMaterial.GOLD,    "gold"));
			ThermionicsItems.HAMMER_DIAMOND = item(r, new ItemHammer(ToolMaterial.DIAMOND, "diamond"));
			//Since we can't *safely*, *cleanly* rendezvous with other mods about Item.ToolMaterial properties, make our own
            /*                                                       name      repairOre      level  uses eff dmg ench */
            /*                                                       iron      ingotIron      2      250  6f  2f  14   */
			ThermionicsItems.HAMMER_COPPER  = item(r, new ItemHammer("copper", "ingotCopper", 2,     200, 7f, 1f, 20));
			ThermionicsItems.HAMMER_LEAD    = item(r, new ItemHammer("lead",   "ingotLead",   2,    1550, 4f, 2f,  8));
			ThermionicsItems.HAMMER_INVAR   = item(r, new ItemHammer("invar",  "ingotInvar",  2,     300, 6f, 2f, 14));
		//}
		
		//Ingredients
		ThermionicsItems.INGREDIENT         = item(r, new ItemSubtyped<EnumIngredient>      ("ingredient",       EnumIngredient.values(),       false));
		ThermionicsItems.ALLOMANTIC_POWDER  = item(r, new ItemSubtyped<EnumAllomanticPowder>("allomanticpowder", EnumAllomanticPowder.values(), true));
		
		//Cloaks
		ThermionicsItems.MISTCLOAK          = item(r, new ItemMistcloak());
		
		//Creative
		ThermionicsItems.CHUNK_UNLOADER     = item(r, new ItemChunkUnloader());
		
		
	}
	
	public static <T extends Item> T item(IForgeRegistry<Item> registry, T t) {
		registry.register(t);
		Thermionics.instance().needModelRegistration.add(t);
		return t;
	}
}
