package com.elytradev.concrete.gui.widget;

import com.elytradev.concrete.client.gui.GuiDrawing;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WImage extends WWidget {
	ResourceLocation texture;
	
	public WImage(ResourceLocation loc) {
		this.texture = loc;
	}
	
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.rect(texture, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
	}
}
