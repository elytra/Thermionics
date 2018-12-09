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

import java.util.Set;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IWrenchRemoval;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHandyRescueTool extends ItemTool implements IMetaItemModel {
	private static final Set<String> TOOL_CLASSES = ImmutableSet.of("axe", "wrench");
	private static final Set<Block> EFFECTIVE_ON = ImmutableSet.of( //Lifted from Vanilla. I *hate* vanilla sometimes.
			Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN,
			Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);
	private static final Set<String> DISMANTLE_WHITELIST = Sets.newHashSet(
			"thermaldynamics:duct_0", "thermaldynamics:duct_16", "thermaldynamics:duct_32" //Thermal Dynamics fluxducts, itemducts, and fluiducts
			);
	
	public ItemHandyRescueTool() {
		super(8.0f, -3.1f, ToolMaterial.IRON, EFFECTIVE_ON);
		
		this.setRegistryName(new ResourceLocation("thermionics","hrt"));
		this.setTranslationKey("thermionics.hrt");
		
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}

	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		Material material = state.getMaterial();
		
		if (material==Material.WOOD || material==Material.PLANTS || material==Material.VINE) return this.efficiency;
		
		/*try { //We catch logs just as well as vanilla axes without this horrible piece of nonsense
			if (state.getBlock().isWood(null, null)) return this.efficiency;
		} catch (Throwable t) {}*/
		
		return super.getDestroySpeed(stack, state);
	}
	
	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		return TOOL_CLASSES;
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.isRemote) return EnumActionResult.PASS;
		
		if (player.isSneaking()) {
			//shift-dismantle cables and scaffolds
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof IWrenchRemoval) {
				((IWrenchRemoval)state.getBlock()).wrenchRemove(world, pos, side);
				return EnumActionResult.SUCCESS;
			}
			
			//special-case some other mods' blocks
			if (DISMANTLE_WHITELIST.contains(state.getBlock().getRegistryName().toString())) {
				world.destroyBlock(pos, true);
			}
		}
		
		return EnumActionResult.PASS;
	}
	
	/*// Positioning still needs to be tweaked
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		if (armorType==EntityEquipmentSlot.HEAD) return true;
		return super.isValidArmor(stack, armorType, entity);
	}*/
	
	//implements IMetaItemModel {
		@Override
		public String[] getModelLocations() {
			return new String[] {"hrt"};
		}
	//}
}
