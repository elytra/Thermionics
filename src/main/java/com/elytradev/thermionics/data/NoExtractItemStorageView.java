package com.elytradev.thermionics.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class NoExtractItemStorageView implements IItemHandler {
	private final IItemHandler delegate;
	
	public NoExtractItemStorageView(IItemHandler delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int getSlots() {
		return delegate.getSlots();
	}

	@Override
	@Nullable
	public ItemStack getStackInSlot(int slot) {
		return delegate.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return delegate.insertItem(slot, stack, simulate);
	}

	/** I don't care what the forge javadocs say: Returning nulls from here crashes the game. */
	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		//No extract available!
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return delegate.getSlotLimit(slot);
	}

}
