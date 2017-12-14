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

import com.elytradev.concrete.inventory.gui.widget.WBar;
import com.elytradev.concrete.inventory.gui.widget.WFluidBar;
import com.elytradev.concrete.inventory.gui.widget.WImage;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.thermionics.gui.widget.WColoredSlot;
import com.elytradev.thermionics.tileentity.TileEntityPotStill;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ContainerPotStill extends ContainerMachine {

	public ContainerPotStill(IInventory player, IInventory container, TileEntity te) {
		super(player, container);
		TileEntityPotStill still = (TileEntityPotStill)te;
		/*
		this.setTitleColor(0xFFFFFFFF);
		this.setColor(0xCC707070);
		this.setBevelStrength(0.4f);
		
		WGridPanel panel = new WGridPanel();
		super.setRootPanel(panel);
		
		if (Thermionics.isAprilFools()) {
			panel.add(new WPlasma(), 0, 0, 9, 4);
		}*/
		
		panel.add(WColoredSlot.of(container, 1, WColoredSlot.INPUT), 0, 1); //Full buckets for loading in fluids
		panel.add(WItemSlot.of(container, 2), 0, 3);                        //Empty buckets from inserting fluids
		panel.add(new WImage(new ResourceLocation("thermionics", "textures/gui/input_bucket_arrows.png")), 1, 1, 1, 3);
		panel.add(new WFluidBar(
				new ResourceLocation("thermionics", "textures/gui/bg_tank.png"),
				new ResourceLocation("thermionics", "textures/gui/input_tank.png"),
				still.getInputTank())
				.withTooltip("%3$s\n\u00A77%1$d/%2$dmB"),
				2, 1, 1, 3); //Input tank
		
		
		//TODO: FANCEH DISTILLER LOOPS
		panel.add(WItemSlot.of(container, 0), 4, 3); //Precipitate
		
		panel.add(new WFluidBar(
				new ResourceLocation("thermionics", "textures/gui/bg_tank.png"),
				new ResourceLocation("thermionics", "textures/gui/output_tank.png"),
				still.getOutputTank())
				.withTooltip("%3$s\n\u00A77%1$d/%2$dmB"),
				6, 1, 1, 3); //Output tank
		panel.add(new WImage(new ResourceLocation("thermionics", "textures/gui/output_bucket_arrows.png")), 7, 1, 1, 3);
		panel.add(WColoredSlot.of(container, 3, WColoredSlot.OUTPUT), 8, 1); //Empty buckets for unloading fluids
		panel.add(WItemSlot.of(container, 4), 8, 3);                         //Full buckets of output fluid
		
		panel.add(new WBar(
				new ResourceLocation("thermionics","textures/gui/progress.heat.bg.png"),
				new ResourceLocation("thermionics","textures/gui/progress.heat.bar.png"),
				container, 0, 1, WBar.Direction.RIGHT
				), 1, 4, 7, 1);
		
		panel.add(this.createPlayerInventoryPanel(), 0, 5);
	}

}
