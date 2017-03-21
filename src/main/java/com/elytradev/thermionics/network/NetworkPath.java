package com.elytradev.thermionics.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
