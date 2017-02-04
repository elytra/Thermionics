/**
 * MIT License
 *
 * Copyright (c) 2017 The Elytra Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.api.IHeatStorage;
import io.github.elytra.thermionics.tileentity.TileEntityHeatStorage;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid="theoneprobe", iface="mcjty.theoneprobe.api.IProbeInfoAccessor")
public class BlockHeatPipe extends Block implements ITileEntityProvider, IProbeInfoAccessor {

	public BlockHeatPipe() {
		super(Material.IRON);
		
		this.setHarvestLevel("pickaxe", 1);
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
	
	@Optional.Method(modid="theoneprobe")
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData hitData) {
		TileEntity te = world.getTileEntity(hitData.getPos());
		
		if (te!=null && te instanceof TileEntityHeatStorage) {
			
			IHeatStorage storage = ((TileEntityHeatStorage)te).getCapability(Thermionics.CAPABILITY_HEATSTORAGE, null);
			//probeInfo.text(""+storage);
			ProgressStyle heatStyle = new ProgressStyle()
					.filledColor(0xFFFF6600)
					.alternateFilledColor(0xFFCC6600)
					.backgroundColor(0x660000)
					.numberFormat(NumberFormat.COMPACT)
					.suffix("H");
			probeInfo.text("Stored Heat:");
			probeInfo.progress(storage.getHeatStored(), storage.getMaxHeatStored(), heatStyle);
		} else {
			probeInfo.text("HEATPIPE ERROR");
		}
	}
	
}
