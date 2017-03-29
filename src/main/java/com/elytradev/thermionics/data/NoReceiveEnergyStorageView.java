package com.elytradev.thermionics.data;

import net.minecraftforge.energy.IEnergyStorage;

public class NoReceiveEnergyStorageView implements IEnergyStorage, ITransferRate {
	private final IEnergyStorage delegate;
	
	public NoReceiveEnergyStorageView(IEnergyStorage delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return delegate.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return delegate.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return delegate.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return delegate.canExtract();
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public int getMaxTransfer() {
		if (delegate instanceof ITransferRate) return ((ITransferRate)delegate).getMaxTransfer();
		return delegate.getMaxEnergyStored();
	}

	@Override
	public float getCurTransfer() {
		if (delegate instanceof ITransferRate) return ((ITransferRate)delegate).getMaxTransfer();
		return 0;
	}
}
