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
package com.elytradev.thermionics.block.behavior;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * May or may not remain; connectivity depends on getActualState, and I need to consider how getActualState would
 * function as a part of this concern.
 */
public class ConnectivityBlockStateBehavior implements BlockStateBehavior {
	public static final ConnectivityBlockStateBehavior INSTANCE = new ConnectivityBlockStateBehavior();
	
	public static PropertyBool NORTH = PropertyBool.create("north");
	public static PropertyBool EAST  = PropertyBool.create("east");
	public static PropertyBool SOUTH = PropertyBool.create("south");
	public static PropertyBool WEST  = PropertyBool.create("west");
	public static PropertyBool UP    = PropertyBool.create("up");
	public static PropertyBool DOWN  = PropertyBool.create("down");
	
	@Override
	public BlockStateContainer createBlockState(Block block) {
		return new BlockStateContainer(block, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	@Override
	public IBlockState getDefaultState(IBlockState baseState) {
		return baseState
				.withProperty(NORTH, false)
				.withProperty(SOUTH, false)
				.withProperty(EAST, false)
				.withProperty(WEST, false)
				.withProperty(UP, false)
				.withProperty(DOWN, false);
	}
	@Override
	public IBlockState getPlacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		return state;
	}
	@Override
	public IBlockState getStateFromMeta(Block block, int meta) {
		return block.getDefaultState();
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	
}
