package com.elytradev.thermionics.gui;

import com.elytradev.concrete.gui.ConcreteContainer;

import net.minecraft.inventory.IInventory;

public class ContainerFirebox extends ConcreteContainer {
	public static final int ID = 0;
	
	public ContainerFirebox(IInventory player, IInventory container) {
		super(player, container);
		
		initPlayerInventory(0, 18*4);
		
		initContainerSlot(0, 2,1);
		initContainerSlot(1, 6,1);
	}

}
