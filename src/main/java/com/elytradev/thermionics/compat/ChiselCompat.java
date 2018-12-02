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

package com.elytradev.thermionics.compat;

import com.elytradev.thermionics.block.ThermionicsBlocks;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ChiselCompat {
	public static void init() {
		String roadLoc = ThermionicsBlocks.ROAD.getRegistryName().toString();
		String compressedRoadLoc =  ThermionicsBlocks.ROAD_COMPRESSED.getRegistryName().toString();
		
		for(int i=0; i<6; i++) {
			FMLInterModComms.sendMessage("chisel", "variation:add", "thermionics_road|"+roadLoc+"|"+i);
			FMLInterModComms.sendMessage("chisel", "variation:add", "thermionics_compressed_road|"+compressedRoadLoc+"|"+i);
		}
		
		
	}
}
