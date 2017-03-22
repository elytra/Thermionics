package com.elytradev.thermionics.block;

import java.util.List;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffold extends BlockBase {
	public static final AxisAlignedBB WITHDRAWN_SIDES_AABB = new AxisAlignedBB(0.005f, 0.0f, 0.005f, 0.995f, 1.0f, 0.995f);

	public BlockScaffold(String subId) {
		super(Material.IRON);
		this.setLightOpacity(0);
		this.setRegistryName("scaffold."+subId);
		this.setUnlocalizedName("thermionics.scaffold."+subId);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
		this.setHardness(1.5f);
		this.setResistance(20.0f);
		this.setHarvestLevel("pickaxe", 0);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	
	/* Not used that much but here for consistency. Probably a raytrace thing. */
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.005f, 0.0f, 0.005f, 0.995f, 1.0f, 0.995f);
	}
	
	/* Used for addCollisionBoxToList, so this is what really determines our shape for physics */
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WITHDRAWN_SIDES_AABB;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess World, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityItem) return;
		
		if (entity.isCollidedHorizontally) {
			entity.motionY = 0.35;
		} else if (entity.isSneaking()) {
			entity.motionY = 0.08; //Stop, but also counteract EntityLivingBase-applied microgravity
		} else if (entity.motionY<-0.20) {
			entity.motionY = -0.20;
		}
		entity.fallDistance = 0.0f;
	}
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}
}
