package io.github.elytra.thermionics.tileentity;

import io.github.elytra.thermionics.CapabilityProvider;
import io.github.elytra.thermionics.Thermionics;
import io.github.elytra.thermionics.api.impl.HeatStorage;
import io.github.elytra.thermionics.api.impl.HeatStorageView;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityHeatStorage extends TileEntity  implements ITickable {
	private CapabilityProvider capabilities = new CapabilityProvider();
	private HeatStorage heatStorage;
	
	public TileEntityHeatStorage() {
		heatStorage = new HeatStorage(200);
		Thermionics.instance();
		capabilities.registerForAllSides(Thermionics.CAPABILITY_HEATSTORAGE, ()->HeatStorageView.of(heatStorage));
	}
	
	public TileEntityHeatStorage(int capacity) {
		heatStorage = new HeatStorage(capacity);
	}

	@Override
	public void update() {
		//TODO: trigger diffusion
	}
}
