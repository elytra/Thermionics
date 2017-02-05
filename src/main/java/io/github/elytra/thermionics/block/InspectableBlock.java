package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.api.IHeatStorage;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public class InspectableBlock extends Block implements IProbeInfoAccessor {

	public InspectableBlock(Material material, String id) {
		super(material);
		
		//this.setHarvestLevel("pickaxe", 1);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setRegistryName("thermionics", id);
		this.setUnlocalizedName("thermionics."+id);
	}

	@Optional.Method(modid="theoneprobe")
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData hitData) {
		TileEntity te = world.getTileEntity(hitData.getPos());
		if (te==null) return;
		
		if (te.hasCapability(Thermionics.CAPABILITY_HEATSTORAGE, null)) {
			
			IHeatStorage storage = te.getCapability(Thermionics.CAPABILITY_HEATSTORAGE, null);
			ProgressStyle heatStyle = new ProgressStyle()
					.filledColor(0xFFFF6600)
					.alternateFilledColor(0xFFCC6600)
					.backgroundColor(0x660000)
					.numberFormat(NumberFormat.COMPACT)
					.suffix("H");
			probeInfo.text("Stored Heat:");
			probeInfo.progress(storage.getHeatStored(), storage.getMaxHeatStored(), heatStyle);
		}
	}
	
	/**
	 * Controls whether adjacent faces of neighboring blocks are removed when this block is present.
	 * @return true if this block should trigger hidden surface removal on neighboring blocks, otherwise false.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}
	
	/**
	 * Controls whether torches, redstone torches, ladders, and other "supported" blocks can be placed on the side of
	 * this block.
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
}
