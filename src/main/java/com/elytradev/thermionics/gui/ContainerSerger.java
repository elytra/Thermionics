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

package com.elytradev.thermionics.gui;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WBar;
import com.elytradev.concrete.inventory.gui.widget.WGridPanel;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.gui.widget.WPlasma;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ContainerSerger extends ConcreteContainer {
	public static final int ID = 4;

	public ContainerSerger(IInventory player, IInventory container, TileEntity te) {
		super(player, container);
		
		this.setTitleColor(0xFFFFFFFF);
		this.setColor(0xCC707070);
		
		WGridPanel panel = new WGridPanel();
		super.setRootPanel(panel);
		
		if (Thermionics.isAprilFools()) {
			panel.add(new WPlasma(), 0, 0, 9, 5);
		}
		
		panel.add(WItemSlot.of(container, 0, 3, 3), 1, 1);
		panel.add(WItemSlot.outputOf(container, 9), 7, 2);
		
		panel.add(new WBar(
				new ResourceLocation("thermionics","textures/gui/progress.overlock.bg.png"),
				new ResourceLocation("thermionics","textures/gui/progress.overlock.fg.png"),
				container, 0, 1, WBar.Direction.UP
				), 5, 2);
		
		panel.add(this.createPlayerInventoryPanel(), 0, 5);
	}

}
