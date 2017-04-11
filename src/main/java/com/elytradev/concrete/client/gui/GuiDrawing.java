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
package com.elytradev.concrete.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiDrawing {
	
	public static void rect(ResourceLocation texture, int left, int top, int width, int height, int color) {
		rect(texture, left, top, width, height, 0, 0, 1, 1, color);
	}
	
	public static void rect(ResourceLocation texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		//float scale = 0.00390625F;
		
		if (width<=0) width=1;
	    if (height<=0) height=1;
	    
	    float r = (float)(color >> 16 & 255) / 255.0F;
	    float g = (float)(color >> 8 & 255) / 255.0F;
	    float b = (float)(color & 255) / 255.0F;
	    Tessellator tessellator = Tessellator.getInstance();
	    VertexBuffer vertexbuffer = tessellator.getBuffer();
	    GlStateManager.enableBlend();
	    //GlStateManager.disableTexture2D();
	    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	    GlStateManager.color(r, g, b, 1.0f);
	    vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
	    vertexbuffer.pos(left,       top+height, 0.0D).tex(u1, v2).endVertex();
	    vertexbuffer.pos(left+width, top+height, 0.0D).tex(u2, v2).endVertex();
	    vertexbuffer.pos(left+width, top,        0.0D).tex(u2, v1).endVertex();
	    vertexbuffer.pos(left,       top,        0.0D).tex(u1, v1).endVertex();
	    tessellator.draw();
	    //GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	}
	
	/**
	 * Draws an untextured rectangle of the specified RGB color. Alpha is always 1.0f.
	 */
	public static void rect(int left, int top, int width, int height, int color) {
	    if (width<=0) width=1;
	    if (height<=0) height=1;
	    
	    float r = (float)(color >> 16 & 255) / 255.0F;
	    float g = (float)(color >> 8 & 255) / 255.0F;
	    float b = (float)(color & 255) / 255.0F;
	    Tessellator tessellator = Tessellator.getInstance();
	    VertexBuffer vertexbuffer = tessellator.getBuffer();
	    GlStateManager.enableBlend();
	    GlStateManager.disableTexture2D();
	    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	    GlStateManager.color(r, g, b, 1.0f);
	    vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION); //I thought GL_QUADS was deprecated but okay, sure.
	    vertexbuffer.pos(left,       top+height, 0.0D).endVertex();
	    vertexbuffer.pos(left+width, top+height, 0.0D).endVertex();
	    vertexbuffer.pos(left+width, top,        0.0D).endVertex();
	    vertexbuffer.pos(left,       top,        0.0D).endVertex();
	    tessellator.draw();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	}

	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 */
	public static void drawGuiPanel(int x, int y, int width, int height) {
		drawGuiPanel(x, y, width, height, 0x555555, 0xC6C6C6, 0xFFFFFF, 0x000000);
	}
	
	
	public static void drawGuiPanel(int x, int y, int width, int height, int shadow, int panel, int hilight, int outline) {
		rect(x+3,       y+3,        width-6, height-6, panel); //Main panel area
		
		rect(x+2,       y+1,        width-4, 2,        hilight); //Top hilight
		rect(x+2,       y+height-3, width-4, 2,        shadow); //Bottom shadow
		rect(x+1,       y+2,        2,       height-4, hilight); //Left hilight
		rect(x+width-3, y+2,        2,       height-4, shadow); //Right shadow
		rect(x+width-3, y+2,        1,       1,        panel); //Topright non-hilight/non-shadow transition pixel
		rect(x+2,       y+height-3, 1,       1,        panel); //Bottomleft non-hilight/non-shadow transition pixel
		rect(x+3,       y+3,        1,       1,        hilight); //Topleft round hilight pixel
		rect(x+width-4, y+height-4, 1,       1,        shadow); //Bottomright round shadow pixel
		
	    rect(x+2,       y,          width-4, 1,        outline); //Top outline
	    rect(x,         y+2,        1,       height-4, outline); //Left outline
	    rect(x+width-1, y+2,        1,       height-4, outline); //Right outline
	    rect(x+2,       y+height-1, width-4, 1, outline); //Bottom outline
	    rect(x+1,       y+1,        1,       1, outline); //Topleft round pixel
	    rect(x+1,       y+height-2, 1,       1, outline); //Bottomleft round pixel
	    rect(x+width-2, y+1,        1,       1, outline); //Topright round pixel
	    rect(x+width-2, y+height-2, 1,       1, outline); //Bottomright round pixel
	}

	/**
	 * Draws a default-sized recessed itemslot panel
	 */
	public static void drawBeveledPanel(int x, int y) {
		drawBeveledPanel(x,y,18,18,0x373737,0x8b8b8b,0xFFFFFF);
	}
	
	/**
	 * Draws a default-color recessed itemslot panel of variable size
	 */
	public static void drawBeveledPanel(int x, int y, int width, int height) {
		drawBeveledPanel(x,y,width,height,0x373737,0x8b8b8b,0xFFFFFF);
	}
	
	/**
	 * Draws a generalized-case beveled panel. Can be inset or outset depending on arguments.
	 * @param x				x coordinate of the topleft corner
	 * @param y				y coordinate of the topleft corner
	 * @param width			width of the panel
	 * @param height		height of the panel
	 * @param topleft		color of the top/left bevel
	 * @param panel			color of the panel area
	 * @param bottomright	color of the bottom/right bevel
	 */
	public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		rect(x,         y,         width,   height,   0x8b8b8b); //Center panel
		rect(x,         y,         width-1, 1,        0x373737); //Top shadow
		rect(x,         y+1,       1,       height-2, 0x373737); //Left shadow
		rect(x+width-1, y+1,       1,       height-1, 0xFFFFFF); //Right hilight
		rect(x+1,       y+height-1,width-1, 1,        0xFFFFFF); //Bottom hilight
	}
	
	public static void drawString(String s, int x, int y, int color) {
		Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, color);
	}
}
