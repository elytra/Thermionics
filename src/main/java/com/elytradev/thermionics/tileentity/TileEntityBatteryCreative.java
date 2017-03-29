package com.elytradev.thermionics.tileentity;

import com.elytradev.thermionics.block.BlockBattery;
import com.elytradev.thermionics.transport.RFTransport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityBatteryCreative extends TileEntityBattery {
	public TileEntityBatteryCreative() {
		
	}
	
	@Override
	public void update() {
		this.energyStorage.tick();
		IBlockState cur = world.getBlockState(pos);
		EnumFacing facing = cur.getValue(BlockBattery.FACING);
		IEnergyStorage target = RFTransport.getStorage(world, pos.offset(facing), facing.getOpposite());
		if (target.canReceive()) {
			int toPush = Math.min(energyStorage.getEnergyStored(), target.getMaxEnergyStored());
			int received = target.receiveEnergy(toPush, false);
			if (received>0) energyStorage.extractEnergy(received, false);
			energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false);
		}
	}
}
