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

package com.elytradev.thermionics.network;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Represents one leg of an undirected graph. One Path must be wholly contained within one world
 */
public class NetworkPath {
	private World world;
	private BlockPos a = BlockPos.ORIGIN;
	private EnumFacing aFacing = EnumFacing.UP;
	private BlockPos b = BlockPos.ORIGIN;
	private EnumFacing bFacing = EnumFacing.UP;
	private LinkedHashSet<BlockPos> path = new LinkedHashSet<>();
	private int pathLength = 0;
	
	public NetworkPath(BlockPos a, EnumFacing aFacing, BlockPos b, EnumFacing bFacing, Set<BlockPos> path) {
		this.a = a;
		this.b = b;
		
		BlockPos firstBlock = path.stream().findFirst().orElse(null);
		//if (firstBlock!=null) aFacing = getFacingFrom
		
		BlockPos lastBlock = path.stream().reduce(null, (u,v)->v);
		
	}
	
	
	public boolean areEndpointsLoaded() {
		return world.isBlockLoaded(a) && world.isBlockLoaded(b);
	}
	
	/**
	 * If something enters a network 
	 * @param origin
	 * @return
	 */
	@Nullable
	public BlockPos getEndpointFrom(BlockPos origin) {
		if (origin.equals(a)) return b;
		if (origin.equals(b)) return a;
		return null;
	}
	
	public EnumFacing getFacingFrom(BlockPos origin) {
		if (origin.equals(a)) return aFacing;
		if (origin.equals(b)) return bFacing;
		return null;
	}
	
	public Set<NetworkPath> bisectAt(BlockPos pos) {
		Set<NetworkPath> result = new HashSet<>();
		
		return result;
	}
	
	/**
	 * Represents the entrance to a cable, pipe, wire, tube, 
	 */
	public static class Endpoint {
		private BlockPos location;
		private EnumFacing facing;
		
		public Endpoint(BlockPos loc) {
			this.location = loc;
			this.facing = EnumFacing.UP;
		}
		
		public Endpoint(BlockPos loc, EnumFacing facing) {
			this.location = loc;
			this.facing = facing;
		}
		
		public BlockPos getLocation() { return location; }
		public EnumFacing getFacing() { return facing; }
	}
}
