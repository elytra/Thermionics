package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.IPreferredRenderState;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMachineBase extends BlockBase implements IPreferredRenderState {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING; //Yes, I'm stealing it.
	private static final EnumFacing[] VALID_FACING = { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST };
	
	public BlockMachineBase(String id) {
		super(Material.IRON);
		this.setRegistryName("machine."+id);
		this.setUnlocalizedName("thermionics.machine."+id);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.4f);
		this.setResistance(8f); //actually quite susceptible to explosions
		
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ACTIVE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 0x03));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos) {
		return VALID_FACING;
	}
	
	/**
	 * A straightforward (vanilla-conforming) implementation for planar machines would be to discard rotation hits from
	 * the sides, but this might be hard for players to interpret. Instead, when asked to rotate, we proactively rotate
	 * around the Y axis. This should make things like charset and yotta wrench Just Work, even though machines don't
	 * know that they exist.
	 * 
	 * This may even cause rotations to work properly in charset block-carry. More research is needed.
	 */
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState cur = world.getBlockState(pos);
		
		world.setBlockState(pos, cur.withProperty(FACING, cur.getValue(FACING).rotateY()));
		return true;
	}

	@Override
	public String getPreferredRenderState() {
		return "active=false,facing=north";
	}
}
