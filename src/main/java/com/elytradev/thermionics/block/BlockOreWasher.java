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

import java.util.List;
import java.util.UUID;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.IArmor;
import com.elytradev.thermionics.gui.EnumGui;
import com.elytradev.thermionics.tileentity.TileEntityOreWasher;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockOreWasher extends BlockMachineBase implements ITileEntityProvider, IArmor {
	
	public BlockOreWasher() {
		super("orewasher");
		this.setLightOpacity(2);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(Thermionics.instance(), EnumGui.ORE_WASHER.id(), world, pos.getX(), pos.getY(), pos.getZ());
		} else {
			
		}
		return true;
	}
	
	
	
	//overrides light opacity if true >:|
	@Override
	public boolean isFullCube(IBlockState state) {
		return false; 
	}
	
	//controls hidden surface removal
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	
	//implements ITileEntityProvider {
		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new TileEntityOreWasher();
		}
	//}
	
	//implements IArmor {
		@Override
		public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
			return armorType==EntityEquipmentSlot.HEAD;
		}
	
		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
			Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
			if (slot==EntityEquipmentSlot.HEAD) {
				modifiers.put(
						SharedMonsterAttributes.ARMOR.getName(),
						new AttributeModifier(UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"), "Armor modifier", 1D, 0)
						);
	
			}
			
			return modifiers;
		}
	//}
}
