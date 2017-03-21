package com.elytradev.thermionics.data;

import java.util.LinkedHashSet;

public class OrderedSet<E> extends LinkedHashSet<E> {
	private E head = null;
	private E tail = null;
	
	@Override
	public boolean add(E e) {
		if (this.isEmpty()) head = null;
		if (super.add(e)) {
			tail = e;
			return true;
		} else {
			return false;
		}
	}
	
	public E getHead() {
		return head;
	}
	
	public E getTail() {
		return tail;
	}
}
