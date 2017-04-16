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
package com.elytradev.thermionics.data;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public enum EnumDyeSource {
	DANDELION   ( BlockFlower.EnumFlowerType.DANDELION,     EnumDyeColor.YELLOW    ),
    POPPY       ( BlockFlower.EnumFlowerType.POPPY,         EnumDyeColor.RED       ),
    BONE        ( new ItemStack(Items.BONE),                EnumDyeColor.WHITE,       3 ),
    BONE_BLOCK  ( new ItemStack(Blocks.BONE_BLOCK),         EnumDyeColor.WHITE,       9 ),
    BLUE_ORCHID ( BlockFlower.EnumFlowerType.BLUE_ORCHID,   EnumDyeColor.LIGHT_BLUE),
    ALLIUM      ( BlockFlower.EnumFlowerType.ALLIUM,        EnumDyeColor.MAGENTA   ),
    HOUSTONIA   ( BlockFlower.EnumFlowerType.HOUSTONIA,     EnumDyeColor.SILVER    ),
    RED_TULIP   ( BlockFlower.EnumFlowerType.RED_TULIP,     EnumDyeColor.RED       ),
    ORANGE_TULIP( BlockFlower.EnumFlowerType.ORANGE_TULIP,  EnumDyeColor.ORANGE    ),
    WHITE_TULIP ( BlockFlower.EnumFlowerType.WHITE_TULIP,   EnumDyeColor.SILVER    ),
    PINK_TULIP  ( BlockFlower.EnumFlowerType.PINK_TULIP,    EnumDyeColor.PINK      ),
    OXEYE_DAISY ( BlockFlower.EnumFlowerType.OXEYE_DAISY,   EnumDyeColor.SILVER    ),
    SUNFLOWER   ( BlockDoublePlant.EnumPlantType.SUNFLOWER, EnumDyeColor.YELLOW    ),
    SYRINGA     ( BlockDoublePlant.EnumPlantType.SYRINGA,   EnumDyeColor.MAGENTA   ),
    ROSE        ( BlockDoublePlant.EnumPlantType.ROSE,      EnumDyeColor.RED       ),
    PAEONIA     ( BlockDoublePlant.EnumPlantType.PAEONIA,   EnumDyeColor.PINK      ),
    BEETROOT    ( new ItemStack(Items.BEETROOT),            EnumDyeColor.RED,         1 ),
	;
	
	private final ItemStack exemplar;
	private final EnumDyeColor dyeColor;
	private final int quantity;
	
	EnumDyeSource(BlockFlower.EnumFlowerType singleFlowerType, EnumDyeColor dyeColor) {
		this.exemplar = new ItemStack(singleFlowerType.getBlockType().getBlock(), 1, singleFlowerType.getMeta());
		this.dyeColor = dyeColor;
		this.quantity = 1;
	}
	
	EnumDyeSource(BlockDoublePlant.EnumPlantType doubleFlowerType, EnumDyeColor dyeColor) {
		this.exemplar = new ItemStack(Blocks.DOUBLE_PLANT, 1, doubleFlowerType.getMeta());
		this.dyeColor = dyeColor;
		this.quantity = 2;
	}
	
	EnumDyeSource(ItemStack exemplar, EnumDyeColor dyeColor, int quantity) {
		this.exemplar = exemplar;
		this.dyeColor = dyeColor;
		this.quantity = quantity;
	}
	
	public ItemStack getExemplar() {
		return exemplar;
	}
	
	public ItemStack createOutputStack() {
		return new ItemStack(Items.DYE, quantity, dyeColor.getDyeDamage());
	}
}
