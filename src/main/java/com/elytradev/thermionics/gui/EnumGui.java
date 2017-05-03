package com.elytradev.thermionics.gui;

import java.util.function.BiFunction;

import com.elytradev.concrete.gui.ConcreteContainer;

import net.minecraft.inventory.IInventory;

public enum EnumGui {
	FIREBOX         (ContainerFirebox::new),
	OVEN            (ContainerOven::new),
	CONVECTION_MOTOR(ContainerMotor::new),
	HAMMER_MILL     (ContainerHammerMill::new),
	SERGER          (ContainerSerger::new);
	
	private final BiFunction<IInventory, IInventory, ConcreteContainer> supplier;
	
	EnumGui(BiFunction<IInventory, IInventory, ConcreteContainer> supplier) {
		this.supplier = supplier;
	}
	
	public ConcreteContainer createContainer(IInventory player, IInventory tile) {
		return supplier.apply(player, tile);
	}
	
	public int id() {
		return ordinal(); //Oh. Right. This exists.
		/*for(int i = 0; i<values().length; i++) {
			if (values()[i]==this) return i;
		}
		return 0; //Should be impossible*/
	}
	
	public static EnumGui forId(int i) {
		if (i<0 || i>=values().length) return FIREBOX;
		return values()[i];
	}
}
