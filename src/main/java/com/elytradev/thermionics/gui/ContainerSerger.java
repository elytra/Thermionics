package com.elytradev.thermionics.gui;

import com.elytradev.concrete.gui.ConcreteContainer;
import com.elytradev.concrete.gui.widget.WBar;
import com.elytradev.concrete.gui.widget.WGridPanel;
import com.elytradev.concrete.gui.widget.WItemSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ContainerSerger extends ConcreteContainer {
	public static final int ID = 4;

	public ContainerSerger(IInventory player, IInventory container) {
		super(player, container);
		
		WGridPanel panel = new WGridPanel();
		super.setRootPanel(panel);
		
		panel.add(WItemSlot.of(container, 0, 3, 3), 1, 1);
		panel.add(WItemSlot.outputOf(container, 9), 7, 2);
		
		panel.add(new WBar(
				new ResourceLocation("thermionics","textures/gui/progress.overlock.bg.png"),
				new ResourceLocation("thermionics","textures/gui/progress.overlock.fg.png"),
				container, 0, 1, WBar.Direction.UP
				), 5, 2);
		
		panel.add(WItemSlot.ofPlayerStorage(player), 0, 5);
		//panel.add(new WImage(new ResourceLocation("thermionics","textures/gui/scrollwork.png")), 2, 7, 5, 1);
		panel.add(WItemSlot.of(player, 0, 9, 1), 0, 8);
	}

}
