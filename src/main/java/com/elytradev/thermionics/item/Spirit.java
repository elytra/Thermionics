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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Spirit implements IForgeRegistryEntry<Spirit> {
	private ResourceLocation registryName;
	private String name;
	private Ingredient mashBase;
	private int color = 0x60FFFFFF;
	
	public Spirit(String name, Ingredient mashBase) {
		this.registryName = new ResourceLocation(name);
		this.name = name;
		this.mashBase = mashBase;
	}
	
	public Spirit(String name, Item mashBase) {
		this(name, Ingredient.fromItem(mashBase));
	}
	
	public Spirit(String name, Block mashBase) {
		this(name, ItemBlock.getItemFromBlock(mashBase));
	}
	
	public Spirit(String name, String mashBase) {
		this.registryName = new ResourceLocation(name);
		this.name = name;
		this.mashBase = new OreIngredient(mashBase);
	}

	@Override
	public Spirit setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<Spirit> getRegistryType() {
		return Spirit.class;
	}

	public Ingredient getMashBase() {
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
		return "spirit."+name;
	}
	
	public String getUnlocalizedBrewedName() {
		return "hootch."+name;
	}
}