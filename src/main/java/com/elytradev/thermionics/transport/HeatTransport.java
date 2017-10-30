/*
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

package com.elytradev.thermionics.transport;

import java.util.ArrayList;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IHeatStorage;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeatTransport {
	public static final IHeatStorage NULL_HEAT = new NullHeatStorage();
	
	public static void diffuse(World world, BlockPos pos, IHeatStorage host) {
		ArrayList<IHeatStorage> consumers = getAdjacentStorage(world, pos);
		RFTransport.reorder(consumers); //We want to sample them in random order
		for(IHeatStorage consumer : consumers) {
			float sourceLevel = host.getHeatStored()/(float)host.getMaxHeatStored();
			float destLevel = consumer.getHeatStored()/(float)consumer.getMaxHeatStored();
			if (sourceLevel>destLevel) {
				int availableQuanta = ((int)((sourceLevel-destLevel)*host.getMaxHeatStored())/2); //trunc leaves fractional power differences where they are
				if (availableQuanta<=0) continue;
				int transferred = consumer.receiveHeat(availableQuanta, true);
				if (transferred>0) transferred = host.extractHeat(transferred, false);
				consumer.receiveHeat(transferred, false);
			}
		}
	}
	
	public static ArrayList<IHeatStorage> getAdjacentStorage(World world, BlockPos pos) {
		ArrayList<IHeatStorage> consumers = new ArrayList<>();
		
		if (pos.getY()<255) {
			IHeatStorage up = getStorage(world,pos.up(),EnumFacing.DOWN);
			if (up.canReceiveHeat()) consumers.add(up);
		}
		
		if (pos.getY()>0) {
			IHeatStorage down = getStorage(world, pos.down(), EnumFacing.UP);
			if (down.canReceiveHeat()) consumers.add(down);
		}
		
		IHeatStorage north = getStorage(world, pos.north(), EnumFacing.SOUTH);
		if (north.canReceiveHeat()) consumers.add(north);
		IHeatStorage south = getStorage(world, pos.south(), EnumFacing.NORTH);
		if (south.canReceiveHeat()) consumers.add(south);
		IHeatStorage east = getStorage(world, pos.east(), EnumFacing.WEST);
		if (east.canReceiveHeat()) consumers.add(east);
		IHeatStorage west = getStorage(world, pos.west(), EnumFacing.EAST);
		if (west.canReceiveHeat()) consumers.add(west);
		
		return consumers;
	}
	
	public static IHeatStorage getStorage(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return NULL_HEAT; //If it can't Capabilities, it can't IHeatStorage
		if (te.hasCapability(Thermionics.CAPABILITY_HEATSTORAGE, side)) {
			return te.getCapability(Thermionics.CAPABILITY_HEATSTORAGE, side);
		} else {
			return NULL_HEAT; //Some day we might support other APIs. Not today.
		}
	}
	
	public static class NullHeatStorage implements IHeatStorage {
		@Override
		public int getHeatStored() {
			return 0;
		}

		@Override
		public int getMaxHeatStored() {
			return 0;
		}

		@Override
		public boolean canReceiveHeat() {
			return false;
		}

		@Override
		public boolean canExtractHeat() {
			return false;
		}

		@Override
		public int receiveHeat(int amount, boolean simulate) {
			return 0;
		}

		@Override
		public int extractHeat(int amount, boolean simulate) {
			return 0;
		}
	}
}
