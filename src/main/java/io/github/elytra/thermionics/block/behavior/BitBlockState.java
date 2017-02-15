/**
 * MIT License
 *
 * Copyright (c) 2017 The Isaac Ellingson (Falkreon) and contributors
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
package io.github.elytra.thermionics.block.behavior;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BitBlockState implements IBlockState {
	Block block;
	long bits = 0L;
	
	@Override
	public void neighborChanged(World world, BlockPos pos, Block block, BlockPos pos2) {
		this.block.neighborChanged(this, world, pos, block, pos2);
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, int event, int data) {
		return block.eventReceived(this, world, pos, event, data);
	}

	@Override
	public void addCollisionBoxToList(World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity, boolean stuff) {
		block.addCollisionBoxToList(this, world, pos, aabb, list, entity, stuff);
	}

	@Override
	public boolean canEntitySpawn(Entity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canProvidePower() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean causesSuffocation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RayTraceResult collisionRayTrace(World arg0, BlockPos arg1, Vec3d arg2, Vec3d arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockAccess arg0, BlockPos arg1, EnumFacing arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBlockState getActualState(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getAmbientOcclusionLightValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getBlockHardness(World arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getComparatorInputOverride(World arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLightOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLightOpacity(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLightValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLightValue(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MapColor getMapColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumPushReaction getMobilityFlag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3d getOffset(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPackedLightmapCoords(IBlockAccess arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer arg0, World arg1, BlockPos arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EnumBlockRenderType getRenderType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World arg0, BlockPos arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStrongPower(IBlockAccess arg0, BlockPos arg1, EnumFacing arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWeakPower(IBlockAccess arg0, BlockPos arg1, EnumFacing arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCustomBreakingProgress() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFullBlock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFullCube() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFullyOpaque() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNormalCube() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess arg0, BlockPos arg1, EnumFacing arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTranslucent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess arg0, BlockPos arg1, EnumFacing arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useNeighborBrightness() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBlockState withMirror(Mirror arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBlockState withRotation(Rotation arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IProperty<?>> getPropertyKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Comparable<T>> T getValue(IProperty<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> arg0, V arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
