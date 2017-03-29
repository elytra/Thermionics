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
package com.elytradev.thermionics.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Manages motion of RF through Thermionics blocks.
 */
public class RFTransport {
	public static final NullEnergyStorage NULL_ENERGY = new NullEnergyStorage();
	
	public static void diffuse(World world, BlockPos pos, IEnergyStorage host) {
		ArrayList<IEnergyStorage> consumers = getAdjacentStorage(world, pos);
		reorder(consumers); //We want to sample them in random order
		for(IEnergyStorage consumer : consumers) {
			//int totalEnergy = host.getEnergyStored() + consumer.getEnergyStored();
			//float energyRatio = host.getMaxEnergyStored() / (float)consumer.getMaxEnergyStored();
			float sourceLevel = host.getEnergyStored()/(float)host.getMaxEnergyStored();
			float destLevel = consumer.getEnergyStored()/(float)consumer.getMaxEnergyStored();
			if (sourceLevel>destLevel) {
				//Figure out how many quanta to transfer
				//int desiredSource = (int)(totalEnergy*energyRatio);
				//int desiredDest = (int)(totalEnergy/energyRatio);
				/*
				 * Example: 2200 source, 1700 dest, capacity for each is 4000.
				 * 
				 *   totalEnergy: 3900
				 *   energyRatio: 1.0f
				 *   sourceLevel: 0.55f
				 *   destLevel: 0.425f
				 *   availableQuanta: 500 / 2 - 1 = 249
				 * 
				 */
				
				/*
				 * Transmission data:
				 * 0 blocks: 775.0
				 * 1 block : 393.6
				 * 2 blocks: 260.8
				 * 3 blocks: 196.6
				 * 4 blocks: 157.8
				 * 5 blocks: 131.8
				 */
				
				int availableQuanta = ((int)((sourceLevel-destLevel)*host.getMaxEnergyStored())/2); //trunc leaves fractional power differences where they are
				if (availableQuanta<=0) continue;
				//StringBuilder debugString = new StringBuilder("RFTransfer: ");
				//debugString.append(host.getEnergyStored());
				//debugString.append("("); debugString.append((int)(sourceLevel*100)); debugString.append("%) -> ");
				//debugString.append(consumer.getEnergyStored());
				//debugString.append("("); debugString.append((int)(destLevel*100)); debugString.append("%) :");
				
				//debugString.append(" req="); debugString.append(availableQuanta);
				int transferred = consumer.receiveEnergy(availableQuanta, true);
				if (transferred>0) transferred = host.extractEnergy(transferred, false);
				//debugString.append(" rec="); debugString.append(transferred);
				//System.out.println(debugString.toString());
				consumer.receiveEnergy(transferred, false);
			}
		}
	}
	
	public static ArrayList<IEnergyStorage> getAdjacentStorage(World world, BlockPos pos) {
		ArrayList<IEnergyStorage> consumers = new ArrayList<>();
		
		if (pos.getY()<255) {
			IEnergyStorage up = getStorage(world,pos.up(),EnumFacing.DOWN);
			if (up.canReceive()) consumers.add(up);
		}
		
		if (pos.getY()>0) {
			IEnergyStorage down = getStorage(world, pos.down(), EnumFacing.UP);
			if (down.canReceive()) consumers.add(down);
		}
		
		IEnergyStorage north = getStorage(world, pos.north(), EnumFacing.SOUTH);
		if (north.canReceive()) consumers.add(north);
		IEnergyStorage south = getStorage(world, pos.south(), EnumFacing.NORTH);
		if (south.canReceive()) consumers.add(south);
		IEnergyStorage east = getStorage(world, pos.east(), EnumFacing.WEST);
		if (east.canReceive()) consumers.add(east);
		IEnergyStorage west = getStorage(world, pos.west(), EnumFacing.EAST);
		if (west.canReceive()) consumers.add(west);
		
		return consumers;
	}
	
	public static IEnergyStorage getStorage(World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return NULL_ENERGY; //If it can't Capabilities, it can't IEnergyStorage
		if (te.hasCapability(CapabilityEnergy.ENERGY, side)) {
			return te.getCapability(CapabilityEnergy.ENERGY, side);
		} else {
			return NULL_ENERGY; //Some day we might support other APIs. Not today.
		}
	}
	
	/**
	 * Flushes and re-adds items to the supplied List in pseudorandom order.
	 * @param t the List to reorder. Do not pass this method an immutable list!!!
	 */
	static <T> void reorder(List<T> t) {
		Random rnd = new Random();
		ArrayList<T> scratch = new ArrayList<>();
		scratch.addAll(t);
		t.clear();
		int picks = scratch.size();
		
		for(int i=0; i<picks; i++) {
			int pick = rnd.nextInt(scratch.size());
			T picked = scratch.remove(pick);
			t.add(picked);
		}
	}
	
	private static class NullEnergyStorage implements IEnergyStorage {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return 0;
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return false;
		}
	}
}
