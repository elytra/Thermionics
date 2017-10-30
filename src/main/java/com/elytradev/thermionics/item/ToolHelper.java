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

import com.elytradev.thermionics.api.IAuxDestroyBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ToolHelper {
	
	/**
	 * Should be called both client- and server-side to remove a block.
	 * @param world The world the block is in
	 * @param pos The position of the block
	 * @param player The player removing the block
	 * @param canHarvest Whether the block should drop items
	 * @return True if the block was removed, false if its removal was cancelled or delayed.
	 */
	public static boolean removeBlock(World world, BlockPos pos, EntityPlayer player, boolean canHarvest) {
        IBlockState iblockstate = world.getBlockState(pos);
        boolean removed = iblockstate.getBlock().removedByPlayer(iblockstate, world, pos, player, canHarvest);

        if (removed) {
            iblockstate.getBlock().onBlockDestroyedByPlayer(world, pos, iblockstate);
        }

        return removed;
    }
	
	/**
	 * Destroys and tries to harvest a block with the currently active tool, except that instead of calling
	 * onBlockDestroyed, it calls onBlockAuxDestroyed on the tool, preventing infinite loops.
	 */
	public static boolean auxHarvestBlock(World world, BlockPos pos, EntityPlayerMP player) {
		if (world.isRemote) return false; //Shouldn't even be possible if we have an EntityPlayerMP!
		
		GameType gameType = player.interactionManager.getGameType();
		
		int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
        if (exp == -1) {
            return false;
        } else {
            IBlockState iblockstate = world.getBlockState(pos);
            if (iblockstate.getBlockHardness(world, pos)<0) return false;
            TileEntity tileentity = world.getTileEntity(pos);
            Block block = iblockstate.getBlock();

            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !player.canUseCommandBlock()) {
                world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
                return false;
            } else {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem().onBlockStartBreak(stack, pos, player)) return false;

                world.playEvent(player, 2001, pos, Block.getStateId(iblockstate));
                boolean removed = false;

                if (gameType==GameType.CREATIVE) {
                    removed = removeBlock(world, pos, player, false);
                    player.connection.sendPacket(new SPacketBlockChange(world, pos));
                } else {
                    ItemStack itemstack1 = player.getHeldItemMainhand();
                    ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
                    boolean canHarvest = iblockstate.getBlock().canHarvestBlock(world, pos, player);

                    if (!itemstack1.isEmpty()) {
                    //    itemstack1.onBlockDestroyed(world, iblockstate, pos, player);
	                    if (itemstack1.getItem() instanceof IAuxDestroyBlock) {
	                    	((IAuxDestroyBlock)itemstack1.getItem()).onBlockAuxDestroyed(world, iblockstate, pos, player);
	                    }
                    }
                    
                    removed = removeBlock(world, pos, player, canHarvest);
                    if (removed && canHarvest) {
                        iblockstate.getBlock().harvestBlock(world, player, pos, iblockstate, tileentity, itemstack2);
                    }
                }

                // Drop experience
                if (gameType!=GameType.CREATIVE && removed && exp > 0) {
                    iblockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
                }
                return removed;
            }
        }
	}
	
	
	public static boolean matchesOreName(String oreName, ItemStack stack) {
		if (!OreDictionary.doesOreNameExist(oreName)) return false;
		
		NonNullList<ItemStack> oreItems = OreDictionary.getOres(oreName);
		for(ItemStack oreItem : oreItems) {
			if (OreDictionary.itemMatches(stack, oreItem, false)) return true;
		}
		
		return false;
	}
}
