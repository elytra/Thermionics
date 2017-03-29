package com.elytradev.thermionics.block;

import com.elytradev.thermionics.tileentity.TileEntityBatteryCreative;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBatteryCreative extends BlockBattery {
	public BlockBatteryCreative() {
		super("creative");
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBatteryCreative();
	}
}
