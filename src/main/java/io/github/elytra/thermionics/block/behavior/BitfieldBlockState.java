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
package io.github.elytra.thermionics.block.behavior;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.BlockStateContainer;
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

public class BitfieldBlockState extends BlockStateBase {
	private BlockStateContainer owner;
	long values;

	@Override
	public Block getBlock() {
		return owner.getBlock();
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

	@Override
	public void neighborChanged(World arg0, BlockPos arg1, Block arg2, BlockPos arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockEventReceived(World arg0, BlockPos arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addCollisionBoxToList(World arg0, BlockPos arg1, AxisAlignedBB arg2, List<AxisAlignedBB> arg3,
			Entity arg4, boolean arg5) {
		// TODO Auto-generated method stub
		
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
}

/*
static class StateImplementation extends BlockStateBase
{
	private final Block block;
	private final ImmutableMap < IProperty<?>, Comparable<? >> properties;
	private ImmutableTable < IProperty<?>, Comparable<?>, IBlockState > propertyValueTable;

	private StateImplementation(Block blockIn, ImmutableMap < IProperty<?>, Comparable<? >> propertiesIn)
	{
		this.block = blockIn;
		this.properties = propertiesIn;
	}

	public Collection < IProperty<? >> getPropertyNames()
	{
		return Collections. < IProperty<? >> unmodifiableCollection(this.properties.keySet());
	}

	public <T extends Comparable<T>> T getValue(IProperty<T> property)
	{
		Comparable<?> comparable = (Comparable)this.properties.get(property);

		if (comparable == null)
		{
			throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
		}
		else
		{
			return (T)((Comparable)property.getValueClass().cast(comparable));
		}
	}

	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
	{
		Comparable<?> comparable = (Comparable)this.properties.get(property);

		if (comparable == null)
		{
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
		}
		else if (comparable == value)
		{
			return this;
		}
		else
		{
			IBlockState iblockstate = (IBlockState)this.propertyValueTable.get(property, value);

			if (iblockstate == null)
			{
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(this.block) + ", it is not an allowed value");
			}
			else
			{
				return iblockstate;
			}
		}
	}

	public ImmutableMap < IProperty<?>, Comparable<? >> getProperties()
	{
		return this.properties;
	}

	public Block getBlock()
	{
		return this.block;
	}

	public boolean equals(Object p_equals_1_)
	{
		return this == p_equals_1_;
	}

	public int hashCode()
	{
		return this.properties.hashCode();
	}

	public void buildPropertyValueTable(Map < Map < IProperty<?>, Comparable<? >> , BlockStateContainer.StateImplementation > map)
	{
		if (this.propertyValueTable != null)
		{
			throw new IllegalStateException();
		}
		else
		{
			Table < IProperty<?>, Comparable<?>, IBlockState > table = HashBasedTable. < IProperty<?>, Comparable<?>, IBlockState > create();

			for (Entry < IProperty<?>, Comparable<? >> entry : this.properties.entrySet())
			{
				IProperty<?> iproperty = (IProperty)entry.getKey();

				for (Comparable<?> comparable : iproperty.getAllowedValues())
				{
					if (comparable != entry.getValue())
					{
						table.put(iproperty, comparable, map.get(this.getPropertiesWithValue(iproperty, comparable)));
					}
				}
			}

			this.propertyValueTable = ImmutableTable. < IProperty<?>, Comparable<?>, IBlockState > copyOf(table);
		}
	}

	private Map < IProperty<?>, Comparable<? >> getPropertiesWithValue(IProperty<?> property, Comparable<?> value)
	{
		Map < IProperty<?>, Comparable<? >> map = Maps. < IProperty<?>, Comparable<? >> newHashMap(this.properties);
		map.put(property, value);
		return map;
	}

	public Material getMaterial()
	{
		return this.block.getMaterial(this);
	}

	public boolean isFullBlock()
	{
		return this.block.isFullBlock(this);
	}

	public boolean func_189884_a(Entity p_189884_1_)
	{
		return this.block.func_189872_a(this, p_189884_1_);
	}

	public int getLightOpacity()
	{
		return this.block.getLightOpacity(this);
	}

	public int getLightValue()
	{
		return this.block.getLightValue(this);
	}

	public boolean isTranslucent()
	{
		return this.block.isTranslucent(this);
	}

	public boolean useNeighborBrightness()
	{
		return this.block.getUseNeighborBrightness(this);
	}

	public MapColor getMapColor()
	{
		return this.block.getMapColor(this);
	}

	public IBlockState withRotation(Rotation rot)
	{
		return this.block.withRotation(this, rot);
	}

	public IBlockState withMirror(Mirror mirrorIn)
	{
		return this.block.withMirror(this, mirrorIn);
	}

	public boolean isFullCube()
	{
		return this.block.isFullCube(this);
	}

	public EnumBlockRenderType getRenderType()
	{
		return this.block.getRenderType(this);
	}

	public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos)
	{
		return this.block.getPackedLightmapCoords(this, source, pos);
	}

	public float getAmbientOcclusionLightValue()
	{
		return this.block.getAmbientOcclusionLightValue(this);
	}

	public boolean isBlockNormalCube()
	{
		return this.block.isBlockNormalCube(this);
	}

	public boolean isNormalCube()
	{
		return this.block.isNormalCube(this);
	}

	public boolean canProvidePower()
	{
		return this.block.canProvidePower(this);
	}

	public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return this.block.getWeakPower(this, blockAccess, pos, side);
	}

	public boolean hasComparatorInputOverride()
	{
		return this.block.hasComparatorInputOverride(this);
	}

	public int getComparatorInputOverride(World worldIn, BlockPos pos)
	{
		return this.block.getComparatorInputOverride(this, worldIn, pos);
	}

	public float getBlockHardness(World worldIn, BlockPos pos)
	{
		return this.block.getBlockHardness(this, worldIn, pos);
	}

	public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos)
	{
		return this.block.getPlayerRelativeBlockHardness(this, player, worldIn, pos);
	}

	public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return this.block.getStrongPower(this, blockAccess, pos, side);
	}

	public EnumPushReaction getMobilityFlag()
	{
		return this.block.getMobilityFlag(this);
	}

	public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos)
	{
		return this.block.getActualState(this, blockAccess, pos);
	}

	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
	{
		return this.block.getSelectedBoundingBox(this, worldIn, pos);
	}

	public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing)
	{
		return this.block.shouldSideBeRendered(this, blockAccess, pos, facing);
	}

	public boolean isOpaqueCube()
	{
		return this.block.isOpaqueCube(this);
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos)
	{
		return this.block.getCollisionBoundingBox(this, worldIn, pos);
	}

	public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_, @Nullable Entity p_185908_5_)
	{
		this.block.addCollisionBoxToList(this, worldIn, pos, p_185908_3_, p_185908_4_, p_185908_5_);
	}

	public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos)
	{
		return this.block.getBoundingBox(this, blockAccess, pos);
	}

	public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		return this.block.collisionRayTrace(this, worldIn, pos, start, end);
	}

	public boolean isFullyOpaque()
	{
		return this.block.isFullyOpaque(this);
	}

	public boolean func_189547_a(World p_189547_1_, BlockPos p_189547_2_, int p_189547_3_, int p_189547_4_)
	{
		return this.block.func_189539_a(this, p_189547_1_, p_189547_2_, p_189547_3_, p_189547_4_);
	}

	public void func_189546_a(World p_189546_1_, BlockPos p_189546_2_, Block p_189546_3_)
	{
		this.block.func_189540_a(this, p_189546_1_, p_189546_2_, p_189546_3_);
	}
}
}*/