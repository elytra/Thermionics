package com.elytradev.thermionics.data;

import java.util.function.BiFunction;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ValidatedItemStorageView implements IItemHandler {
	private IItemHandler delegate;
	private BiFunction<Integer,ItemStack,Boolean> validator;
	
	public ValidatedItemStorageView(IItemHandler delegate, BiFunction<Integer,ItemStack,Boolean> validator) {
		this.delegate = delegate;
		this.validator = validator;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!validator.apply(slot, stack)) return stack;
		else return delegate.insertItem(slot, stack, simulate);
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
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return delegate.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return delegate.getSlotLimit(slot);
	}
}
