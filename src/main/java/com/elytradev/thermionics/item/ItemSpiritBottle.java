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

import javax.annotation.Nullable;

import com.elytradev.thermionics.api.Spirits;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class ItemSpiritBottle extends Item {
	public ItemSpiritBottle() {
		this.setRegistryName("thermionics", "spiritbottle");
		this.setUnlocalizedName("thermionics.spiritbottle");
	}
	
	@Nullable
	public Spirit getSpirit(ItemStack bottle) {
		if (bottle.hasTagCompound() && bottle.getTagCompound().hasKey("Spirit")) {
			return Spirits.REGISTRY.getValue(new ResourceLocation(bottle.getTagCompound().getString("Spirit")));
		} else {
			return null;
		}
	}
	
	@Nullable
	public Potion getPotion(ItemStack bottle) {
		if (bottle.hasTagCompound() && bottle.getTagCompound().hasKey("Potion")) {
			return Potion.REGISTRY.getObject(new ResourceLocation(bottle.getTagCompound().getString("Potion")));
		} else {
			return null;
		}
	}
	
	/**
	 * Applies all effects in the bottle to the entity; if the bottle has both a potion and a spirit, both are applied.
	 */
	public void apply(ItemStack bottle, EntityLiving entity) {
		Spirit spirit = getSpirit(bottle);
		if (spirit!=null) {
			//TODO: Drunkenness system
		}
		
		Potion potion = getPotion(bottle);
		if (potion!=null) {
			
		}
	}
}
