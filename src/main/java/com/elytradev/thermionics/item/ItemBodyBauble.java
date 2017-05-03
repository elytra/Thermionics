package com.elytradev.thermionics.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
/**
 * Extremely useful if Baubles is installed, completely useless if it isn't.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemBodyBauble extends Item implements baubles.api.IBauble {

	public ItemBodyBauble(String id) {
		this.setRegistryName("bauble."+id);
		this.setUnlocalizedName("thermionics.bauble."+id);
	}
	
	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack arg0) {
		return baubles.api.BaubleType.BODY;
	}

}
