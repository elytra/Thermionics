package com.elytradev.thermionics.block;

import com.elytradev.thermionics.tileentity.TileEntityConvectionMotor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockConvectionMotor extends BlockMachineBase implements ITileEntityProvider {

	public BlockConvectionMotor() {
		super("convectionmotor");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConvectionMotor();
	}
}
