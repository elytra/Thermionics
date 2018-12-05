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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
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
	
	
	//Gravels
	public static ItemIngredient GRAVEL_PYRITE;       //iron, zinc
	public static ItemIngredient GRAVEL_PENTLANDITE;  //nickel, iron, zinc
	public static ItemIngredient GRAVEL_SYLVANITE;    //gold, silver, tellurium; actual gold ores are rare, but among the rare non-native golds, this is fairly common.
	public static ItemIngredient GRAVEL_CHALCOPYRITE; //copper, iron, zinc
	public static ItemIngredient GRAVEL_GALENA;       //lead, silver, zinc
	public static ItemIngredient GRAVEL_BAUXITE;      //aluminum, iron
	public static ItemIngredient GRAVEL_SPHALERITE;   //zinc sulfide
	
	//Dusts
	public static ItemIngredient DUST_IRON;
	public static ItemIngredient DUST_NICKEL;
	public static ItemIngredient DUST_GOLD;
	public static ItemIngredient DUST_COPPER;
	public static ItemIngredient DUST_SILVER;
	public static ItemIngredient DUST_LEAD;
	public static ItemIngredient DUST_ALUMINUM;
	public static ItemIngredient DUST_ZINC;
	
	//Alloy Dusts
	public static ItemIngredient DUST_ELECTRUM;      //gold-silver 1:1, obtained from sylvanite
	public static ItemIngredient DUST_INVAR;         //iron-nickel 3:1
	public static ItemIngredient DUST_BRASS;         //copper-zinc 3:1
	public static ItemIngredient DUST_SILVERED_LEAD; //lead-silver 3:1
	
	/** Before carbon fiber, Duralumin was one of the only really strong lightweight materials, so it found use in
	 * aircraft and zeppelins for a short while after WWI. The material actually hardens as it ages, so it's cheap to
	 * work but lasts for a long time. This was undercut by its poor corrosion resistance, and by the 1940s, aerospace
	 * was dominated by steel and simple aluminum. Even though carbon fiber showed up in the 1970s, lightweight
	 * construction today is split between it and ordinary aluminum.
	 */
	public static ItemIngredient DUST_DURALUMIN;     //aluminum-copper 3:1 (IRL it's more like 90-95% Al)
	
	//Ingots
	public static ItemIngredient INGOT_NICKEL;
	public static ItemIngredient INGOT_COPPER;
	public static ItemIngredient INGOT_SILVER;
	public static ItemIngredient INGOT_LEAD;
	public static ItemIngredient INGOT_ALUMINUM;
	public static ItemIngredient INGOT_ZINC;
	public static ItemIngredient INGOT_ELECTRUM;
	public static ItemIngredient INGOT_INVAR;
	public static ItemIngredient INGOT_BRASS;
	public static ItemIngredient INGOT_SILVERED_LEAD;
	public static ItemIngredient INGOT_DURALUMIN;
	
	//Ingredients
	public static ItemSubtyped<EnumIngredient>       INGREDIENT;
	public static ItemFabricSquare                   FABRIC_SQUARE;
	public static ItemSubtyped<EnumAllomanticPowder> ALLOMANTIC_POWDER;
	
	//Booze - Drink responsibly, kids
	public static ItemSpiritBottle                   EMPTY_SPIRIT_BOTTLE;
	public static ItemSpiritBottle                   SPIRIT_BOTTLE;
	
	//Cloaks
	public static ItemMistcloak                      MISTCLOAK;
	public static ItemScarf                          SCARF;
	
	//Creative
	public static ItemChunkUnloader                  CHUNK_UNLOADER;
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
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
			//For hammers, these values are multiplied by 9 (so iron, which gives a pick 250 uses, gives a hammer 2250 to
			//compensate for the extra material used
			/*                                                        name      repairOre     level  uses eff dmg ench */
			/*                                                        iron      ingotIron      2     250  6f  2f  14   */
			ThermionicsItems.HAMMER_COPPER  = item(r, new ItemHammer("copper", "ingotCopper", 2,     200, 7f, 1f, 20));
			ThermionicsItems.HAMMER_LEAD    = item(r, new ItemHammer("lead",   "ingotLead",   2,    1550, 4f, 2f,  8));
			ThermionicsItems.HAMMER_INVAR   = item(r, new ItemHammer("invar",  "ingotInvar",  2,     300, 6f, 2f, 14));
		//}
		
		//Ingredients
		ThermionicsItems.INGREDIENT         = item(r, new ItemSubtyped<EnumIngredient>      ("ingredient",       EnumIngredient.values(),       false));
		ThermionicsItems.FABRIC_SQUARE      = item(r, new ItemFabricSquare());
		ThermionicsItems.ALLOMANTIC_POWDER  = item(r, new ItemSubtyped<EnumAllomanticPowder>("allomanticpowder", EnumAllomanticPowder.values(), true));
		OreDictionary.registerOre("gearBrass", new ItemStack(ThermionicsItems.INGREDIENT,1, EnumIngredient.GEAR_BRASS.ordinal()));
		
		ThermionicsItems.GRAVEL_PYRITE      = item(r, new ItemIngredient("gravel.pyrite"));
		ThermionicsItems.GRAVEL_PENTLANDITE = item(r, new ItemIngredient("gravel.pentlandite"));
		ThermionicsItems.GRAVEL_SYLVANITE   = item(r, new ItemIngredient("gravel.sylvanite"));
		ThermionicsItems.GRAVEL_CHALCOPYRITE= item(r, new ItemIngredient("gravel.chalcopyrite"));
		ThermionicsItems.GRAVEL_GALENA      = item(r, new ItemIngredient("gravel.galena"));
		ThermionicsItems.GRAVEL_BAUXITE     = item(r, new ItemIngredient("gravel.bauxite"));
		ThermionicsItems.GRAVEL_SPHALERITE  = item(r, new ItemIngredient("gravel.sphalerite"));
		
		ThermionicsItems.DUST_IRON          = item(r, new ItemIngredient("dust.iron"));
		ThermionicsItems.DUST_NICKEL        = item(r, new ItemIngredient("dust.nickel"));
		ThermionicsItems.DUST_GOLD          = item(r, new ItemIngredient("dust.gold"));
		ThermionicsItems.DUST_COPPER        = item(r, new ItemIngredient("dust.copper"));
		ThermionicsItems.DUST_SILVER        = item(r, new ItemIngredient("dust.silver"));
		ThermionicsItems.DUST_LEAD          = item(r, new ItemIngredient("dust.lead"));
		ThermionicsItems.DUST_ALUMINUM      = item(r, new ItemIngredient("dust.aluminum"));
		ThermionicsItems.DUST_ZINC          = item(r, new ItemIngredient("dust.zinc"));
		
		ThermionicsItems.DUST_ELECTRUM      = item(r, new ItemIngredient("dust.electrum"));
		ThermionicsItems.DUST_INVAR         = item(r, new ItemIngredient("dust.invar"));
		ThermionicsItems.DUST_BRASS         = item(r, new ItemIngredient("dust.brass"));
		ThermionicsItems.DUST_SILVERED_LEAD = item(r, new ItemIngredient("dust.silvered_lead"));
		ThermionicsItems.DUST_DURALUMIN     = item(r, new ItemIngredient("dust.duralumin"));
		
		ThermionicsItems.INGOT_NICKEL       = item(r, new ItemIngredient("ingot.nickel"));
		ThermionicsItems.INGOT_COPPER       = item(r, new ItemIngredient("ingot.copper"));
		ThermionicsItems.INGOT_SILVER       = item(r, new ItemIngredient("ingot.silver"));
		ThermionicsItems.INGOT_LEAD         = item(r, new ItemIngredient("ingot.lead"));
		ThermionicsItems.INGOT_ALUMINUM     = item(r, new ItemIngredient("ingot.aluminum"));
		ThermionicsItems.INGOT_ZINC         = item(r, new ItemIngredient("ingot.zinc"));
		ThermionicsItems.INGOT_ELECTRUM     = item(r, new ItemIngredient("ingot.electrum"));
		ThermionicsItems.INGOT_INVAR        = item(r, new ItemIngredient("ingot.invar"));
		ThermionicsItems.INGOT_BRASS        = item(r, new ItemIngredient("ingot.brass"));
		ThermionicsItems.INGOT_SILVERED_LEAD= item(r, new ItemIngredient("ingot.silvered_lead"));
		ThermionicsItems.INGOT_DURALUMIN    = item(r, new ItemIngredient("ingot.duralumin"));
		
		//oredict: General, specific
		//Gravels are mixed ore rock, often containing multiple metals, so this may be necessary for mod interop
		OreDictionary.registerOre("gravelIron",       GRAVEL_PYRITE);       OreDictionary.registerOre("gravelPyrite",       GRAVEL_PYRITE);
		OreDictionary.registerOre("gravelNickel",     GRAVEL_PENTLANDITE);  OreDictionary.registerOre("gravelPentlandite",  GRAVEL_PENTLANDITE);
		OreDictionary.registerOre("gravelGold",       GRAVEL_SYLVANITE);    OreDictionary.registerOre("gravelSylvanite",    GRAVEL_SYLVANITE);
		OreDictionary.registerOre("gravelCopper",     GRAVEL_CHALCOPYRITE); OreDictionary.registerOre("gravelChalcopyrite", GRAVEL_CHALCOPYRITE);
		OreDictionary.registerOre("gravelLead",       GRAVEL_GALENA);       OreDictionary.registerOre("gravelGalena",       GRAVEL_GALENA);
		OreDictionary.registerOre("gravelAluminum",   GRAVEL_BAUXITE);      OreDictionary.registerOre("gravelBauxite",      GRAVEL_BAUXITE);
		OreDictionary.registerOre("gravelSphalerite", GRAVEL_SPHALERITE);   OreDictionary.registerOre("gravelZinc",         GRAVEL_SPHALERITE);
		
		/*
		Unifier<ItemStack> unifier = UnificationRegistry.getItemStackUnifier();
		unifier.add(UnifierConstants.IRON,     UnifierConstants.GRAVEL, new ItemStack(GRAVEL_PYRITE));
		unifier.add(UnifierConstants.NICKEL,   UnifierConstants.GRAVEL, new ItemStack(GRAVEL_PENTLANDITE));
		unifier.add(UnifierConstants.GOLD,     UnifierConstants.GRAVEL, new ItemStack(GRAVEL_SYLVANITE));
		unifier.add(UnifierConstants.COPPER,   UnifierConstants.GRAVEL, new ItemStack(GRAVEL_CHALCOPYRITE));
		unifier.add(UnifierConstants.LEAD,     UnifierConstants.GRAVEL, new ItemStack(GRAVEL_GALENA));
		unifier.add(UnifierConstants.ALUMINUM, UnifierConstants.GRAVEL, new ItemStack(GRAVEL_BAUXITE));
		unifier.add(UnifierConstants.ZINC,     UnifierConstants.GRAVEL, new ItemStack(GRAVEL_SPHALERITE));
		*/
		
		//Dusts are pure-metal so we can safely just register them with their metal name. Sorry brits, you're going to have to deal with american "aluminum" spelling.
		OreDictionary.registerOre("dustIron",     DUST_IRON);
		OreDictionary.registerOre("dustNickel",   DUST_NICKEL);
		OreDictionary.registerOre("dustGold",     DUST_GOLD);
		OreDictionary.registerOre("dustCopper",   DUST_COPPER);
		OreDictionary.registerOre("dustSilver",   DUST_SILVER);
		OreDictionary.registerOre("dustLead",     DUST_LEAD);
		OreDictionary.registerOre("dustLeadAny",  DUST_LEAD);
		OreDictionary.registerOre("dustAluminum", DUST_ALUMINUM);
		OreDictionary.registerOre("dustZinc",     DUST_ZINC);
		OreDictionary.registerOre("dustElectrum", DUST_ELECTRUM);
		OreDictionary.registerOre("dustInvar",    DUST_INVAR);
		OreDictionary.registerOre("dustBrass",    DUST_BRASS);
		OreDictionary.registerOre("dustLeadAny",  DUST_SILVERED_LEAD); //Can be used in *some* lead recipes, but not all
		OreDictionary.registerOre("dustDuralumin",DUST_DURALUMIN);
		
		/*
		unifier.add(UnifierConstants.IRON,     UnifierConstants.DUST, new ItemStack(DUST_IRON));
		unifier.add(UnifierConstants.NICKEL,   UnifierConstants.DUST, new ItemStack(DUST_NICKEL));
		unifier.add(UnifierConstants.GOLD,     UnifierConstants.DUST, new ItemStack(DUST_GOLD));
		unifier.add(UnifierConstants.COPPER,   UnifierConstants.DUST, new ItemStack(DUST_COPPER));
		unifier.add(UnifierConstants.SILVER,   UnifierConstants.DUST, new ItemStack(DUST_SILVER));
		unifier.add(UnifierConstants.LEAD,     UnifierConstants.DUST, new ItemStack(DUST_LEAD));
		unifier.add(UnifierConstants.ALUMINUM, UnifierConstants.DUST, new ItemStack(DUST_ALUMINUM));
		unifier.add(UnifierConstants.ZINC,     UnifierConstants.DUST, new ItemStack(DUST_ZINC));
		unifier.add(UnifierConstants.ELECTRUM, UnifierConstants.DUST, new ItemStack(DUST_ELECTRUM));
		unifier.add(UnifierConstants.INVAR,    UnifierConstants.DUST, new ItemStack(DUST_INVAR));
		unifier.add(UnifierConstants.BRASS,    UnifierConstants.DUST, new ItemStack(DUST_BRASS));
		//silvered lead doesn't get a unifier entry because it behaves differently from these established terms
		unifier.add(new ResourceLocation("thermionics:duralumin"), UnifierConstants.DUST, new ItemStack(DUST_DURALUMIN));
		*/
		
		OreDictionary.registerOre("ingotNickel",   INGOT_NICKEL);
		OreDictionary.registerOre("ingotCopper",   INGOT_COPPER);
		OreDictionary.registerOre("ingotSilver",   INGOT_SILVER);
		OreDictionary.registerOre("ingotLead",     INGOT_LEAD);
		OreDictionary.registerOre("ingotLeadAny",  INGOT_LEAD);
		OreDictionary.registerOre("ingotAluminum", INGOT_ALUMINUM);
		OreDictionary.registerOre("ingotZinc",     INGOT_ZINC);
		OreDictionary.registerOre("ingotElectrum", INGOT_ELECTRUM);
		OreDictionary.registerOre("ingotInvar",    INGOT_INVAR);
		OreDictionary.registerOre("ingotBrass",    INGOT_BRASS);
		OreDictionary.registerOre("ingotLeadAny",  INGOT_SILVERED_LEAD);
		OreDictionary.registerOre("ingotDuralumin",INGOT_DURALUMIN);
		
		/*
		unifier.add(UnifierConstants.NICKEL,   UnifierConstants.INGOT, new ItemStack(INGOT_NICKEL));
		unifier.add(UnifierConstants.COPPER,   UnifierConstants.INGOT, new ItemStack(INGOT_COPPER));
		unifier.add(UnifierConstants.SILVER,   UnifierConstants.INGOT, new ItemStack(INGOT_SILVER));
		unifier.add(UnifierConstants.LEAD,     UnifierConstants.INGOT, new ItemStack(INGOT_LEAD));
		unifier.add(UnifierConstants.ALUMINUM, UnifierConstants.INGOT, new ItemStack(INGOT_ALUMINUM));
		unifier.add(UnifierConstants.ZINC,     UnifierConstants.INGOT, new ItemStack(INGOT_ZINC));
		unifier.add(UnifierConstants.ELECTRUM, UnifierConstants.INGOT, new ItemStack(INGOT_ELECTRUM));
		unifier.add(UnifierConstants.INVAR,    UnifierConstants.INGOT, new ItemStack(INGOT_INVAR));
		unifier.add(UnifierConstants.BRASS,    UnifierConstants.INGOT, new ItemStack(INGOT_BRASS));
		unifier.add(new ResourceLocation("thermionics:duralumin"), UnifierConstants.INGOT, new ItemStack(INGOT_DURALUMIN));
		*/
		
		//Booze
		ThermionicsItems.EMPTY_SPIRIT_BOTTLE= item(r, new ItemSpiritBottle("empty"));
		ThermionicsItems.SPIRIT_BOTTLE      = item(r, new ItemSpiritBottle(null));
		
		//Cloaks
		ThermionicsItems.MISTCLOAK          = item(r, new ItemMistcloak());
		ThermionicsItems.SCARF              = item(r, new ItemScarf());
		
		//Creative
		ThermionicsItems.CHUNK_UNLOADER     = item(r, new ItemChunkUnloader());
	}
	
	public static <T extends Item> T item(IForgeRegistry<Item> registry, T t) {
		registry.register(t);
		Thermionics.instance().needModelRegistration.add(t);
		return t;
	}
}
