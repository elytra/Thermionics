package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAxle extends BlockBase {
	public static final PropertyEnum<EnumFacing.Axis> AXIS = BlockRotatedPillar.AXIS;
	
	public BlockAxle(Material material, String id) {
		super(material);
		this.setRegistryName("axle."+id);
		this.setUnlocalizedName("thermionics.axle."+id);
		this.setHarvestLevel("pickaxe", 0);
		this.setLightOpacity(0);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
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
}
