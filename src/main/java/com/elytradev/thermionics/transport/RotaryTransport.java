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

import java.util.Set;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IRotaryPowerConsumer;
import com.elytradev.thermionics.api.IRotaryPowerSupply;
import com.elytradev.thermionics.block.BlockAxle;
import com.elytradev.thermionics.block.BlockGearbox;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotaryTransport {
	public static void diffuse(World world, BlockPos pos, IRotaryPowerSupply host) {
		
	}
	
	public static void searchForConsumers(World world, BlockPos pos, EnumFacing dir, Set<BlockPos> searchedNodes, Set<BlockPos> consumers) {
		searchedNodes.add(pos); //Make sure we're on the list of searched nodes.
		BlockPos cur = pos;
		for(int i=0; i<16; i++) {
			cur = cur.offset(dir);
			if (!isAxle(world,cur)) {
				if (isConsumer(world,cur,dir)) {
					if (!searchedNodes.contains(cur)) {
						consumers.add(cur);
						searchedNodes.add(cur);
					}
					
					break;
				} else {
					if (isGearbox(world,cur)) {
						//Recursive search
						if (!searchedNodes.contains(cur)) {
							searchedNodes.add(cur);
							for(EnumFacing f : EnumFacing.values()) {
								if (f != dir.getOpposite()) searchForConsumers(world, cur, f, searchedNodes, consumers);
							}
						}
					}
					
					break;
				}
			}
		}
	}
	
	public static float getTorqueLoad(World world, Set<BlockPos> consumers) {
		float result = 0f;
		for(BlockPos pos : consumers) {
			TileEntity te = world.getTileEntity(pos);
			if (te!=null && te.hasCapability(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, null)) {
				IRotaryPowerConsumer cap = te.getCapability(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, null);
				result += cap.getRequiredTorque();
			}
		}
		return result;
	}
	
	public static void deliverRevolutions(World world, BlockPos pos, float revolutions) {
		TileEntity te = world.getTileEntity(pos);
		if (te!=null && te.hasCapability(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, null)) {
			IRotaryPowerConsumer cap = te.getCapability(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, null);
			cap.supplyRevolutions(revolutions);
		}
	}
	
	public static boolean isAxle(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return (state.getBlock() instanceof BlockAxle);
	}
	
	public static boolean isConsumer(World world, BlockPos pos, EnumFacing facing) {
		TileEntity te = world.getTileEntity(pos);
		if (te==null) return false;
		return te.hasCapability(Thermionics.CAPABILITY_ROTARYPOWER_CONSUMER, facing);
	}
	
	public static boolean isGearbox(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof BlockGearbox;
	}
}
