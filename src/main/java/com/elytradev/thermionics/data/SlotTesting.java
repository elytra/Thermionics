package com.elytradev.thermionics.data;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTesting extends Slot {

	public SlotTesting(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if (inventory instanceof ValidatedInventory) {
			return ((ValidatedInventory) inventory).getPredicate(getSlotIndex()).test(stack);
		} else {
			return super.isItemValid(stack);
		}
	}
}
