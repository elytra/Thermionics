/**
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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;

public class ItemMistcloak extends ItemBodyBauble implements IMetaItemModel {
	private static final String NAME_TASSELCLOAK = "item.thermionics.bauble.tasselcloak";
	private static final String NAME_MISTCLOAK = "item.thermionics.bauble.mistcloak";
	private final Potion POTION_INVIS;
	
	public ItemMistcloak() {
		super("cloak");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
		POTION_INVIS = Potion.getPotionFromResourceLocation("minecraft:invisibility");
	}

	public boolean isAllomantic(ItemStack stack) {
		return (stack.getItemDamage()==1);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (isAllomantic(stack)) ? NAME_MISTCLOAK : NAME_TASSELCLOAK;
	}
	
	public ItemStack createMistcloak() {
		return new ItemStack(this, 1, 1);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab.equals(this.getCreativeTab())) {
			list.add(new ItemStack(this, 1, 0));
			list.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getItemDamage()==1;
	}
	
	@Override
	public String[] getModelLocations() {
		return new String[]{ "bauble.cloak.tasselcloak", "bauble.cloak.mistcloak" };
	}
	
	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player.world.isRemote) return;
		
		if (stack.getItemDamage()==1) {
			
			if (player.getBrightness() < 0.4375f) {
				player.addPotionEffect(new PotionEffect(POTION_INVIS, 5));
			}
		}
	}
}
