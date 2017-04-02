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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IAuxDestroyBlock;
import com.elytradev.thermionics.api.IOreRepair;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHammer extends ItemTool implements IAuxDestroyBlock, IOreRepair {
	String fakeToolMaterial = "ingotIron";
	
	//Whoever in Mojang specialcased literally every effective pickaxe block inside ItemPickaxe can die in a fire.
	private static Set<Block> VANILLA_WHITELIST = Sets.newHashSet(new Block[] {
			Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE,
			Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL,
			Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB,
			Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE});

	
	private static HashSet<Block> BLACKLIST = new HashSet<>();
	private static HashSet<Block> WHITELIST = new HashSet<>();
	static {
		BLACKLIST.add(Blocks.TORCH);
		BLACKLIST.add(Blocks.STANDING_BANNER);
		BLACKLIST.add(Blocks.WALL_BANNER);
		BLACKLIST.add(Blocks.STANDING_SIGN);
		BLACKLIST.add(Blocks.WALL_SIGN);
		
		WHITELIST.addAll(VANILLA_WHITELIST);
	}
	
	public ItemHammer(ToolMaterial materialIn, String materialName) {
		super(4, -2, materialIn, Sets.newHashSet());
		
		this.setRegistryName(new ResourceLocation("thermionics","hammer."+materialName));
		this.setUnlocalizedName("thermionics.hammer."+materialName);
		
        this.toolMaterial = materialIn;
        this.maxStackSize = 1;
        this.setMaxDamage(materialIn.getMaxUses() * 9);
        this.efficiencyOnProperMaterial = materialIn.getEfficiencyOnProperMaterial();
        this.damageVsEntity = 4 + materialIn.getDamageVsEntity();
        this.attackSpeed = 2;
        this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}
	
	public ItemHammer(String id, String fakeToolMaterial, int level, int uses, float efficiency, float damage, int enchantability) {
		super(4, -2, ToolMaterial.IRON, Sets.newHashSet()); 
		this.fakeToolMaterial = fakeToolMaterial;
		
		this.setRegistryName(new ResourceLocation("thermionics","hammer."+id));
		this.setUnlocalizedName("thermionics.hammer."+id);
		
        this.toolMaterial = ToolMaterial.IRON;
        this.maxStackSize = 1;
        this.setMaxDamage(uses * 9);
        this.efficiencyOnProperMaterial = efficiency;
        this.damageVsEntity = 4 + damage;
        this.attackSpeed = 2;
        this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
		String harvestTool = blockIn.getBlock().getHarvestTool(blockIn);
		if (harvestTool==null) return true;
		
		boolean isCorrectTool = harvestTool.equals("pickaxe") || harvestTool.equals("hammer");
		boolean isCorrectLevel = blockIn.getBlock().getHarvestLevel(blockIn) <= this.toolMaterial.getHarvestLevel();
		
		return isCorrectTool && isCorrectLevel;
	}
	
	@Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("pickaxe", "hammer");
    }
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer)) return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
		EntityPlayer player = (EntityPlayer)entityLiving;
		
        if (!world.isRemote && state.getBlockHardness(world, pos) != 0) {
            stack.damageItem(1, player);
            
            ArrayList<BlockPos> crushGroup = new ArrayList<>();
            
            //check and break anything nearby of equal or lower hardness
            for(int z=-1; z<=1; z++) {
            	for(int y=-1; y<=1; y++) {
            		for(int x=-1; x<=1; x++) {
            			if (x==0 && y==0 && z==0) continue;
            			BlockPos crushLocation = pos.add(x, y, z);
            			IBlockState toCrush = world.getBlockState(crushLocation);
            			
            			if (toCrush.getBlockHardness(world, crushLocation) <= state.getBlockHardness(world, pos)) {
            				if (!BLACKLIST.contains(toCrush.getBlock())) {
	            				crushGroup.add(crushLocation);
	            				stack.damageItem(1, entityLiving);
            				}
            			}
            		}
            	}
            }
            
            for(BlockPos cur : crushGroup) {
            	if (player instanceof EntityPlayerMP) { //Should be true any time we reach this part of the code
    				ToolHelper.auxHarvestBlock(world, cur, (EntityPlayerMP)player);
    			}
            }
        }
        
        return true;
    }
	
	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (fakeToolMaterial!=null) {
			return ToolHelper.matchesOreName(fakeToolMaterial, repair);
		} else {
			return super.getIsRepairable(toRepair, repair);
		}
    }
	
	@Override
	public void onBlockAuxDestroyed(World world, IBlockState state, BlockPos pos, EntityPlayer player) {
		// Thermionics hammers don't do anything special here. But you could.
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		//Certain materials are just effective with a pickaxe, and by extension, hammers.
        Material material = state.getMaterial();
        if (material == Material.IRON || material == Material.ANVIL || material == Material.ROCK) return this.efficiencyOnProperMaterial;
        
        if (WHITELIST.contains(state.getBlock())) return this.efficiencyOnProperMaterial;
        else return super.getStrVsBlock(stack, state);
    }

	@Override
	public String getOreRepairMaterial() {
		return fakeToolMaterial;
	}
}
