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
package com.elytradev.concrete.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elytradev.concrete.gui.widget.WPanel;
import com.elytradev.concrete.inventory.ValidatedSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * "Container" is Minecraft's way of managing shared state for a block whose GUI is currently open.
 */
public class ConcreteContainer extends Container {
	private IInventory playerInventory;
	private IInventory container;
	private WPanel rootPanel;
	private int[] syncFields = new int[0];
	
	public ConcreteContainer(@Nonnull IInventory player, @Nullable IInventory container) {
		this.playerInventory = player;
		this.container = container;
		
	}
	
	/**
	 * Checks to see if the rootPanel needs validating, and if so, handles the validation. Modders generally don't need
	 * to call this method unless they're overriding ConcreteGui's drawGuiContainerBackgroundLayer method (which is
	 * unwise!)
	 */
	public void validate() {
		if (rootPanel!=null && !rootPanel.isValid()) {
			this.inventorySlots.clear();
			this.inventoryItemStacks.clear();
			this.rootPanel.validate(this);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (container!=null) return container.isUsableByPlayer(playerIn);
		return true;
	}

	public void addSlotPeer(Slot slot) {
		this.addSlotToContainer(slot);
	}
	
	
	public void initContainerSlot(int slot, int x, int y) {
		this.addSlotToContainer(new ValidatedSlot(container, slot, x*18, y*18));
	}
	
	public void initPlayerInventory(int x, int y) {
		for (int yi = 0; yi < 3; yi++) {
            for (int xi = 0; xi < 9; xi++) {
                addSlotToContainer(new Slot(playerInventory, xi + (yi * 9) + 9, x + (xi * 18), y + (yi * 18)));
            }
        }
		
		
		for(int i=0; i<9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, x+(i*18), y + (3*18) + 4));
		}
	}
	
	@Override
	public void addListener(IContainerListener listener) {
        super.addListener(listener);
        if (container!=null) listener.sendAllWindowProperties(this, container);
    }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		if (container!=null && container.getFieldCount()>0) {
			//Any change in the number of fields reported by the server represents a total desync.
			int numFields = container.getFieldCount();
			if (syncFields.length < numFields) {
				syncFields = new int[numFields];
			}
			
			for (IContainerListener listener : this.listeners) {
	            for(int field = 0; field<numFields; field++) {
	            	int newValue = container.getField(field);
	            	if (syncFields[field]!=newValue) {
	            		listener.sendProgressBarUpdate(this, field, newValue);
	            		syncFields[field] = newValue;
	            	}
	            }
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if(container!=null) container.setField(id, data);
    }
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack srcStack = ItemStack.EMPTY;
		Slot src = this.inventorySlots.get(index);
		if (src != null && src.getHasStack()) {
			srcStack = src.getStack();
			
			if (src.inventory==playerInventory) {
				//Try to push the stack from the player-inventory to the container-inventory.
				ItemStack remaining = transferToInventory(srcStack, container);
				src.putStack(remaining);
				return ItemStack.EMPTY;
			} else {
				//Try to push the stack from the container-inventory to the player-inventory
				ItemStack remaining = transferToInventory(srcStack, playerInventory);
				src.putStack(remaining);
				return ItemStack.EMPTY;
			}
		} else {
			//Shift-clicking on an invalid or empty slot does nothing.
		}
		
		src.putStack(srcStack);
		return ItemStack.EMPTY;
    }
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
		return result;
	}
	
	/**
	 * Sets the root WPanel element
	 * @param panel
	 */
	public void setRootPanel(WPanel panel) {
		//Invalidate anything the panel added
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		
		this.rootPanel = panel;
	}
	
	public WPanel getRootPanel() {
		return this.rootPanel;
	}
	
	public ItemStack transferToInventory(ItemStack stack, IInventory inventory) {
		ItemStack result = stack.copy();
		
		//Prefer dropping on top of existing stacks
		for(Slot s : this.inventorySlots) {
			if (s.inventory==inventory && s.isItemValid(result)) {
				if (s.getHasStack()) {
					ItemStack dest = s.getStack();
					
					//If the two items can stack together and the existing stack can hold more items...
					if (canStackTogether(result, dest) && dest.getCount()<s.getItemStackLimit(dest)) {
						int sum = dest.getCount() + result.getCount();
						int toDeposit = Math.min(s.getItemStackLimit(dest), sum);
						int remaining = sum - toDeposit;
						dest.setCount(toDeposit);
						result.setCount(remaining);
						s.onSlotChanged();
					}
					if (result.isEmpty()) {
						return ItemStack.EMPTY;
					}
				}
				
			}
		}
		
		//No eligible existing stacks remain. Drop into the first available empty slots.
		for(Slot s : this.inventorySlots) {
			if (s.inventory==inventory && s.isItemValid(result)) {
				if (!s.getHasStack()) {
					s.putStack(result.splitStack(s.getSlotStackLimit()));
				}
			}
			
			if (result.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		
		return result;
	}
	
	public boolean canStackTogether(ItemStack src, ItemStack dest) {
		if (src.isEmpty() || dest.isEmpty()) return false; //Don't stack using itemstack counts if one or the other is empty.
		
		return
				dest.isStackable() &&
				dest.getItem()==src.getItem() &&
				dest.getItemDamage()==src.getItemDamage() &&
				dest.areCapsCompatible(src);
	}

	public String getLocalizedName() {
		
		return container.getDisplayName().getUnformattedComponentText();
	}
}
