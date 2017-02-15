package io.github.elytra.thermionics.data;

import java.util.ArrayList;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ObservableItemHandler extends ItemStackHandler {
	private ArrayList<Consumer<ObservableItemHandler>> listeners = new ArrayList<>();
	
	public ObservableItemHandler(int slots) {
		super(slots);
	}

	private void markDirty() {
		for(Consumer<ObservableItemHandler> c : listeners) {
			c.accept(this);
		}
	}
	
	public void listen(@Nonnull Consumer<ObservableItemHandler> c) {
		listeners.add(c);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = super.extractItem(slot, amount, simulate);
		if (!simulate) markDirty();
		return stack;
	}

	@Override
	public int getSlotLimit(int slot) {
		return super.getSlotLimit(slot);
	}

	@Override
	public int getSlots() {
		return super.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return super.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
		ItemStack result = super.insertItem(slot, itemStack, simulate);
		if (!simulate) markDirty();
		return result;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack itemStack) {
		super.setStackInSlot(slot, itemStack);
		markDirty();
	}
}
