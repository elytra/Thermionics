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
package com.elytradev.thermionics.gui.widget;

import com.elytradev.concrete.client.gui.GuiDrawing;
import com.elytradev.concrete.gui.widget.WWidget;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WPBar extends WWidget {
	public static final ResourceLocation ARROW_BG = new ResourceLocation("thermionics","textures/gui/progress.p.bg.png");
	public static final ResourceLocation ARROW_FG = new ResourceLocation("thermionics","textures/gui/progress.p.fg.png");
	public static final ResourceLocation P_BG =     new ResourceLocation("thermionics","textures/gui/progress.pb.bg.png");
	public static final ResourceLocation P_FG =     new ResourceLocation("thermionics","textures/gui/progress.pb.fg.png");
	
	private IInventory container;
	private int powerField = 0;
	private int transmissionField = 0;
	private int loadField = 0;
	
	public WPBar(IInventory container, int power, int transmission, int load) {
		this.container = container;
		powerField = power;
		transmissionField = transmission;
		loadField = load;
	}
	
	@Override
	public boolean canResize() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		int maxPower = container.getField(powerField);
		int transmission = container.getField(transmissionField);
		int load = container.getField(loadField);
		
		int maxDelta = Math.abs(maxPower - load);
		int curDelta = Math.abs(transmission - load);
		float percent = 1f - (curDelta/(float)maxDelta);
		int arrows = (int)(14 * percent);
		//arrows = 8;
		
		for(int i=0; i<13; i++) {
			if (i<arrows) {
				GuiDrawing.rect(ARROW_FG, x+3+(i*8), y, 8, 8, 0xFFFFFFFF);
			} else {
				GuiDrawing.rect(ARROW_BG, x+3+(i*8), y, 8, 8, 0xFFFFFFFF);
			}
		}
		if (arrows>=13) {
			GuiDrawing.rect(P_FG, x+3+(13*8), y, 16, 8, 0xFFFFFFFF);
		} else {
			GuiDrawing.rect(P_BG, x+3+(13*8), y, 16, 8, 0xFFFFFFFF);
		}
	}
}
