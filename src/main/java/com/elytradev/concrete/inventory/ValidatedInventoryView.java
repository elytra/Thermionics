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

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ValidatedInventoryView implements IInventory {
	private final ConcreteItemStorage delegate;
	private int[] fields = new int[0];
	private HashMap<Integer, Supplier<Integer>> fieldDelegates = new HashMap<>();
	
	public ValidatedInventoryView(ConcreteItemStorage delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean hasCustomName() {
		return delegate.getName()!=null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(delegate.getName());
	}

	@Override
	public int getSizeInventory() {
		return delegate.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for(int i=0; i<delegate.getSlots(); i++) {
			if (!delegate.getStackInSlot(i).isEmpty()) return false;
		}
		
		return true;
	}
	
	public ValidatedInventoryView withField(int index, Supplier<Integer> delegate) {
		
		fieldDelegates.put(index, delegate);
		return this;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index>=delegate.getSlots()) return ItemStack.EMPTY;
		return delegate.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		//if (!delegate.getCanExtract(index)) return ItemStack.EMPTY;
		return delegate.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		//if (!delegate.getCanExtract(index)) return ItemStack.EMPTY;
		ItemStack existing = delegate.getStackInSlot(index);
		if (existing.isEmpty()) return ItemStack.EMPTY;
		return delegate.extractItem(index, existing.getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		delegate.setStackInSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		delegate.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return delegate.getValidator(index).test(stack);
	}

	@Override
	public int getField(int id) {
		Supplier<Integer> delegate = fieldDelegates.get(id);
		if (delegate!=null) return delegate.get();
		if (fields.length>id) return fields[id];
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		//System.out.println("SetField id:"+id+" val:"+value);
		if (fields.length<=id) {
			int[] newFields = new int[id+1];
			if (fields.length>0) System.arraycopy(fields, 0, newFields, 0, fields.length);
			fields = newFields;
		}
		fields[id] = value;
	}

	@Override
	public int getFieldCount() {
		//TODO: This is prone to problems; assumes that fieldDelegates are contiguous
		return Math.max(fields.length, fieldDelegates.size());
	}

	@Override
	public void clear() {
		for(int i=0; i<delegate.getSlots(); i++) {
			setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}
	
	public Predicate<ItemStack> getValidator(int index) {
		return delegate.getValidator(index);
	}
}
