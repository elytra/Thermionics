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
import com.elytradev.thermionics.data.IPreferredRenderState;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAxle extends BlockBase implements IPreferredRenderState {
	public static final PropertyEnum<EnumFacing.Axis> AXIS = BlockRotatedPillar.AXIS;
	
	private static final float PIXEL = 1/16f;
	private static final AxisAlignedBB AABB_X = new AxisAlignedBB(0,PIXEL*6,PIXEL*6,1,PIXEL*10,PIXEL*10);
	private static final AxisAlignedBB AABB_Y = new AxisAlignedBB(PIXEL*6,0,PIXEL*6,PIXEL*10,1,PIXEL*10);
	private static final AxisAlignedBB AABB_Z = new AxisAlignedBB(PIXEL*6,PIXEL*6,0,PIXEL*10,PIXEL*10,1);
	
	public BlockAxle(Material material, String id) {
		super(material);
		this.setRegistryName("axle."+id);
		this.setUnlocalizedName("thermionics.axle."+id);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.3f);
		this.setResistance(15.0f);
		this.setLightOpacity(0);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
	}
	
	public BlockAxle withHardness(float hardness) {
		setHardness(hardness);
		return this;
	}

	public BlockAxle withHarvestLevel(String toolClass, int level) {
		this.setHarvestLevel(toolClass, level);
		return this;
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AXIS);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AXIS).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[meta%EnumFacing.Axis.values().length]);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(AXIS, facing.getAxis());
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getBoundingBox(state, source, pos);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(AXIS)) {
		case X: return AABB_X;
		case Y: return AABB_Y;
		case Z: return AABB_Z;
		}
		return AABB_Y;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return getBoundingBox(state, world, pos).offset(pos);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (state.getValue(AXIS)!=EnumFacing.Axis.Y) return; //Only vertical axles are also firepoles
		if (entity instanceof EntityItem) return; //Items can't use firepoles
		
		if (entity.isSneaking()) {
			entity.motionY = 0.08; //Stop, but also counteract EntityLivingBase-applied microgravity
		} else if (entity.motionY<-0.40) {
			entity.motionY = -0.40;
		}
		entity.fallDistance = 0.0f;
	}
	
	//overrides light opacity if true >:|
	@Override
	public boolean isFullCube(IBlockState state) {
		return false; 
	}
	
	//controls hidden surface removal
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public String getPreferredRenderState() {
		return "axis=y";
	}
}
