package com.elytradev.thermionics.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAuxDestroyBlock {
	public void onBlockAuxDestroyed(World world, IBlockState state, BlockPos pos, EntityPlayer player);
}
