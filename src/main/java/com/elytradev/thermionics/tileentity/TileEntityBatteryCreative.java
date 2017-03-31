/**
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
