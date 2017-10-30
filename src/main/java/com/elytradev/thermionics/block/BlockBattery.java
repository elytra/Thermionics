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

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.IPreferredRenderState;
import com.elytradev.thermionics.tileentity.TileEntityBattery;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class BlockBattery extends BlockBase implements ITileEntityProvider, IPreferredRenderState {
	public static final int CAPACITY = 80_000;
	public static PropertyEnum<EnumFacing> FACING = BlockDirectional.FACING;
	
	public BlockBattery(String id) {
		super(Material.IRON);
		this.setRegistryName("battery."+id);
		this.setUnlocalizedName("thermionics.battery."+id);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.0f);
		this.setResistance(8f);
		
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.values()[(meta%EnumFacing.values().length)]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}
	
	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos) {
		return EnumFacing.values();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}
	
	/**
	 * A straightforward (vanilla-conforming) implementation for planar machines would be to rotate on only the axis
	 * requested, but in true contract-free form, we just loop through all valid states.
	 */
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState cur = world.getBlockState(pos);
		
		int next = cur.getValue(FACING).ordinal()+1;
		if (next>=EnumFacing.values().length) next=0;
		
		world.setBlockState(pos, cur.withProperty(FACING, EnumFacing.values()[next]));
		return true;
	}

	public ItemStack getBatteryItem(TileEntity te) {
		ItemStack result = new ItemStack(this);
		if (te==null) return result;
		
		if (te.hasCapability(CapabilityEnergy.ENERGY, null)) {
        	IEnergyStorage rf = te.getCapability(CapabilityEnergy.ENERGY, null);
        	if (!result.hasTagCompound()) result.setTagCompound(new NBTTagCompound());
        	NBTTagCompound tag = result.getTagCompound();
        	
        	tag.setTag("energy", CapabilityEnergy.ENERGY.getStorage().writeNBT(CapabilityEnergy.ENERGY, rf, null));
		}
		
		return result;
	}
	
	@Override
	public java.util.List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> result = new ArrayList<>();
		result.add(getBatteryItem(world.getTileEntity(pos)));
		return result;
	}
	
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		ItemStack toDrop = getBatteryItem(te);
		//System.out.println("toDrop: "+toDrop);
		spawnAsEntity(world, pos, toDrop);
		//world.removeTileEntity(pos);
    }
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("energy")) return;
		
		TileEntity te = world.getTileEntity(pos);
		if (te.hasCapability(CapabilityEnergy.ENERGY, null)) {
        	IEnergyStorage rf = te.getCapability(CapabilityEnergy.ENERGY, null);
        	
        	CapabilityEnergy.ENERGY.getStorage().readNBT(CapabilityEnergy.ENERGY, rf, null, stack.getTagCompound().getTag("energy"));
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBattery();
	}

	@Override
	public String getPreferredRenderState() {
		return "facing=up";
	}
}
