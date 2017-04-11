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
package com.elytradev.concrete.gui.widget;

import com.elytradev.concrete.gui.ConcreteContainer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WWidget {
	private boolean valid = false;
	protected WPanel parent;
	private int x = 0;
	private int y = 0;
	private int width = 18;
	private int height = 18;
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean canResize() {
		return false;
	}
	
	public void setParent(WPanel parent) {
		this.parent = parent;
	}
	
	/**
	 * Draw this Widget at the specified coordinates. The coordinates provided are the top-level device coordinates of
	 * this widget's topleft corner, so don't translate by the widget X/Y! That's already been done. Your "valid"
	 * drawing space is from (x,y) to (x+width-1, y+height-1) inclusive. However, no scissor or depth masking is done,
	 * so please take care to respect your boundaries.
	 * @param x The X coordinate of the leftmost pixels of this widget in device (opengl) coordinates
	 * @param y The Y coordinate of the topmost pixels of this widget in device (opengl) coordinates
	 */
	public void paint(int x, int y) {
		
	}
	
	/**
	 * Creates "heavyweight" component peers
	 * @param c the top-level Container that will hold the peers
	 */
	public void createPeers(ConcreteContainer c) {
	}

	@SideOnly(Side.CLIENT)
	public void paintBackground(int x, int y) {
	}
	
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Creates component peers, lays out children, and initializes animation data for this Widget and all its children.
	 * The host container must clear any heavyweight peers from its records before this method is called.
	 */
	public void validate(ConcreteContainer host) {
		valid = true;
	}
	
	/**
	 * Marks this Widget as having dirty state; component peers may need to be recreated, children adapted to a new size,
	 * and animation data reset.
	 */
	public void invalidate() {
		valid = false;
	}
}
