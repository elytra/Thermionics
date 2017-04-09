package com.elytradev.concrete.gui.widget;

public class WGridPanel extends WPanel {
	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.setLocation(x*18, y*18);
		if (w.canResize()) {
			w.setSize(18, 18);
		}
		valid = false;
	}
}
