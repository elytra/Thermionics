package com.elytradev.thermionics.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class ItemBlockBattery extends ItemBlockEquivalentState {

	public ItemBlockBattery(Block block) {
		super(block);
	}

	int getBatteryRF(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("energy")) {
			return stack.getTagCompound().getInteger("energy");
		} else {
			return 0;
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		String storedEnergy = I18n.translateToLocal("thermionics.data.energystorage");
		
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("energy")) {
			tooltip.add(storedEnergy+": "+stack.getTagCompound().getInteger("energy"));
		} else {
			tooltip.add(storedEnergy+": 0");
		}
	}
}
