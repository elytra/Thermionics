package com.elytradev.thermionics.gui;

import com.elytradev.concrete.gui.ConcreteContainer;

import net.minecraft.inventory.IInventory;

public class ContainerOven extends ConcreteContainer {
	public static final int ID = 1;
	
	public ContainerOven(IInventory player, IInventory container) {
		super(player, container);
		
		initPlayerInventory(0, 18*4);
		
		initContainerSlot(0, 2,1);
		initContainerSlot(1, 6,1);
	}

}
