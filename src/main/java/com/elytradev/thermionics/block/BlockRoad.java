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

package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRoad extends BlockBase {
	public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 5);
	private final int level;
	
	public BlockRoad(int level) {
		super(Material.ROCK, MapColor.QUARTZ);
		
		String registryName = "road";
		if (level>0) registryName+="."+level;
		this.setRegistryName(registryName);
		this.setTranslationKey("thermionics."+registryName);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.4f);
		this.setResistance(20f); //somewhat resistant to explosions
		this.level = level;
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT);
	}
	
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, meta);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if (world.isRemote) return;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase)entity;
			
			living.addPotionEffect(new PotionEffect(Thermionics.POTION_EFFORTLESS_SPEED, 20, level));
		}
	}
	
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		if (itemIn.equals(CreativeTabs.SEARCH) || itemIn.equals(this.getCreativeTab())) {
			getVariants(Item.getItemFromBlock(this), list);
		}
	}
	
	@Override
	public void getVariants(Item item, NonNullList<ItemStack> variants) {
		for(int i : VARIANT.getAllowedValues()) {
			variants.add(new ItemStack(item, 1, i));
		}
	}
}
