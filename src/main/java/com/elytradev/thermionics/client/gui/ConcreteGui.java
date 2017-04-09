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

import com.elytradev.concrete.gui.ConcreteContainer;
import com.elytradev.concrete.gui.GuiDrawing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ConcreteGui extends GuiContainer {
	public static final int PADDING = 8;
	private ConcreteContainer container;
	
	//private IInventory playerInv;
	
	public ConcreteGui(InventoryPlayer playerInv, ConcreteContainer container) {
		super(container);
		this.container = container;
		this.xSize = 18*9;
		this.ySize = 18*8 + 6;
		//this.playerInv = playerInv;
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "xSize" and "ySize" are the width and height of the panel to render
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	//@Override
	//public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	//}
	
	
	/*
	 * These methods are called frequently and empty, meaning they're probably *meant* for subclasses to override to
	 * provide core GUI functionality.
	 */
	
	@Override
	public void initGui() {
		//System.out.println("initGui");
		super.initGui();
	}
	
	//Will probably re-activate for animation!
	//@Override
	//public void updateScreen() {
	//	System.out.println("updateScreen");
    //}
	
	@Override
	public void onGuiClosed() {
		//System.out.println("onGuiClosed");
		super.onGuiClosed();
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
		//System.out.println("keyTyped:"+Integer.toHexString(keyCode)+" ("+typedChar+")");
		if (typedChar=='e') {
			this.mc.player.closeScreen();
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}
	
	/*
	 * We'll probably wind up calling some of this manually, but they do useful things for us so we may leave
	 * them unharmed.
	 */
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		//System.out.println("setWorldAndResolution:"+width+"x"+height);
		
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
		
	}
	
	@Override
	public void setGuiSize(int w, int h) {
		super.setGuiSize(w, h);
		//System.out.println("setGuiSize:"+w+"x"+h);
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
		
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
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		//drawDefaultBackground();
		
		GuiDrawing.drawGuiPanel(guiLeft-PADDING, guiTop-PADDING, xSize+((PADDING-1)*2), ySize+((PADDING-1)*2));
		//fontRenderer.drawString("Firebox", left+PADDING, top+PADDING, 0xFF404040);
		
		if (inventorySlots!=null) {
			//RenderHelper.enableGUIStandardItemLighting();
			//GlStateManager.enableRescaleNormal();
			//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			
			for(Slot slot : inventorySlots.inventorySlots) {
				GuiDrawing.drawItemSlot(guiLeft+slot.xPos-1, guiTop+slot.yPos-1);
				
				//itemRender.renderItemAndEffectIntoGUI(this.mc.player, slot.getStack(), left+slot.xPos+1, top+slot.yPos+1);
				//slot.xPos
			}
			
			//GlStateManager.disableRescaleNormal();
			//RenderHelper.disableStandardItemLighting();
		}
		
		fontRenderer.drawString(container.getLocalizedName(), guiLeft, guiTop, 0xFF404040);
		//drawItemSlot(left+PADDING, top+PADDING+18);
		//drawItemSlot(left+PADDING+18, top+PADDING+18);
		//drawItemSlot(left+PADDING, top+PADDING+18+18);
		
	}
}
