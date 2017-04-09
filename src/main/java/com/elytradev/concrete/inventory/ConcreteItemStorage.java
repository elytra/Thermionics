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
package com.elytradev.concrete.inventory;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import scala.actors.threadpool.Arrays;

/**
 * Base/internal class for managing item storage. Can be wrapped to create various kinds of filtered or validated
 * IItemHandlers and IInventories.
 */
public class ConcreteItemStorage extends ItemStackHandler {
	private ArrayList<Runnable> listeners = new ArrayList<>();
	private ArrayList<Predicate<ItemStack>> validators = new ArrayList<>();
	private boolean[] extractMask;
	private String name = "";
	
	public ConcreteItemStorage(int slots) {
		super(slots);
		extractMask = new boolean[slots];
		Arrays.fill(extractMask, true);
	}
	
	public final ConcreteItemStorage withName(String name) {
		this.name = name;
		return this;
	}
	
	@SafeVarargs
	public final ConcreteItemStorage withValidators(Predicate<ItemStack>... predicates) {
		validators.clear();
		for(Predicate<ItemStack> predicate : predicates) validators.add(predicate);
		return this;
	}
	
	public final ConcreteItemStorage setCanExtract(int index, boolean canExtract) {
		if (index<extractMask.length) extractMask[index] = canExtract;
		return this;
	}

	public void markDirty() {
		for(Runnable r : listeners) {
			r.run();
		}
	}
	
	public void listen(@Nonnull Runnable r) {
		listeners.add(r);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = super.extractItem(slot, amount, simulate);
		if (!simulate) markDirty();
		return stack;
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
	
	@Nullable
	public String getName() {
		return name;
	}
	
	public boolean hasValidator(int slot) {
		return validators.size()>slot;
	}
	
	/**
	 * Returns a validator for the indicated slot if one is set, otherwise returns a validator which accepts anything.
	 */
	@Nonnull
	public Predicate<ItemStack> getValidator(int slot) {
		if (validators.size()<=slot) return Validators.ANYTHING;
		return validators.get(slot);
	}
	
	public boolean getCanExtract(int slot) {
		if (extractMask.length<=slot) return false;
		return extractMask[slot];
	}
}
