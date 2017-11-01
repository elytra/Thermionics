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

package com.elytradev.thermionics.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WColoredSlot extends WItemSlot {
	public static final int OUTPUT  = 0xff_ff6600;
	public static final int INPUT   = 0xff_0084ff;
	
	protected int slotsWide;
	protected int slotsHigh;
	protected int color = 0xFF000000;
	protected boolean big;
	
	public WColoredSlot(IInventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
		super(inventory, startIndex, slotsWide, slotsHigh, big, false);
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.big = big;
	}
	
	private static int multiplyColor(int color, float amount) {
		int a = color & 0xFF000000;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;
		
		r = Math.min(r*amount, 1.0f);
		g = Math.min(g*amount, 1.0f);
		b = Math.min(b*amount, 1.0f);
		
		int ir = (int)(r*255);
		int ig = (int)(g*255);
		int ib = (int)(b*255);
		
		return    a |
				(ir << 16) |
				(ig <<  8) |
				 ib;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for (int xi = 0; xi < slotsWide; xi++) {
			for (int yi = 0; yi < slotsHigh; yi++) {
				
				int lo = multiplyColor(color, 0.7f);
				int bg = GuiDrawing.colorAtOpacity(0x000000, 0.29f);
				int hi = multiplyColor(color, 1.7f);
				
				if (big) {
					GuiDrawing.drawBeveledPanel((xi * 18) + x - 4, (yi * 18) + y - 4, 24, 24,
							lo, bg, hi);
				} else {
					GuiDrawing.drawBeveledPanel((xi * 18) + x - 1, (yi * 18) + y - 1, 18, 18,
							lo, bg, hi);
				}
			}
		}
	}
	
	public static WColoredSlot of(IInventory container, int slotNum, int color) {
		WColoredSlot slot = new WColoredSlot(container, slotNum, 1, 1, false);
		slot.color = color;
		return slot;
	}
}
