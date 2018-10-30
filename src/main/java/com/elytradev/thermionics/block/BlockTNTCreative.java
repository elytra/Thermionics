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

import com.elytradev.libasplod.BigExplosion;
import com.elytradev.libasplod.BigExplosionHandler;
import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTNTCreative extends BlockBase {

	public BlockTNTCreative() {
		super(Material.TNT);
		this.setRegistryName("creativetnt");
		this.setTranslationKey("thermionics.creativetnt");
		this.setHardness(0.5f);
		this.setResistance(1f);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}
	
	
	//@Override
	//public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		//IBlockState above = world.getBlockState(pos.up());
		//if (above.getBlock().isBurning(world, pos.up())) triggerExplosion(world, pos);
	//}
	
	@Override
	public void observedNeighborChange(IBlockState state, World world, BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
		if (world.isRemote) return;
		
		if (ignitesThings(world, changedBlockPos) || world.isBlockPowered(pos)) {
			triggerExplosion(world, pos);
		}
	}

	public static boolean ignitesThings(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial()==Material.FIRE) return true;
		if (state.getBlock()==Blocks.FIRE) return true;
		if (state.getBlock().isBurning(world, pos)) return true;
		
		return false;
	}
	
	public void triggerExplosion(World world, BlockPos pos) {
		System.out.println("BOOM!");
		world.setBlockToAir(pos);
		BigExplosionHandler.instance().getOrCreateScheduler(world).schedule(new BigExplosion(pos, 80, 80));
	}
}
