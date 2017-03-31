package com.elytradev.thermionics.block;

import com.elytradev.thermionics.tileentity.TileEntityOven;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockOven extends BlockMachineBase implements ITileEntityProvider {

	public BlockOven() {
		super("oven");
		
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOven();
	}
}
