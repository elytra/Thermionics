package com.elytradev.thermionics.gui;

import java.util.ArrayList;

import com.elytradev.thermionics.data.ContainerTesting;

/**
 * Comparable to swing's JPanel, except that this is the base class for containers too - there's no way to make a
 * WContainer such that it isn't confused with Container, and we don't lose anything from the lack of abstraction.
 */
public class WPanel extends WWidget {
	protected ArrayList<WWidget> children = new ArrayList<>();
	
	
	/* handled by subclasses
	public void add(WWidget w) {
		children.add(w);
	}*/
	
	@Override
	public void createPeers(ContainerTesting c) {
		for(WWidget child : children) {
			child.createPeers(c);
		}
	}
	
	public void remove(WWidget w) {
		children.remove(w);
	}
}
