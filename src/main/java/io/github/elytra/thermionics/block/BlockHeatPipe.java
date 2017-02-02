package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.tileentity.TileEntityHeatStorage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHeatPipe extends Block implements ITileEntityProvider {

	public BlockHeatPipe() {
		super(Material.IRON);
		
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setRegistryName("thermionics", "machine.heatpipe");
		this.setUnlocalizedName("thermionics.machine.heatpipe");
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.blockState.getBaseState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHeatStorage();
	}
	
}
