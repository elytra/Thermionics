package com.elytradev.thermionics.block;

import com.elytradev.thermionics.tileentity.TileEntityRFMotor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRFMotor extends BlockMachineBase implements ITileEntityProvider {

	public BlockRFMotor() {
		super("rf_motor");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRFMotor();
	}
	
	/*//Uncomment if we get a good model for this
	//overrides light opacity if true >:|
	@Override
	public boolean isFullCube(IBlockState state) {
		return false; 
	}
	
	//controls hidden surface removal
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}*/
}
