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

import java.util.Iterator;

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
