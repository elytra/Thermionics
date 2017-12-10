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

import com.elytradev.concrete.recipe.ItemIngredient;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class Spirit {
	private String name;
	private ItemIngredient mashBase;
	private int color = 0x60FFFFFF;
	private Clarity clarity = Clarity.MEDIUM;
	
	public Spirit(String name, ItemIngredient mashBase) {
		this.name = name;
		this.mashBase = mashBase;
	}
	
	public Spirit(String name, Item mashBase) {
		this(name, ItemIngredient.of(mashBase));
	}
	
	public Spirit(String name, Block mashBase) {
		this(name, ItemBlock.getItemFromBlock(mashBase));
	}
	
	public Spirit(String name, String mashBase) {
		this.name = name;
		this.mashBase = ItemIngredient.of(mashBase);
	}

	public ItemIngredient getMashBase() {
		return mashBase;
	}

	public Spirit withColor(int color) {
		this.color = color;
		return this;
	}
	
	public int getColor() {
		return color;
	}

	public String getUnlocalizedDistilledName() {
		return "spirit.thermionics."+name;
	}
	
	public String getUnlocalizedBrewedName() {
		return "hootch.thermionics."+name;
	}
	
	public Clarity getClarity() {
		return clarity;
	}
	
	public Spirit withClarity(Clarity clarity) {
		this.clarity = clarity;
		return this;
	}
	
	public enum Clarity {
		CLEAR("fluids/"),
		MEDIUM("fluids/"),
		DARK("fluids/");
		
		private final ResourceLocation still_hootch;
		private final ResourceLocation still_spirit;
		
		Clarity(String loc) {
			this.still_hootch = new ResourceLocation("thermionics", loc+"hootch");
			this.still_spirit = new ResourceLocation("thermionics", loc+"spirit");
		}
		
		public ResourceLocation getStillHootchIcon() {
			return still_hootch;
		}
		
		public ResourceLocation getStillSpiritIcon() {
			return still_spirit;
		}
		
		public ResourceLocation getFlowingHootchIcon() {
			return still_hootch;
		}
		
		public ResourceLocation getFlowingSpiritIcon() {
			return still_spirit;
		}
	}
}