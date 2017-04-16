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
package com.elytradev.concrete.gui.widget;

import java.util.ArrayList;

import com.elytradev.concrete.client.gui.GuiDrawing;
import com.elytradev.concrete.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.ValidatedSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WItemSlot extends WWidget {
	private ArrayList<Slot> peers = new ArrayList<>();
	private IInventory inventory;
	private int startIndex = 0;
	private int slotsWide = 1;
	private int slotsHigh = 1;
	private boolean big = false;
	private boolean ltr = true;
	
	public static WItemSlot of(IInventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		
		return w;
	}
	
	public static WItemSlot of(IInventory inventory, int startIndex, int slotsWide, int slotsHigh) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = startIndex;
		w.slotsWide = slotsWide;
		w.slotsHigh = slotsHigh;
		
		return w;
	}
	
	public static WItemSlot outputOf(IInventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		w.big = true;
		
		return w;
	}
	
	public static WItemSlot ofPlayerStorage(IInventory inventory) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = 9;
		w.slotsWide = 9;
		w.slotsHigh = 3;
		w.ltr = false;
		
		return w;
	}
	
	@Override
	public int getWidth() {
		return slotsWide*18;
	}
	
	@Override
	public int getHeight() {
		return slotsHigh*18;
	}
	
	@Override
	public void createPeers(ConcreteContainer c) {
		peers.clear();
		int index = startIndex;
		
		if (ltr) {
			for(int x=0; x<slotsWide; x++) {
				for(int y=0; y<slotsHigh; y++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getX()+(x*18), this.getY()+(y*18));
					peers.add(slot);
					c.addSlotPeer(slot);
					index++;
				}
			}
		} else {
			for(int y=0; y<slotsHigh; y++) {
				for(int x=0; x<slotsWide; x++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getX()+(x*18), this.getY()+(y*18));
					peers.add(slot);
					c.addSlotPeer(slot);
					index++;
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for(int xi=0; xi<slotsWide; xi++) {
			for(int yi=0; yi<slotsHigh; yi++) {
				if (big) {
					GuiDrawing.drawBeveledPanel((xi*18) + x - 4, (yi*18) + y - 4, 24, 24);
				} else {
					GuiDrawing.drawBeveledPanel((xi*18) + x - 1, (yi*18) + y - 1, 18, 18);
				}
			}
		}
	}
}
