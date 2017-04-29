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

import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Ints;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Yes, I know how to spell explosion, but that's already a class, and we're having fun blowing up stuff. Stop being so
 * prescriptivist and hold this bomb for me while I get my flint.
 * 
 * <p>This class represents a persistent, scheduled explosion of potentially unparalelled magnitude. And provides a
 * friendly interface so that you can paralell it with your own dream-wrecking crater-maker. If you make a permission /
 * area protection mod, this might be a good time to stop what you're doing, panic, and contact the elytra team so we
 * know to support it.
 */
public strictfp class BigExplosion {
	/**
	 * Because we only have one "resistance" for blocks, we don't really have data capturing the energy-flux density of
	 * impacts at the given block. So we fudge things and say that in the process of breaking, elastic deformations
	 * diminish the power of the gas/metal jet by a tiny fraction of the material's static density.
	 */
	public static final float RESISTANCE_SCALE = 0.025f;
	public static final float TAU = (float)(Math.PI*2d);
	public static final float PI = (float)Math.PI;
	public static final float QUARTER = PI/2f;
	
	private long halflife = 0L; //More than zero halflife will cause the area to be radioactive after the blast
	private int radioactivity = 0; //Radioactivity determines the strength of the radiation damage. Keep it low!
	private int power = 50; //Power overcomes hardness. e.g. 10 power will be consumed by a single stone block.
	private int radius = 50; //Radius limits the explosion size. Often power and radius are related.
	private Explosion dummyExplosion = null;
	
	private MutableVector epicenter;
	private MutableVector cursor;
	private int curRadius = 0;
	private int stepInRadius = 0;
	private long lifetime = 0L;
	private boolean dead = false;
	
	float[] cylinderMap = new float[16 * 11];
	
	/**
	 * Creates a conventional explosion out to the specified radius which can be stopped by obsidian.
	 * @param pos The epicenter of the explosion
	 * @param radius The maximum radius at which damage will occur, in blocks (meters)
	 */
	public BigExplosion(BlockPos pos, int radius) {
		this.radius = radius;
		this.power = Math.min(2000, radius);
		this.epicenter = new MutableVector().withPosition(pos);
		clearCylinderMap();
	}
	
	/**
	 * Creates a conventional explosion with the specified radius and power. More than 2000 will power through small
	 * amounts of obsidian.
	 * @param pos The epicenter of the explosion
	 * @param radius The maximum radius at which damage will occur, in blocks (meters)
	 * @param power The power at the center of the explosion. This is diminished as the explosion travels through blocks. After 2000 power the explosion begins traveling through obsidian.
	 */
	public BigExplosion(BlockPos pos, int radius, int power) {
		this.epicenter = new MutableVector().withPosition(pos);
		this.radius = radius;
		this.power = power;
		clearCylinderMap();
	}
	
	/**
	 * Creates a nuclear/radioactive explosion. Nuclear explosions cause lingering environmental damage
	 * @param pos The epicenter of the explosion
	 * @param radius The maximum radius at which damage will occur, in blocks (meters)
	 * @param power The power at the center of the explosion. This is diminished as the explosion travels through blocks. After 2000 power the explosion begins traveling through obsidian.
	 * @param radioactivity The strength of the radiation effect (0 is still quite strong). *this ignores conventional armor*
	 * @param halflife The duration of the radioactivity effect, in ticks.
	 */
	public BigExplosion(BlockPos pos, int radius, int power, int radioactivity, long halflife) {
		this.epicenter = new MutableVector().withPosition(pos);
		this.radius = radius;
		this.power = power;
		this.radioactivity = radioactivity;
		this.halflife = halflife;
		clearCylinderMap();
	}
	
	public int getRadius() { return radius; }
	public int getPower() { return power; }
	public int getRadioactivity() { return (halflife>0L) ? radioactivity : 0; }
	public long getHalflife() { return halflife; }
	public boolean isDead() { return dead; }
	public long getLifetime() { return lifetime; }
	
	private String explainCylinderMap() {
		StringBuilder str = new StringBuilder();
		int idx = 0;
		for(int y=0; y<11; y++) {
			for(int x=0; x<11; x++) {
				float cur = cylinderMap[idx];
				str.append(explainFloat(cur));
				idx++;
			}
			str.append('\n');
		}
		str.setLength(str.length()-1);
		return str.toString();
	}
	
	private char explainFloat(float f) {
		String glyphs = " .-:=+*%#@";
		int idx = (int)((f / power) * glyphs.length()); if (idx>=glyphs.length()) idx = glyphs.length()-1;
		return glyphs.charAt(idx);
	}
	
	public int tick(World w) {
		if (w.isRemote) return 0; //it should NEVER come to this, but let's guard against malicious calls.
		if (curRadius>radius*2) {
			dead = true;
			return 0;
		}
		int r2 = radius * radius;
		
		if (dummyExplosion==null) {
			dummyExplosion = new Explosion(w, null, epicenter.x+0.5D, epicenter.y+0.5D, epicenter.z+0.5D, radius, true, true);
			List<Entity> entitiesAffected = w.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(epicenter.x - radius, epicenter.y - radius, epicenter.z - radius, epicenter.x + radius, epicenter.y + radius, epicenter.z + radius));
			boolean deny = ForgeEventFactory.onExplosionStart(w, dummyExplosion);
			if (deny) {
				dead = true;
				return 0;
			}
		}
		
		if (cursor==null) cursor = new MutableVector();
		int exploded = 0;
		ManhattanMath.getHullLocation(curRadius, stepInRadius, epicenter, cursor);
		
		
		//We've got our column
		for(int i=radius; i>=-radius; i--) {
		//for(int i=0; i<(radius*2+1); i++) {
		//	int targetY = epicenter.y + ManhattanMath.wiggle(i);
			int targetY = epicenter.y + i;
			if (targetY<0 || targetY>255) continue;
			cursor.y = targetY;
			
			int dx = Math.abs(epicenter.x - cursor.x);
			int dy = Math.abs(epicenter.y - cursor.y);
			int dz = Math.abs(epicenter.z - cursor.z);
			if ((dx*dx + dy*dy + dz*dz) > r2) continue;
			
			//float powerHere = sampleCylinderMap(epicenter, cursor);
			float powerHere = power;
			//System.out.println("Cylindermap sampled:"+powerHere);
			if (powerHere<=0) continue;
			float powerRemaining = raycast(w, epicenter, cursor, powerHere);
			//System.out.println("Power remaining after raycast:"+powerRemaining);
			setCylinderMap(epicenter, cursor, powerRemaining);
			//float powerRemaining = explodeBlock(w, new BlockPos(cursor.x,cursor.y,cursor.z), powerHere);
			exploded++;
		}
		
		//System.out.println(explainCylinderMap());
		
		/*if (curRadius>1 && ManhattanMath.isWithinHull(curRadius-1, stepInRadius)) {
			IBlockState air = Blocks.AIR.getDefaultState();
			
			ManhattanMath.getHullLocation(curRadius-1, stepInRadius, epicenter, cursor);
			BlockPos top = w.getTopSolidOrLiquidBlock(new BlockPos(cursor.x,cursor.y,cursor.z));
			//try {
				
				while(top.getY()>0) {
					IBlockState state = w.getBlockState(top);
					if (isFluid(state.getBlock())) {
						w.setBlockState(top, air, 0);
						//w.setBlockToAir(top);
					} else {
						break;
					}
					top = top.down();
				}
			//} catch (Throwable t) {
				//Force noisy error but keep on trucking - suppress the stacktrace because it'd definitely crash the server printing them out at the rate they'd be generated.
			//	System.out.println("Block "+w.getBlockState(top).getBlock().getRegistryName()+" errored during neighborChange!");
			//}
		}*/
		
		stepInRadius++;
		if (!ManhattanMath.isWithinHull(curRadius, stepInRadius)) {
			curRadius++;
			stepInRadius = 0;
		}
		
		lifetime++;
		
		//if (exploded==0) this.dead = true;
		return exploded;
	}
	
	public void clearCylinderMap() {
		Arrays.fill(cylinderMap, power);
		//TODO: Feather power down at steep angles
	}
	
	private static float normalizeAngle(float angle) {
		if (angle>=0f && angle<TAU) return angle;
		while(angle<0f) angle+=TAU;
		while(angle>=TAU) angle-=TAU;
		return angle;
	}
	
	public float sampleCylinderMap(MutableVector center, MutableVector cursor) {
		float dx = cursor.x - center.x;
		float dy = cursor.y - center.y;
		float dz = cursor.z - center.z;
		float dxz = (float)Math.sqrt(dx*dx + dz*dz);
		
		float yaw = (float)Math.atan2(dz, dx);
		float pitch = (float)Math.atan2(dy, dxz); //pitch relative to the rotated coords!
		
		yaw = normalizeAngle(yaw);
		pitch = normalizeAngle(pitch);
		if (pitch>PI) pitch=PI; //We only accept a half circle worth of pitch.
		
		int mapX = (int)(yaw * (16f / TAU)); if (mapX>=16) mapX=15; if (mapX<0) mapX=0;
		int mapY = (int)(pitch * (11f / PI)); if (mapY>=11) mapY=10; if (mapY<0) mapY=0;
		
		//System.out.println("delta:"+dx+","+dy+","+dz+" Sampling coords: "+mapX+","+mapY+" result:"+cylinderMap[mapY*16+mapX]);
		
		return cylinderMap[mapY*16+mapX];
	}
	
	public void setCylinderMap(MutableVector center, MutableVector cursor, float power) {
		float dx = cursor.x - center.x;
		float dy = cursor.y - center.y;
		float dz = cursor.z - center.z;
		float dxz = (float)Math.sqrt(dx*dx + dz*dz);
		
		float yaw = (float)Math.atan2(dz, dx);
		float pitch = (float)Math.atan2(dy, dxz); //pitch relative to the rotated coords!
		
		yaw = normalizeAngle(yaw);
		pitch = normalizeAngle(pitch);
		if (pitch>PI) pitch=PI; //We only accept a half circle worth of pitch.
		
		int mapX = (int)(yaw * (16f / TAU)); if (mapX>=16) mapX=15; if (mapX<0) mapX=0;
		int mapY = (int)(pitch * (11f / PI)); if (mapY>=11) mapY=10; if (mapY<0) mapY=0;
		
		cylinderMap[mapY*16+mapX] = power;
	}
	
	public float explodeBlock(World world, BlockPos pos, float power) {
		if (world.isAirBlock(pos)) return power;
		
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		
		if (block==Blocks.BEDROCK) return 0;
		
		//Just delete fluids without examination or notification so their neighbors don't try to fill them in.
		if (isFluid(block)) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 16);
			return power;
		}
		
		float resistance = block.getExplosionResistance(world, pos, null, dummyExplosion);
		if (resistance>power) return 0;
		
		boolean drop = state.getBlock().canDropFromExplosion(dummyExplosion);
		
		if (drop && world.rand.nextInt(1000)==5) {
			block.dropBlockAsItem(world, pos, state, 0);
		}
		//block.onBlockExploded(world, pos, dummyExplosion); //Also calls onBlockDestroyedByExplosion
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 16); //Observers are blinded by explosions or something
		
		return power - (int)(resistance*RESISTANCE_SCALE);
	}
	
	public boolean isFluid(Block block) {
		return
				block instanceof BlockLiquid || //Vanilla fluid block
				block instanceof IFluidBlock;   //Forge fluid block or other relatively polite mod fluids
		//Any fluid that isn't one of these is going to also behave and render very differently from vanilla fluids.
	}
	
	/**
	 * Casts a ray from origin to target, destroying blocks along the way and diminishing power.
	 * 
	 * <li>Does not alter origin or target vector.
	 * <li>Destroys blocks
	 * <li>Returns unused power to store back in the cylinder map
	 */
	public float raycast(World world, MutableVector origin, MutableVector target, float power) {
		IBlockState air = Blocks.AIR.getDefaultState();
		float x = origin.x + 0.5f;
		float y = origin.y + 0.5f;
		float z = origin.z + 0.5f;
		float dx = target.x - origin.x;
		float dy = target.y - origin.y;
		float dz = target.z - origin.z;
		int d = Ints.max((int)Math.abs(dx), (int)Math.abs(dy), (int)Math.abs(dz));
		dx /= (float)d;
		dy /= (float)d;
		dz /= (float)d;
		float curPower = power;
		for(int i=0; i<=d; i++) {
			BlockPos here = new BlockPos((int)x, (int)y, (int)z);
			IBlockState state = world.getBlockState(here);
			if (state!=air) {
				//curPower = 
				float remaining = explodeBlock(world, here, curPower);
				if (remaining<=0) return 0;
				//if (curPower<=0) return 0;
				
			}
			
			x += dx;
			y += dy;
			z += dz;
		}
		
		return curPower;
	}
}
