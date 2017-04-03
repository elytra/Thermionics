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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elytradev.thermionics.client.gui.GuiTesting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerTesting extends Container {
	
	
	private IInventory player;
	private IInventory container;
	
	public ContainerTesting(@Nonnull IInventory player, @Nullable IInventory container) {
		this.player = player;
		this.container = container;
		
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	
	public void initContainerSlot(int slot, int x, int y) {
		this.addSlotToContainer(new Slot(container, slot, x*18 + 6, y*18 + 6));
	}
	
	public void initPlayerInventory(int x, int y) {
		for (int yi = 0; yi < 3; yi++) {
            for (int xi = 0; xi < 9; xi++) {
                addSlotToContainer(new Slot(player, xi + (yi * 9) + 9, x + (xi * 18), y + (yi * 18)));
            }
        }
		
		
		for(int i=0; i<9; i++) {
			
		}
	}
}
