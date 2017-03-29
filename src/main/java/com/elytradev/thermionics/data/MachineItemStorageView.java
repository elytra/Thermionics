package com.elytradev.thermionics.data;

import org.apache.commons.lang3.Validate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MachineItemStorageView implements IItemHandler {
	public static final int SLOT_MACHINE_INPUT  = 0;
	public static final int SLOT_MACHINE_OUTPUT = 1;
	private final IItemHandler delegate;
	
	public MachineItemStorageView(IItemHandler delegate) {
		Validate.isTrue(delegate.getSlots()>=2, "Cannot create a machine storage view of an inventory with less than 2 slots.");
		this.delegate = delegate;
	}
	
	@Override
	public int getSlots() {
		return delegate.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return delegate.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (slot==SLOT_MACHINE_INPUT) return delegate.insertItem(slot, stack, simulate);
		else return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot==SLOT_MACHINE_OUTPUT) return delegate.extractItem(slot, amount, simulate);
		else return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return delegate.getSlotLimit(slot);
	}
}
