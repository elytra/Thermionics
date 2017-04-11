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

import java.util.ArrayList;

import com.elytradev.concrete.gui.ConcreteContainer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Comparable to swing's JPanel, except that this is the base class for containers too - there's no way to make a
 * WContainer such that it isn't confused with Container, and we don't lose anything from the lack of abstraction.
 */
public class WPanel extends WWidget {
	protected ArrayList<WWidget> children = new ArrayList<>();
	protected boolean valid;
	
	@Override
	public void createPeers(ConcreteContainer c) {
		for(WWidget child : children) {
			child.createPeers(c);
		}
		valid = true;
	}
	
	public void remove(WWidget w) {
		children.remove(w);
		valid = false;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	/**
	 * Uses this Panel's layout rules to reposition and resize components to fit nicely in the panel.
	 */
	public void layout() {
		for(WWidget child : children) {
			if (child instanceof WPanel) ((WPanel)child).layout();
		}
	}
	
	@Override
	public void validate(ConcreteContainer c) {
		layout();
		createPeers(c);
		valid = true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for(WWidget child : children) {
			child.paintBackground(x+child.getX(), y+child.getY());
		}
	}
}
