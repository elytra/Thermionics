/**
 * MIT License
 *
 * Copyright (c) 2017 The Isaac Ellingson (Falkreon) and contributors
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
package io.github.elytra.thermionics.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.util.EnumFacing;

public enum RelativeDirection {
	/** From the device's perspective, its front face */
	BOW,
	/** From the device's perspective, its left face */
	PORT,
	/** From the device's perspective, its right face */
	STARBOARD,
	/** From the device's perspective, its rear face */
	STERN,
	/** From the device's perspective, its top face */
	TOP,
	/** From the device's perspective, its bottom face */
	BOTTOM;
	
	@Nonnull
	public static RelativeDirection of(@Nonnull EnumFacing deviceFacing, @Nullable EnumFacing side) {
		if (side==null) return null; //valid configuration
		Validate.notNull(deviceFacing);
		
		if (deviceFacing==side) return BOW;
		if (deviceFacing==side.getOpposite()) return STERN;
		
		//when facing up and down, the block has a preferred, unchangeable Y rotation
		if (deviceFacing==EnumFacing.UP) {
			switch(side) {
			case NORTH:
				return TOP;
			case EAST:
				return PORT;
			case SOUTH:
				return BOTTOM;
			case WEST:
				return STARBOARD;
			default:
				throw new AssertionError("Unreachable code reached (side="+side.getName()+" deviceFacing="+deviceFacing.getName()+")");
			}
		}
		
		if (deviceFacing==EnumFacing.DOWN) {
			switch(side) {
			case NORTH:
				return BOTTOM;
			case EAST:
				return PORT;
			case SOUTH:
				return TOP;
			case WEST:
				return STARBOARD;
			default:
				throw new AssertionError("Unreachable code reached.");
			}
		}
		
		//From here on we're dealing with planar rotations
		
		if (side==EnumFacing.DOWN) return BOTTOM;
		if (side==EnumFacing.UP) return TOP;
		if (side==deviceFacing.rotateY()) return STARBOARD;
		if (side==deviceFacing.rotateYCCW()) return PORT;
		
		throw new AssertionError("Unreachable code reached (side="+side.getName()+" deviceFacing="+deviceFacing.getName()+")");
	}
}
