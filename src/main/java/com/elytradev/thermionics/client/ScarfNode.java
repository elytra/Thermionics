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

package com.elytradev.thermionics.client;

import net.minecraft.util.math.Vec3d;

public class ScarfNode {
	public float x = 0;
	public float y = 0;
	public float z = 0;
	
	public float vx = 0;
	public float vy = 0;
	public float vz = 0;
	
	//TODO: roll
	public float roll = 0;
	
	public float r = 1;
	public float g = 1;
	public float b = 1;
	
	public ScarfNode() {
		
	}
	
	public ScarfNode(Vec3d vec) {
		x = (float)vec.x;
		y = (float)vec.y;
		z = (float)vec.z;
	}
	
	public float distanceTo(ScarfNode node) {
		float dx = x-node.x;
		float dy = y-node.y;
		float dz = z-node.z;
		return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public Vec3d subtract(ScarfNode node) {
		return new Vec3d(x-node.x, y-node.y, z-node.z);
	}
	
	public void addToNode(Vec3d vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	public void setColor(int col) {
		r = ( (col >> 16) & 0xFF ) / 255.0f;
		g = ( (col >>  8) & 0xFF ) / 255.0f;
		b = ( (col >>  0) & 0xFF ) / 255.0f;
	}
	
	public void inheritMotion(ScarfNode other) {
		x = other.x;
		y = other.y;
		z = other.z;
		vx = other.vx;
		vy = other.vy;
		vz = other.vz;
	}
}
