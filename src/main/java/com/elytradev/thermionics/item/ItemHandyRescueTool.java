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

import java.util.List;
import java.util.Set;

import com.elytradev.thermionics.StringExtras;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IWrenchRemoval;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHandyRescueTool extends ItemTool implements IMetaItemModel {
	private static final Set<String> TOOL_CLASSES = ImmutableSet.of("axe", "wrench");
	private static final Set<Block> EFFECTIVE_ON = ImmutableSet.of( //Lifted from Vanilla. I *hate* vanilla sometimes.
			Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN,
			Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);
	private static final Set<String> DISMANTLE_WHITELIST = Sets.newHashSet(
			"thermaldynamics:duct_0", "thermaldynamics:duct_16", "thermaldynamics:duct_32" //Thermal Dynamics fluxducts, itemducts, and fluiducts
			);
	private static final Set<String> ROTATE_BLACKLIST = Sets.newHashSet(
			//"minecraft:wooden_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:oak_door", 
			);
	
	public ItemHandyRescueTool() {
		super(8.0f, -3.1f, ToolMaterial.IRON, EFFECTIVE_ON);
		
		this.setRegistryName(new ResourceLocation("thermionics","hrt"));
		this.setTranslationKey("thermionics.hrt");
		
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
		this.attackDamage = 0.5f;
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
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		return true; //Does not cost dura!
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return true; //HRT is invincible
	}
	
	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		return TOOL_CLASSES;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
        }

        return multimap;
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
		} else {
			//Try to rotate like a normal wrench
			IBlockState state = world.getBlockState(pos);
			if (!ROTATE_BLACKLIST.contains(state.getBlock().getRegistryName().toString()) && !(state.getBlock() instanceof BlockDoor)) {
				if (state.getBlock().rotateBlock(world, pos, side)) {
					return EnumActionResult.SUCCESS;
				}
			}
		}
		
		return EnumActionResult.PASS;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}
	
	@Override
	public int getItemEnchantability(ItemStack stack) {
		return 0;
	}
	
	@Override
	public int getItemEnchantability() {
		return 0;
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
		
		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
			StringExtras.addInformation("tooltip.thermionics.hrt", "§e", tooltip);
			StringExtras.addInformation("tooltip.thermionics.hrt.extra", "§9§o", tooltip);
		}
}
