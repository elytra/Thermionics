package com.elytradev.concrete.gui.widget;

import com.elytradev.concrete.gui.GuiDrawing;

import net.minecraft.util.ResourceLocation;

public class WImage extends WWidget {
	ResourceLocation texture;
	
	public WImage(ResourceLocation loc) {
		this.texture = loc;
	}
	
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.rect(texture, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
	}
}
