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
package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCableSignal extends BlockCableBase {
	
	public BlockCableSignal(String subId) {
		super(subId);
		
		this.setDefaultState(blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(SOUTH, false)
				.withProperty(EAST, false)
				.withProperty(WEST, false)
				.withProperty(UP, false)
				.withProperty(DOWN, false)
				.withProperty(BlockColored.COLOR, EnumDyeColor.WHITE)
				);
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockColored.COLOR, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockColored.COLOR).getMetadata();
	}
	
	@Override
	public boolean canConnectCable(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		//prefer Signal
		if (state.getBlock().hasTileEntity(state)) {
			TileEntity te = world.getTileEntity(pos);
			try {
				if (te.hasCapability(Thermionics.CAPABILITY_SIGNALSTORAGE, side)) return true;
			} catch (Throwable t) {
				//If it hates us, don't connect. Capabilities are funny.
			}
		}
		
		return state.getBlock().canConnectRedstone(state, world, pos, side);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}
}
