package com.elytradev.thermionics.data;

import net.minecraftforge.energy.IEnergyStorage;

public class NoExtractEnergyStorageView implements IEnergyStorage, ITransferRate {
	private final IEnergyStorage delegate;
	
	public NoExtractEnergyStorageView(IEnergyStorage delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return delegate.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
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
		return false;
	}

	@Override
	public boolean canReceive() {
		return delegate.canReceive();
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
