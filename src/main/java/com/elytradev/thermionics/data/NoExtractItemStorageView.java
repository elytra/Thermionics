/*
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
