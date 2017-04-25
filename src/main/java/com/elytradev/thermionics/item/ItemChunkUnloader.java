package com.elytradev.thermionics.item;

import java.util.List;
import java.util.Optional;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemChunkUnloader extends Item implements IMetaItemModel {
	private static final Optional<Boolean> UNATTUNED = Optional.empty();
	private static final Optional<Boolean> LOADED = Optional.of(Boolean.TRUE);
	private static final Optional<Boolean> UNLOADED = Optional.of(Boolean.FALSE);
	
	private static final String[] MODELS = {
		"chunkunloader_unattuned",
		"chunkunloader_unloaded",
		"chunkunloader_loaded"
	};
	
	
	public ItemChunkUnloader() {
		this.setRegistryName("chunkunloader");
		this.setUnlocalizedName("thermionics.chunkunloader");
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
		
		ItemStack stack = player.getHeldItem(hand);
		
		boolean success = unloadChunk(stack);
		if (success) {
			//TODO: Sound and/or particles to indicate chunk unloading.
			//System.out.println("Chunk Unloaded.");
		}
		
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return EnumActionResult.PASS;
		
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		
		tag.setInteger("world", world.getWorldType().getWorldTypeID());
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		tag.setInteger("x", chunk.xPosition);
		tag.setInteger("z", chunk.zPosition);
		tag.setBoolean("ignoreClick", true);
		player.setHeldItem(hand, stack);
		
		//TODO: Sound effect!
		
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tip, boolean advanced) {
		tip.add(I18n.translateToLocal("item.thermionics.chunkunloader.tip"));
		int loadedState = stack.getItemDamage();
		switch(loadedState) {
		default: //Default to unattuned
			
		case 0: tip.add(I18n.translateToLocalFormatted("thermionics.data.chunkunloader.unattuned")); break;
		case 1: tip.add(I18n.translateToLocalFormatted("thermionics.data.chunkunloader.unloaded")); break;
		case 2: tip.add(I18n.translateToLocalFormatted("thermionics.data.chunkunloader.loaded")); break;
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemslot, boolean isSelected) {
		if (world.isRemote) return;
		
		Optional<Boolean> loaded = isChunkLoaded(stack);
		if (loaded==UNATTUNED) {
			if (stack.getMetadata()!=0) {
				stack.setItemDamage(0);
				entity.replaceItemInInventory(itemslot, stack);
			}
		} else if (loaded==UNLOADED) {
			if (stack.getMetadata()!=1) {
				stack.setItemDamage(1);
				entity.replaceItemInInventory(itemslot, stack);
			}
		} else if (loaded==LOADED) {
			if (stack.getMetadata()!=2) {
				stack.setItemDamage(2);
				entity.replaceItemInInventory(itemslot, stack);
			}
		} 
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(item));
	}
	
	public Optional<Boolean> isChunkLoaded(ItemStack stack) {
		if (!stack.hasTagCompound()) return UNATTUNED;
		
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey("world")) {
			int worldId = tag.getInteger("world");
			if (!DimensionManager.isDimensionRegistered(worldId)) return UNATTUNED;
			
			//We're reasonably certain at this point the unloader is attuned to a valid world.
			
			WorldServer world = DimensionManager.getWorld(worldId);
			if (world==null) return UNLOADED;
			
			//boolean playerLoad = world.getPlayerChunkMap().contains(tag.getInteger("x"), tag.getInteger("z"));
			//System.out.println("chunk["+tag.getInteger("x")+","+tag.getInteger("z")+"] isPlayerLoaded:"+playerLoad);
			
			Chunk chunk = world.getChunkProvider().getLoadedChunk(tag.getInteger("x"), tag.getInteger("z"));
			return (chunk!=null) ? LOADED : UNLOADED;
			//Chunk chunk = world.getChunkFromChunkCoords(tag.getInteger("x"),tag.getInteger("z"));
			//return (chunk.isLoaded()) ? LOADED : UNLOADED;
		}
		
		return UNATTUNED;
	}
	
	public boolean unloadChunk(ItemStack stack) {
		if (!stack.hasTagCompound()) return false;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.getBoolean("ignoreClick")) {
			tag.removeTag("ignoreClick");
			stack.setTagCompound(tag);
			return false;
		}
		
		int worldId = tag.getInteger("world");
		int x = tag.getInteger("x");
		int z = tag.getInteger("z");
		World world = DimensionManager.getWorld(worldId);
		if (world==null) return false;
		
		IChunkProvider provider = world.getChunkProvider();
		Chunk chunk = provider.getLoadedChunk(x, z);
		if (chunk==null) return false;
		if (provider instanceof ChunkProviderServer) {
			((ChunkProviderServer) provider).unload(chunk);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String[] getModelLocations() {
		return MODELS;
	}
}
