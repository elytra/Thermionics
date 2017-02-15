package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.api.IHeatStorage;
import io.github.elytra.thermionics.block.behavior.BlockStateBehavior;
import io.github.elytra.thermionics.block.behavior.DelayedBlockStateContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockImpl extends Block implements IProbeInfoAccessor {
	private BlockTransparency blockTransparency = BlockTransparency.OPAQUE;
	private BlockStateBehavior blockStateBehavior = BlockStateBehavior.NONE;
	
	public BlockImpl(Material material, String id) {
		this(material, id, BlockStateBehavior.NONE);
	}
	
	public BlockImpl(Material material, String id, BlockStateBehavior behavior) {
		super(material);
		System.out.println("DelayedBlockStateContainer created. Initializing impl fields.");
		
		this.blockStateBehavior = behavior;
		
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setRegistryName("thermionics", id);
		this.setUnlocalizedName("thermionics."+id);
		
		
		if (this.blockState instanceof DelayedBlockStateContainer) {
			((DelayedBlockStateContainer) this.blockState).freeze(behavior);
			this.setDefaultState(behavior.getDefaultState(this.blockState.getBaseState()));
		}
		
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
			probeInfo.text("Stored Heat: "+storage.getHeatStored()+"/"+storage.getMaxHeatStored());
			probeInfo.progress(storage.getHeatStored(), storage.getMaxHeatStored(), heatStyle);
		}
		
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			probeInfo.text("Item Storage ("+handler.getSlots()+" slots)");
			IProbeInfo sub = probeInfo.vertical();
			for(int i=0; i<handler.getSlots(); i++) {
				ItemStack item = handler.getStackInSlot(i);
				if (!item.isEmpty()) sub.text(item.getDisplayName()+" * "+item.getCount());
			}
		}
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new DelayedBlockStateContainer(this);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return blockStateBehavior.getStateFromMeta(this, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return blockStateBehavior.getMetaFromState(state);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		IBlockState placement = blockStateBehavior.getPlacementState(world, pos, state, placer, stack);
		if (!state.equals(placement)) {
			world.setBlockState(pos, placement);
		}
	}
	
	/**
	 * Controls whether adjacent faces of neighboring blocks are removed when this block is present.
	 * @return true if this block should trigger hidden surface removal on neighboring blocks, otherwise false.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return blockTransparency==BlockTransparency.OPAQUE;
	}
	
	/**
	 * Controls whether torches, redstone torches, ladders, and other "supported" blocks can be placed on the side of
	 * this block.
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	/**
	 * Gets the BlockState Variant key that should be used to render this Block when seen as an Item.
	 * @param item the ItemStack being rendered
	 * @return a fully-specified blockState key in vanilla format (e.g. "axis=y,variant=0" or "inventory")
	 */
	public String getItemModelKey(ItemStack item) {
		return "inventory";
	}
	
	public static enum BlockTransparency {
		OPAQUE,
		TRANSPARENT,
		TRANSLUCENT;
	}
	
	public static class Builder {
		String id = null;
		
		
		public Builder(String id) {
			this.id = id;
		}
		
		public Builder withBehavior(BlockStateBehavior behavior) {
			return this;
		}
	}
}
