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

/**
 * Like a JPanel with a horizontal BoxLayout
 * 
 * <ul>
 * <li>If all children are nonresizable, they will wind up equally spaced horizontally, with their vertical position
 * centered on this Panel's centerline.
 * <li>If all children are resizable, they will wind up taking up the exact height of this Panel, and each component
 * will split this Panel's horizontal space equally.
 * <li>If some children are resizable and some children are not, each resizable child will attempt to grow equally to
 * fill the unoccupied horizontal space, (and all resizable children will still be the exact height of this Panel).
 * </ul>
 */
public class WPanelHorizontal extends WPanel {
	
	public void add(WWidget w) {
		children.add(w);
	}
	
	@Override
	public void createPeers(ConcreteContainer c) {
		for(WWidget child : children) {
			child.createPeers(c);
		}
	}
	
	@Override
	public void layout() {

		int unresizable = 0;
		int numResizable = 0;
		for(WWidget w : children) {
			if (w.canResize()) {
				numResizable++;
			} else {
				int wid = w.getWidth();
				unresizable += wid;
			}
		}
		int resizeSpace = getWidth()-unresizable;
		int resizeEach = resizeSpace / numResizable;
		if (resizeEach<18) resizeEach = 18; //Don't squish things beyond a minimum reasonable size! Better for them to overlap :/
		
		int centerline = this.getHeight()/2;
		int curLeft = 0;
		for(int i=0; i<children.size(); i++) {
			WWidget w = children.get(i);
			if (w.canResize()) {
				w.setSize(resizeEach, this.getHeight());
			}
			w.setLocation(curLeft, centerline - (w.getHeight()/2));
			curLeft += w.getWidth();
		}
		
		//Layout children after parents
		super.layout();
	}
}
