package com.elytradev.thermionics.item;

import net.minecraft.util.IStringSerializable;

public enum EnumAllomanticPowder implements IStringSerializable {
	COPPER("copper");

	private final String name;
	EnumAllomanticPowder(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
