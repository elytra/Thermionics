package com.elytradev.thermionics.network;

import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import net.minecraft.util.math.BlockPos;

public interface BlockSet {
	public boolean contains(int x, int y, int z);
	public Iterator<BlockPos> blocks();
	
	public static class BlockSingle implements BlockSet {
		private int x = 0;
		private int y = 0;
		private int z = 0;
		
		public BlockSingle(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public boolean contains(int x, int y, int z) {
			return x==this.x && y==this.y && z==this.z;
		}

		@Override
		public Iterator<BlockPos> blocks() {
			return Iterators.singletonIterator(new BlockPos(x,y,z));
		}
	}
	/*
	public static class BlockRectangle implements BlockSet {
		private int x = 0;
		private int y = 0;
		private int z = 0;
		private int xsize = 1;
		private int ysize = 1;
		private int zsize = 1;
		
		public BlockRectangle(int x, int y, int z, int xsize, int ysize, int zsize) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;
		}
		
		@Override
		public boolean contains(int x, int y, int z) {
			return
					x>=this.x && 
					y>=this.y && 
					z>=this.z && 
					x<x+xsize &&
					y<y+ysize &&
					z<z+zsize;
		}*/
	//}
}
