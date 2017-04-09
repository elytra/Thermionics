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

import com.elytradev.concrete.inventory.ValidatedSlot;
import com.elytradev.thermionics.gui.WPanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * "Container" is Minecraft's way of managing shared state for a block whose GUI is currently open.
 */
public class ConcreteContainer extends Container {
	
	private IInventory playerInventory;
	private IInventory container;
	private WPanel rootPanel;
	
	public ConcreteContainer(@Nonnull IInventory player, @Nullable IInventory container) {
		this.playerInventory = player;
		this.container = container;
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (container!=null) return container.isUsableByPlayer(playerIn);
		return true;
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
		/*
		if (container!=null && container.getFieldCount()>0) {
			for(IContainerListener listener : listeners) {
				listener.sendAllWindowProperties(this, container);
			}
		}*/
	}
	
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		//System.out.println("transferStackInSlot:"+index);
		
		ItemStack srcStack = ItemStack.EMPTY;
		Slot src = this.inventorySlots.get(index);
		if (src != null && src.getHasStack()) {
			srcStack = src.getStack();
			
			if (src.inventory==playerInventory) {
				//System.out.println("Transferring "+srcStack+" from playerInventory to container");
				//Try to push the stack from the player-inventory to the container-inventory. If that's not possible,
				//try to move it between the non-hotbar-inventory and the hotbar-inventory
				
				ItemStack remaining = transferToInventory(srcStack, container);
				//System.out.println("Remaining: "+remaining);
				//this.detectAndSendChanges();
				src.putStack(remaining);
				return ItemStack.EMPTY;
				//return remaining;
				
				
			} else {
				//Try to push the stack from the container-inventory to the player-inventory. Prefer non-hotbar if possible
				
				//this.mergeItemStack(srcStack, 9, 27+9, false);
				//Non-hotbar inventory
				//System.out.println("Transferring "+srcStack+" from container to playerInventory");
				ItemStack remaining = transferToInventory(srcStack, playerInventory);
				//System.out.println("Remaining: "+remaining);
				//this.detectAndSendChanges();
				src.putStack(remaining);
				return ItemStack.EMPTY;
				//return remaining;
				//
				
				/*
				for(int i=9; i<27+9; i++) {
					if (playerInventory.isItemValidForSlot(index, srcStack)) {
						//playerInventory.
					}
				}*/
			}
		} else {
			//Shift-clicking on an invalid or empty slot does nothing.
		}
		
		src.putStack(srcStack);
		return ItemStack.EMPTY;
		//return srcStack;
		
		
		
		
		//return super.transferStackInSlot(playerIn, index);
		
		/*
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;*/
    }
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
		return result;
	}
	
	/**
	 * Sets the programmer-friendly UI panel description 
	 * @param panel
	 */
	public void setRootPanel(WPanel panel) {
		//Invalidate anything the panel added
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		
		this.rootPanel = panel;
		this.rootPanel.createPeers(this);
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
