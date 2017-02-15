/**
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
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
