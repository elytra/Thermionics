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

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public enum EnumGui {
	FIREBOX         (ContainerFirebox::new),
	OVEN            (ContainerOven::new),
	CONVECTION_MOTOR(ContainerMotor::new),
	HAMMER_MILL     (ContainerHammerMill::new),
	SERGER          (ContainerSerger::new),
	POT_STILL       (ContainerPotStill::new);
	
	private final GuiSupplier supplier;
	
	EnumGui(GuiSupplier supplier) {
		this.supplier = supplier;
	}
	
	public ConcreteContainer createContainer(IInventory player, IInventory tile, TileEntity te) {
		return supplier.apply(player, tile, te);
	}
	
	public int id() {
		return ordinal();
	}
	
	public static EnumGui forId(int i) {
		if (i<0 || i>=values().length) return FIREBOX;
		return values()[i];
	}
	
	public static interface GuiSupplier {
		public ConcreteContainer apply(IInventory player, IInventory tile, TileEntity te);
	}
}
