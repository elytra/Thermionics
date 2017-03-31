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
