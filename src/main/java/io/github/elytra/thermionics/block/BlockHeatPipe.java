/**
 * MIT License
 *
 * Copyright (c) 2017 The Elytra Team
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
package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.tileentity.TileEntityHeatStorage;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHeatPipe extends InspectableBlock implements ITileEntityProvider {
	public static PropertyBool NORTH = PropertyBool.create("north");
	public static PropertyBool EAST  = PropertyBool.create("east");
	public static PropertyBool SOUTH = PropertyBool.create("south");
	public static PropertyBool WEST  = PropertyBool.create("west");
	public static PropertyBool UP    = PropertyBool.create("up");
	public static PropertyBool DOWN  = PropertyBool.create("down");
	
	public BlockHeatPipe() {
		super(Material.IRON, "machine.heatpipe");
		
		this.setLightOpacity(0);
		
		this.setDefaultState(blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(SOUTH, false)
				.withProperty(EAST, false)
				.withProperty(WEST, false)
				.withProperty(UP, false)
				.withProperty(DOWN, false)
				);
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.blockState.getBaseState();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state
				.withProperty(NORTH, isHeatConductor(world,pos.north(), EnumFacing.SOUTH))
				.withProperty(EAST,  isHeatConductor(world,pos.east(),  EnumFacing.WEST))
				.withProperty(SOUTH, isHeatConductor(world,pos.south(), EnumFacing.NORTH))
				.withProperty(WEST,  isHeatConductor(world,pos.west(),  EnumFacing.EAST))
				.withProperty(DOWN,  isHeatConductor(world,pos.down(),  EnumFacing.UP))
				.withProperty(UP,    isHeatConductor(world,pos.up(),    EnumFacing.DOWN))
				;
	}
	
	private boolean isHeatConductor(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return false;
		return te.hasCapability(Thermionics.CAPABILITY_HEATSTORAGE, side);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHeatStorage();
	}
	
	//Does not control hidden surface removal
	@Override
	public boolean isFullCube(IBlockState state) {
		return false; 
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
