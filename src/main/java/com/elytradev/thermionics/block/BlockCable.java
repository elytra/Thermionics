package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.tileentity.TileEntityCable;
import com.elytradev.thermionics.tileentity.TileEntityHeatStorage;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class BlockCable extends BlockBase implements ITileEntityProvider {
	public static PropertyBool NORTH = PropertyBool.create("north");
	public static PropertyBool EAST  = PropertyBool.create("east");
	public static PropertyBool SOUTH = PropertyBool.create("south");
	public static PropertyBool WEST  = PropertyBool.create("west");
	public static PropertyBool UP    = PropertyBool.create("up");
	public static PropertyBool DOWN  = PropertyBool.create("down");
	
	public BlockCable(String subId) {
		super(Material.CLOTH);
		this.setLightOpacity(0);
		this.setRegistryName("cable."+subId);
		this.setUnlocalizedName("thermionics.cable."+subId);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
		//Creepers, withers, and exploding machines should be pretty catastrophic to these blocks.
		this.setHardness(1.0f);
		this.setResistance(8.0f);
		
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getBoundingBox(state, source, pos);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getActualState(state, world, pos);
		
		float pixel = 1f/16f;
		float min = pixel*5;
		float max = 1 - min;
		
		float x1 = min;
		float y1 = min;
		float z1 = min;
		float x2 = max;
		float y2 = max;
		float z2 = max;
		
		if (actualState.getValue(NORTH)) z1 = 0;
		if (actualState.getValue(WEST))  x1 = 0;
		if (actualState.getValue(DOWN))  y1 = 0;
		if (actualState.getValue(EAST))  x2 = 1;
		if (actualState.getValue(SOUTH)) z2 = 1;
		if (actualState.getValue(UP))    y2 = 1;
		
		return new AxisAlignedBB(x1,y1,z1,x2,y2,z2);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return getBoundingBox(state, world, pos).offset(pos);
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
		return this.getDefaultState();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state
				.withProperty(NORTH, isRFCapable(state, world, pos.north(), EnumFacing.SOUTH))
				.withProperty(EAST,  isRFCapable(state, world, pos.east(),  EnumFacing.WEST))
				.withProperty(SOUTH, isRFCapable(state, world, pos.south(), EnumFacing.NORTH))
				.withProperty(WEST,  isRFCapable(state, world, pos.west(),  EnumFacing.EAST))
				.withProperty(DOWN,  isRFCapable(state, world, pos.down(),  EnumFacing.UP))
				.withProperty(UP,    isRFCapable(state, world, pos.up(),    EnumFacing.DOWN));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCable();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	public static boolean isRFCapable(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (state.getBlock().hasTileEntity(state)) {
			TileEntity te = world.getTileEntity(pos);
			try {
				if (te.hasCapability(CapabilityEnergy.ENERGY, side)) return true;
			} catch (Throwable t) {
				return false; //If it hates us, don't connect. Capabilities are funny.
			}
		}
		return false;
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
