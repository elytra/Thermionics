package com.elytradev.thermionics.tileentity;

import com.elytradev.thermionics.api.IRotaryRecipe;

public class TileEntitySerger extends TileEntityMachine {
	private float revolutionsNeeded = 0f;
	private float revolutionsProcessed = 0f;
	private int rpm = 0;
	private IRotaryRecipe lastRecipe = null;
}
