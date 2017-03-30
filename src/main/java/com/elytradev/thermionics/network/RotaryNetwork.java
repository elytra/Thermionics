package com.elytradev.thermionics.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RotaryNetwork {
	private static ArrayList<RotaryNetwork> liveNetworks = new ArrayList<>();
	
	public IBlockAccess world = null;
	public HashMap<BlockPos, NetworkMember> membership = new HashMap<>();
	public ArrayList<Node> nodes = new ArrayList<>();
	
	
	private RotaryNetwork(IBlockAccess world, BlockPos pos) {
		this.world = world;
		this.nodes.add(new Node(pos));
	}
	
	protected void addNode(Node node) {
		nodes.add(node);
	}
	
	protected boolean tryAddNode(IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Nullable
	public RotaryNetwork discover(IBlockAccess world, BlockPos pos) {
		return null;
	}
	
	/** Tagging interface for network membership */
	public interface NetworkMember {}
	
	public static class Node implements NetworkMember {
		private BlockPos pos;
		private ArrayList<Edge> edges = new ArrayList<>();
		
		public Node(BlockPos pos) {
			this.pos = pos;
		}
	}
	
	public static class Edge implements NetworkMember {
		private Node a;
		private Node b;
		private HashSet<BlockPos> path;
		public boolean contains(BlockPos pos) {
			return path.contains(pos);
		}
		
		public void add(BlockPos pos) {
			path.add(pos);
		}
		
		@Nonnull
		public Node endpoint(Node from) {
			if (from==a) return b;
			return a;
		}
	}
}
