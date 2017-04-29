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
package com.elytradev.libasplod;

public class ManhattanMath {
	/*
	 * Notes:
	 * 
	 * A hullBlocks2d is a diamond shape. A radius of 0 is the center block only. A radius of 1 is the 4 blocks around
	 * it, 2 is the 8 blocks surrounding those 4 blocks, and so on. It happens to be a linear progression.
	 * 
	 * An areaBlocks2d is that same diamond shape, but filled. So a radius of 0 is the center block only. A radius of 1
	 * is 5 blocks in a "plus" shape. A radius of 2 is those 5 blocks plus the 8 blocks surrounding them, for a total of
	 * 13.
	 * 
	 * Generally if invalid (negative) radii are handed to these equations they'll flip the sign for you.
	 * 
	 * 
	 * Now, it happens that in a diamond-shaped hull, if you cut it in half, will never have any overlapping blocks. In
	 * fact, since every "column" of the diamond cross-section is occupied, it has the exact same number of blocks as
	 * the 2d area. So in order to find the 3d hull count, we roll back the radius by one, grab the half-hull, multiply
	 * it by 2 to get both top-and-bottom, and then get the last diamond and add it on to account for the seam.
	 */
	
	
	/** Get the number of blocks in the diamond pattern at the specified manhattan radius */
	public static final int hullBlocks2d(int radius) {
		if (radius<0) radius = -radius;
		if (radius==0) return 1;
		return 4*radius;
	}
	
	/** Gets the area, in square meters, that a manhattan diamond at the specified radius occupies */
	public static final int areaBlocks2d(int radius) {
		if (radius<0) radius=-radius;
		if (radius==0) return 1;
		if (radius==1) return 5;
		int result = 0;
		for(int i=0; i<=radius; i++) {
			result += hullBlocks2d(i);
		}
		return result;
	}
	
	public static boolean isWithinHull(int radius, int step) {
		return step<hullBlocks2d(radius); //For example, the valid radius 1 steps are [0, 1, 2, 3]
	}
	
	/** Place the cursor at the center's Y, and the step's x/z. */
	public static void getHullLocation(int radius, int step, MutableVector center, MutableVector cursor) {
		cursor.y = center.y;
		if (radius==0) {
			cursor.x = center.x;
			cursor.z = center.z;
			return;
		}
		int lineLength = radius;
			
		int line = step / lineLength;
		int lineStep = step % lineLength;
		if (line==0) {
			//we're in the line running from the top middle to the right middle
			cursor.x = center.x + lineStep;
			cursor.z = center.z - radius + lineStep;
		} else if (line==1) {
			cursor.x = center.x + radius - lineStep;
			cursor.z = center.z + lineStep;
		} else if (line==2) {
			cursor.x = center.x - lineStep;
			cursor.z = center.z + radius - lineStep;
		} else if (line==3) {
			cursor.x = center.x - radius + lineStep;
			cursor.z = center.z - lineStep;
		} else {
			//Invalid positions return to the start column of the hull
			cursor.x = center.x;
			cursor.z = center.z - radius;
		}
	}
	
	public static int wiggle(int i) {
		int tmp = i / 2;
		return (i%2==0) ? -tmp : tmp;
	}
	
	public static int clampToChunk(int i) {
		if (i<0) return 0;
		if (i>255) return 255;
		return i;
	}
	
	/** Gets the number of blocks sitting on the surface of a 3d manhattan diamond of the given radius */
	public static final int hullBlocks3d(int radius) {
		return hullBlocks2d(radius) + 2*(areaBlocks2d(radius-1));
	}
	
	public static final int hullBlocksSquare(int radius) {
		if (radius<0) radius=-radius;
		if (radius==0) return 1;
		return 8*radius;
	}
	
	public static boolean spiral3dX(MutableVector center, MutableVector cursor, int radius, int step) {
		if (step<0) return false;
		
		int blocksAccountedFor = 0;
		int hullNumber = -1;
		while(blocksAccountedFor<step) {
			
		}
		return true;
	}
}
