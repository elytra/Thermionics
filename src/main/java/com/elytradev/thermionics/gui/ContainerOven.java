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
package com.elytradev.thermionics.gui;

import com.elytradev.concrete.gui.ConcreteContainer;
import com.elytradev.concrete.gui.widget.WBar;
import com.elytradev.concrete.gui.widget.WGridPanel;
import com.elytradev.concrete.gui.widget.WItemSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ContainerOven extends ConcreteContainer {
	public static final int ID = 1;
	
	public ContainerOven(IInventory player, IInventory container) {
		super(player, container);
		
		WGridPanel panel = new WGridPanel();
		super.setRootPanel(panel);
		
		panel.add(WItemSlot.of(container, 0), 2, 1);
		panel.add(new WBar(
				new ResourceLocation("thermionics","textures/gui/progress.arrow.bg.png"),
				new ResourceLocation("thermionics","textures/gui/progress.arrow.bar.png"),
				container, 0, 1, WBar.Direction.RIGHT
				), 4, 1);
		panel.add(WItemSlot.outputOf(container, 1), 6, 1);
		
		panel.add(new WBar(
				new ResourceLocation("thermionics","textures/gui/progress.heat.bg.png"),
				new ResourceLocation("thermionics","textures/gui/progress.heat.bar.png"),
				container, 2, 3, WBar.Direction.RIGHT
				), 1, 2, 7, 1);
		
		panel.add(WItemSlot.of(player, 9, 9, 4), 0, 3);
		panel.add(WItemSlot.of(player, 0, 9, 1), 0, 8);
		
		//initPlayerInventory(0, 18*4);
		
		//initContainerSlot(0, 2,1);
		//initContainerSlot(1, 6,1);
	}

}
