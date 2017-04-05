package com.elytradev.thermionics.gui;

import java.util.HashMap;

import net.minecraft.util.EnumFacing;

/**
 * Like a JPanel with a BorderLayout
 */
public class WBorderPanel extends WPanel {
	private HashMap<EnumFacing, WWidget> orientations = new HashMap<>();
	
	public void add(WWidget w, EnumFacing side) {
		EnumFacing cleanSide = side;
		if (side.equals(EnumFacing.DOWN)) cleanSide = EnumFacing.UP;
		children.add(w);
		orientations.put(cleanSide, w);
	}
}
