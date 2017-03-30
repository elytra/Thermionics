package com.elytradev.thermionics.tileentity;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.HeatStorageView;
import com.elytradev.thermionics.data.RelativeDirection;

public class TileEntityConvectionMotor extends TileEntityMachine {
	private HeatStorage heatStorage = new HeatStorage(1000);
	
	public TileEntityConvectionMotor() {
		capabilities.registerForSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->HeatStorageView.insertOnlyOf(heatStorage),
				RelativeDirection.ALL_SIDES
				);
		capabilities.registerForSides(Thermionics.CAPABILITY_HEATSTORAGE,
				()->heatStorage,
				RelativeDirection.WITHIN);
	}
}
