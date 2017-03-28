package com.elytradev.thermionics.data;

import net.minecraftforge.energy.IEnergyStorage;

public interface ITransferRate extends IEnergyStorage {
	/**
	 * Gets the maximum RF/FE/FU transfer rate of this device
	 */
	public int getMaxTransfer();
	/**
	 * Gets the amount of RF/FE/FU currently being transferred per tick. May be averaged over several ticks.
	 */
	public float getCurTransfer();
}