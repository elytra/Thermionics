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
package com.elytradev.thermionics.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiTesting extends GuiScreen {
	public static final int PADDING = 6;
	private IInventory playerInv;
	private Container container;
	private int left = 0;
	private int top = 0;
	private int xSize = 200;
	private int ySize = 200;
	
	public GuiTesting(InventoryPlayer playerInv, Container container) {
		System.out.println("Init!");
		
		this.playerInv = playerInv;
		this.container = container;
	}
	
	/*
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		System.out.println("DrawBackgroundLayer!");
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        //int i = -150;
        //int j = -150;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        //if (TileEntityFurnace.isBurning(this.tileFurnace))
        //{
            //int k = 0;//this.getBurnLeftScaled(13);
            //this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
       // }

        int l = 0;//this.getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
    }*/
	
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "xSize" and "ySize" are the width and height of the panel to render
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		drawGuiPanel(left, top, xSize, ySize);
		fontRenderer.drawString("Firebox", left+PADDING, top+PADDING, 0xFF404040);
		
		if (container!=null) {
			for(Slot slot : container.inventorySlots) {
				drawItemSlot(slot.xPos, slot.yPos);
				itemRender.renderItemAndEffectIntoGUI(this.mc.player, slot.getStack(), slot.xPos, slot.yPos);
				//slot.xPos
			}
		}
		drawItemSlot(left+PADDING, top+PADDING+18);
		drawItemSlot(left+PADDING+18, top+PADDING+18);
		drawItemSlot(left+PADDING, top+PADDING+18+18);
		/*
        rect(left+3, top+3, xSize-6, ySize-6, 0xC6C6C6); //Main panel area
        rect(left+2, top,   xSize-4, 1,       0x000000); //Top outline
        rect(left,   top+2, 1,       ySize-4, 0x000000); //Left outline
        rect(left+xSize-1, top+2, 1, ySize-4, 0x000000); //Right outline
        rect(left+2, top+ySize-1, xSize-4, 1, 0x000000); //Bottom outline
        rect(left+1, top+1, 1, 1, 0x000000); //Topleft round pixel
        rect(left+1, top+ySize-2, 1, 1, 0x000000); //Bottomleft round pixel
        rect(left+xSize-2, top+1, 1, 1, 0x000000); //Topright round pixel
        rect(left+xSize-2, top+ySize-2, 1, 1, 0x000000); //Bottomright round pixel
        */
        
        //rect(left, top, width-6, 1, 0x000000);
		//super.drawTexturedModalRect(0, 0, null, 100, 100);
	}
	
	
	/*
	 * These methods are called frequently and empty, meaning they're probably *meant* for subclasses to override to
	 * provide core GUI functionality.
	 */
	
	@Override
	public void initGui() {
		System.out.println("initGui");
		
	}
	
	@Override
	public void updateScreen() {
		//System.out.println("updateScreen");
    }
	
	@Override
	public void onGuiClosed() {
		System.out.println("onGuiClosed");
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}
	
	/*
	 * While these methods are implemented in GuiScreen, chances are we'll be shadowing a lot of the GuiScreen methods
	 * in order to implement our own button protocol and more advanced features.
	 */
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		System.out.println("keyTyped:"+Integer.toHexString(keyCode)+" ("+typedChar+")");
		if (typedChar=='e') this.mc.player.closeScreen();
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		System.out.println("mouseClicked:"+mouseX+","+mouseY+" button:"+mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		System.out.println("mouseClicked:"+mouseX+","+mouseY+" state:"+state);
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		System.out.println("mouseClickMove:"+mouseX+","+mouseY+" button:"+clickedMouseButton+" sinceLastClick:"+timeSinceLastClick);
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		System.out.println("actionPerformed:"+button.displayString+" ("+button.id+")");
		super.actionPerformed(button);
	}
	
	/*
	 * We'll probably wind up calling some of this manually, but they do useful things for us so we may leave
	 * them unharmed.
	 */
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		System.out.println("setWorldAndResolution:"+width+"x"+height);
		
		left = (width  / 2) - (xSize / 2);
		top =  (height / 2) - (ySize / 2);
		super.setWorldAndResolution(mc, width, height);
	}
	
	@Override
	public void setGuiSize(int w, int h) {
		System.out.println("setGuiSize:"+w+"x"+h);
		left = (width  / 2) - (xSize / 2);
		top =  (height / 2) - (ySize / 2);
		super.setGuiSize(w, h);
	}
	
	/* (default impl calls setWorldAndResolution so we're good here.)
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
	}*/
	
	/*
	 * The following methods seem to be internal delegation tools; they poll the LWJGL Mouse and Keyboard objects, fire
	 * Forge events, and generally invisibly handle things for you that you Want Invisibly Handled. Chances are we won't
	 * be messing with these *at all* since they're some of the very few things in guis that work the way they should.
	 */
	/*
	@Override
	public void handleInput() throws IOException {
		System.out.println("handleInput");
		super.handleInput();
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		System.out.println("handleMouseInput");
		super.handleMouseInput();
	}
	
	@Override
	public void handleKeyboardInput() throws IOException {
		System.out.println("handleKeyboardInput");
		super.handleKeyboardInput();
	}*/
	
	/*
	 * SPECIAL FUNCTIONS: Where possible, we want to draw everything based on *actual GUI state and composition* rather
	 * than relying on pre-baked textures that the programmer then needs to carefully match up their GUI to.
	 */
	
	public static void rect(int left, int top, int width, int height, int color) {
        if (width<=0) width=1;
        if (height<=0) height=1;
        
        //float a = (float)(color >> 24 & 255) / 255.0F;
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
	
	public static void drawGuiPanel(int x, int y, int width, int height) {
		rect(x+3,       y+3,        width-6, height-6, 0xC6C6C6); //Main panel area
		rect(x+2,       y+1,        width-4, 2,        0xFFFFFF); //Top hilight
		rect(x+2,       y+height-3, width-4, 2,        0x555555); //Bottom shadow
		rect(x+1,       y+2,        2,       height-4, 0xFFFFFF); //Left hilight
		rect(x+width-3, y+2,        2,       height-4, 0x555555); //Right shadow
		rect(x+width-3, y+2,        1,       1,        0xC6C6C6); //Topright non-hilight/non-shadow transition pixel
		rect(x+2,       y+height-3, 1,       1,        0xC6C6C6); //Bottomleft non-hilight/non-shadow transition pixel
		rect(x+3,       y+3,        1,       1,        0xFFFFFF); //Topleft round hilight pixel
		rect(x+width-4, y+height-4, 1,       1,        0x555555); //Bottomright round hilight pixel
        rect(x+2,       y,          width-4, 1,       0x000000); //Top outline
        rect(x,         y+2,        1,       height-4, 0x000000); //Left outline
        rect(x+width-1, y+2,        1,       height-4, 0x000000); //Right outline
        rect(x+2,       y+height-1, width-4, 1, 0x000000); //Bottom outline
        rect(x+1,       y+1,        1,       1, 0x000000); //Topleft round pixel
        rect(x+1,       y+height-2, 1,       1, 0x000000); //Bottomleft round pixel
        rect(x+width-2, y+1,        1,       1, 0x000000); //Topright round pixel
        rect(x+width-2, y+height-2, 1,       1, 0x000000); //Bottomright round pixel
	}
	
	public static void drawItemSlot(int x, int y) {
		rect(x,    y,    17, 1,  0x373737); //Top shadow
		rect(x,    y+1,  1,  16, 0x373737); //Left shadow
		rect(x+17, y+1,  1,  17, 0xFFFFFF); //Right hilight
		rect(x+1,  y+17, 17, 1,  0xFFFFFF); //Bottom hilight
	}
	
	public static void drawSlot(Slot slotIn, Slot clickedSlot, ItemStack draggedStack) {
        int i = slotIn.xPos;
        int j = slotIn.yPos;
        ItemStack itemstack = slotIn.getStack();
        boolean flag = false;
        //boolean flag1 = slotIn == clickedSlot && !draggedStack.isEmpty() && !this.isRightMouseClick;
        //ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
        String s = null;

        //if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty())
        //{
        //    itemstack = itemstack.copy();
        //    itemstack.setCount(itemstack.getCount() / 2);
        //}
        //else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty())
        //{
        //    if (this.dragSplittingSlots.size() == 1)
        //    {
        //        return;
        //    }

        //    if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn))
        //    {
        //        itemstack = itemstack1.copy();
        //        flag = true;
        //        Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
        //        int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));

        //        if (itemstack.getCount() > k)
        //        {
        //            s = TextFormatting.YELLOW.toString() + k;
        //            itemstack.setCount(k);
        //        }
        //    }
        //    else
        //    {
        //        this.dragSplittingSlots.remove(slotIn);
        //        this.updateDragSplitting();
        //    }
        //}

        //this.zLevel = 100.0F;
        //this.itemRender.zLevel = 100.0F;

        //if (itemstack.isEmpty() && slotIn.canBeHovered()) {
            //TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            //if (textureatlassprite != null) {
            //    GlStateManager.disableLighting();
                //this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                //this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
            //    GlStateManager.enableLighting();
            //    flag1 = true;
            //}
        //}

        //if (!flag1) {
        //    if (flag)
            //{
            //    drawRect(i, j, i + 16, j + 16, -2130706433);
            //}

            //GlStateManager.enableDepth();
            //itemRender.renderItemAndEffectIntoGUI(this.mc.player, itemstack, i, j);
            //itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, i, j, s);
        //}

        //this.itemRender.zLevel = 0.0F;
        //this.zLevel = 0.0F;
    }
}
