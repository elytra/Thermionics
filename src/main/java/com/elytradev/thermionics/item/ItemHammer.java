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
import java.util.Set;

import com.elytradev.thermionics.Thermionics;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHammer extends ItemTool {

	public ItemHammer(ToolMaterial materialIn, String materialName) {
		super(4, 2, materialIn, Sets.newHashSet());
		
		this.setRegistryName(new ResourceLocation("thermionics","hammer."+materialName));
		this.setUnlocalizedName("thermionics.hammer."+materialName);
		
		this.efficiencyOnProperMaterial = 4.0F;
        this.toolMaterial = materialIn;
        this.maxStackSize = 1;
        this.setMaxDamage(materialIn.getMaxUses() * 9); //TODO: This depends on recipe; may be 18+ if two blocks are used
        this.efficiencyOnProperMaterial = materialIn.getEfficiencyOnProperMaterial();
        this.damageVsEntity = 4 + materialIn.getDamageVsEntity();
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
	
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer)) return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
		EntityPlayer player = (EntityPlayer)entityLiving;
		
        if (!world.isRemote && state.getBlockHardness(world, pos) != 0) {
            stack.damageItem(1, player);
            
            int fortuneLevel = 0; //Todo: Allow hammers to have Fortune
            
            ArrayList<BlockPos> toHarvest = new ArrayList<>();
            
            //check and break anything nearby of equal or lower hardness
            for(int z=-1; z<=1; z++) {
            	for(int y=-1; y<=1; y++) {
            		for(int x=-1; x<=1; x++) {
            			if (x==0 && y==0 && z==0) continue;
            			
            			BlockPos crushLocation = pos.add(x, y, z);
            			IBlockState toCrush = world.getBlockState(crushLocation);
            			if (toCrush.getBlockHardness(world, crushLocation) <= state.getBlockHardness(world, pos)) {
            				if (toCrush.getBlock().canHarvestBlock(world, crushLocation, player)) {
            					toHarvest.add(crushLocation);
            					
            					
            					stack.damageItem(1, entityLiving);
            				}
            				
            			}
            		}
            	}
            }
            
            for(BlockPos cur : toHarvest) {
            	IBlockState crushed = world.getBlockState(cur);
            	TileEntity te = world.getTileEntity(cur);
            	boolean doHarvest = crushed.getBlock().removedByPlayer(crushed, world, cur, player, true);
            	if (doHarvest) {
            		crushed.getBlock().harvestBlock(world, player, cur, state, world.getTileEntity(cur), stack);
            	}
            }
        }
        
        return true;
    }
}
