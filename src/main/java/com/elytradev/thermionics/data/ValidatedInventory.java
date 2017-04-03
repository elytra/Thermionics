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
package com.elytradev.thermionics.data;

import java.util.ArrayList;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.text.ITextComponent;

public class ValidatedInventory implements IInventory {
	private ArrayList<Predicate<ItemStack>> validators = new ArrayList<>();
	private final IInventory delegate;

	public ValidatedInventory(IInventory delegate) {
		this.delegate = delegate;
	}
	
	@SafeVarargs
	public ValidatedInventory(IInventory delegate, Predicate<ItemStack>... validators) {
		this.delegate = delegate;
		for(Predicate<ItemStack> validator : validators) this.validators.add(validator);
	}
	
	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean hasCustomName() {
		return delegate.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return delegate.getDisplayName();
	}

	@Override
	public int getSizeInventory() {
		return delegate.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return delegate.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return delegate.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return delegate.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		delegate.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return delegate.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		delegate.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return delegate.isUsableByPlayer(player);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		delegate.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		delegate.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (validators.size()>index) {
			return validators.get(index).test(stack);
		} else {
			return true;
		}
	}

	@Override
	public int getField(int id) {
		return delegate.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		delegate.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return delegate.getFieldCount();
	}

	@Override
	public void clear() {
		delegate.clear();
	}
	
	public static Predicate<ItemStack> ANYTHING = (it)->true;
	public static Predicate<ItemStack> NOTHING = (it)->false;
	public static Predicate<ItemStack> FURNACE_FUELS = TileEntityFurnace::isItemFuel; //This is actually the most correct/accurate way to read the furnace registry!
	
}
