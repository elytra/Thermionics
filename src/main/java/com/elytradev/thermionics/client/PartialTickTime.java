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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartialTickTime {
	private static final float FRAMES_PER_MSEC = 1/20_000f;
	private static final float DISCONTINUITY_THRESHOLD = 3f;
	private static long curFrame = 0L;
	private static long lastFrame = 0L;
	private static float frameTime = 0f;
	
	@SideOnly(Side.CLIENT)
	public static float getFrameTime() {
		if (curFrame==lastFrame) {
			curFrame = System.nanoTime() / 1_000_000L; //Accurate as long as a frame takes less than 292 years
			frameTime = (curFrame-lastFrame)*FRAMES_PER_MSEC;
			if (frameTime>DISCONTINUITY_THRESHOLD) frameTime = DISCONTINUITY_THRESHOLD; //Don't ever jog more sharply
		}
		return frameTime;
	}
	
	@SideOnly(Side.CLIENT)
	public static void endFrame() {
		lastFrame = curFrame;
		frameTime = 0f;
	}
}
