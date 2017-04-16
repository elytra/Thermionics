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

import com.elytradev.concrete.client.gui.GuiDrawing;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WBar extends WWidget {
	private ResourceLocation bg;
	private ResourceLocation bar;
	private int field;
	private int max = 100;
	private IInventory inventory;
	private Direction direction;
	
	public WBar(ResourceLocation bg, ResourceLocation bar, IInventory inventory, int field, int maxfield) {
		this(bg, bar, inventory, field, maxfield, Direction.UP);
	}
	
	
	public WBar(ResourceLocation bg, ResourceLocation bar, IInventory inventory, int field, int maxfield, Direction dir) {
		this.bg = bg;
		this.bar = bar;
		this.inventory = inventory;
		this.field = field;
		this.max = maxfield;
		this.direction = dir;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.rect(bg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		int barValue = inventory.getField(field);
		
		float percent = inventory.getField(field)/(float)inventory.getField(max);
		if (percent<0) percent=0f;
		if (percent>1) percent=1f;
		
		int barMax = getWidth();
		if (direction==Direction.DOWN || direction==Direction.UP) barMax = getHeight();
		percent = ((int)(percent*barMax)) / (float)barMax; //Quantize to bar size
		
		int barSize = (int)(barMax*percent);
		if (barSize<=0) return;
		
		switch(direction) { //anonymous blocks in this switch statement are to sandbox variables
		case UP: {
			int left = x;
			int top = y + getHeight();
			top -= barSize;
			GuiDrawing.rect(bar, left, top, getWidth(), barSize, 0, 1-percent, 1, 1, 0xFFFFFFFF);
		}
			break;
		case RIGHT: {
			GuiDrawing.rect(bar, x, y, barSize, getHeight(), 0, 0, percent, 1, 0xFFFFFFFF);
		}
			break;
		case DOWN: {
			GuiDrawing.rect(bar, x, y, getWidth(), barSize, 0, 0, 1, percent, 0xFFFFFFFF);
		}
			break;
		case LEFT: {
			int left = x + getWidth();
			int top = y;
			left -= barSize;
			GuiDrawing.rect(bar, left, top, barSize, getHeight(), 1-percent, 0, 1, 1, 0xFFFFFFFF);
		}
			break;
		}
		
		
		//GuiDrawing.rect(bar, x, y+(getHeight()-barHeight), getWidth(), barHeight, 0xFFFFFFFF);
		
		//GuiDrawing.drawString(""+inventory.getField(field)+"/", x+18, y+9, 0xFF000000);
		//GuiDrawing.drawString(""+inventory.getField(max)+"", x+32, y+9, 0xFF000000);
	}
	
	public static enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
}
