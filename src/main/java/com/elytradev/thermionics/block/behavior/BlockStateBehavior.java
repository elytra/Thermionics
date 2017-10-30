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

package com.elytradev.thermionics.block.behavior;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockStateBehavior {
	public static final BlockStateBehavior NONE        = new None();
	public static final BlockStateBehavior VARIANT     = new Variant();
	public static final BlockStateBehavior HORIZONTAL  = new Horizontal();
	public static final BlockStateBehavior DIRECTIONAL = new Directional();
	
	/**
	 * Creates a new BlockStateContainer with the properties we'll need for this behavior
	 * @param block	the Block we're constructing
	 * @return A BlockStateContainer containing all appropriate BlockProperties for this behavior.
	 */
	public BlockStateContainer createBlockState(Block block);
	
	public IBlockState getDefaultState(IBlockState baseState);
	
	/**
	 * Determines which IBlockState is expressed when blocks governed by this behavior are placed.
	 * @param world	world the Block was placed in
	 * @param pos position the Block was placed at
	 * @param state the state that would be placed by default
	 * @param placer the entity that placed this block
	 * @param stack the ItemStack that was placed
	 * @return the IBlockState that should be expressed at this location
	 */
	public IBlockState getPlacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack);
	
	/**
	 * Determines how metadata values are mapped to IBlockStates
	 * @param block the block being queried
	 * @param meta the metadata value
	 * @return an IBlockState representing this value
	 */
	public IBlockState getStateFromMeta(Block block, int meta);
	
	/**
	 * Determines how IBlockStates are mapped to metadata values
	 * @param state the IBlockState to map to an integer
	 * @return an integer representing this state
	 */
	public int getMetaFromState(IBlockState state);
	
	
	
	
	
	static class None implements BlockStateBehavior {
		@Override
		public BlockStateContainer createBlockState(Block block) {
			return new BlockStateContainer(block);
		}
		
		@Override
		public IBlockState getDefaultState(IBlockState baseState) {
			return baseState;
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
	
	static class Variant implements BlockStateBehavior {
		public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);
		
		@Override
		public BlockStateContainer createBlockState(Block block) {
			return new BlockStateContainer(block, VARIANT);
		}

		@Override
		public IBlockState getDefaultState(IBlockState baseState) {
			return baseState.withProperty(VARIANT, 0);
		}
		
		@Override
		public IBlockState getPlacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
			return state;
		}

		@Override
		public IBlockState getStateFromMeta(Block block, int meta) {
			return block.getDefaultState().withProperty(VARIANT, meta);
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(VARIANT);
		}
		
	}
	
	static class Horizontal implements BlockStateBehavior {
		@Override
		public BlockStateContainer createBlockState(Block block) {
			return new BlockStateContainer(block, BlockHorizontal.FACING);
		}
		
		@Override
		public IBlockState getDefaultState(IBlockState baseState) {
			return baseState.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
		}
		
		@Override
		public IBlockState getPlacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
			return state.withProperty(BlockHorizontal.FACING, placer.getAdjustedHorizontalFacing().getOpposite());
		}

		@Override
		public IBlockState getStateFromMeta(Block block, int meta) {
			return block.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta & 0x03));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
		}
	}
	
	static class Directional implements BlockStateBehavior {
		@Override
		public BlockStateContainer createBlockState(Block block) {
			return new BlockStateContainer(block, BlockDirectional.FACING);
		}

		@Override
		public IBlockState getDefaultState(IBlockState baseState) {
			return baseState.withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
		}
		
		@Override
		public IBlockState getPlacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
			EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
			/*EnumFacing facing = EnumFacing.UP;
			Vec2f rot = placer.getPitchYaw();
			
			if (rot.x > 45) {
				facing = EnumFacing.DOWN;
			} else if (rot.x > -45) {
				facing = placer.getAdjustedHorizontalFacing();
			}*/
			
			return state.withProperty(BlockDirectional.FACING, facing);
		}

		@Override
		public IBlockState getStateFromMeta(Block block, int meta) {
			return block.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.getFront(meta));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(BlockDirectional.FACING).getIndex();
		}
		
	}
}
