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

import java.util.ArrayList;
import java.util.Random;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.EnumWeaponSkill;
import com.elytradev.thermionics.network.SpawnParticleEmitterMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Manages long-running clientsided visual effects
 */
public class FX {
	protected static Random rnd = new Random();
	
	private static ArrayList<Emitter> animations = new ArrayList<>();
	private static ArrayList<Emitter> dead = new ArrayList<>();
	
	@SideOnly(Side.CLIENT)
	public static void handle(SpawnParticleEmitterMessage msg) {
		spawnEmitter(msg.getSkill(), Minecraft.getMinecraft().world, msg.getX(), msg.getY(), msg.getZ());
	}
	
	public static void spawnEmitter(EnumWeaponSkill skill, World world, float x, float y, float z) {
		switch(skill) {
		case SMAAAAAAASH:
		default:
				SmaaaaaaashEmitter cloud = new SmaaaaaaashEmitter(world, x,y,z);
				cloud.begin(world);
				animations.add(cloud);
		}
	}
	
	public static void spawnEmitter(Emitter emitter) {
		
	}
	
	public static void update(World world) {
		for(Emitter anim : animations) {
			anim.doUpdate(world);
		}
		
		for(Emitter anim : dead) { animations.remove(anim); }
	}
	
	// UTILS
	private static final float TAU = (float)(Math.PI*2);
	private static final float SEGMENTS = TAU/16;
	private static final ArrayList<Vec2f> locationBuffer = new ArrayList<>();
	
	private static Vec2f pickPolar(float rmin, float rmax) {
		float yaw = rnd.nextFloat()*TAU;
		float dist = rnd.nextFloat()*(rmax-rmin) + rmin; 
		float x = MathHelper.cos(yaw)*dist;
		float z = MathHelper.sin(yaw)*dist;
		return new Vec2f(x,z);
	}
	
	private static void pickRing(float r, float angularDensity) {
		for(float theta = 0; theta<TAU; theta+=angularDensity) {
			float x = MathHelper.cos(theta)*r;
			float z = MathHelper.sin(theta)*r;
			locationBuffer.add(new Vec2f(x,z));
		}
	}
	
	
	
	
	
	
	public static abstract class Emitter {
		World world;
		public float x;
		public float y;
		public float z;
		public void begin(World world) {}
		public abstract void update(World world);
		public void release() {};
		protected int ticks = 0;
		public int getTicks() { return ticks; }
		public void doUpdate(World world) {
			if (this.world!=world) {
				die();
				return;
			}
			update(world);
			ticks++;
		}
		
		public Emitter(World world, float x, float y, float z) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public void die() {
			dead.add(this);
		}
	}

	public static class SmaaaaaaashEmitter extends Emitter {
		
		public SmaaaaaaashEmitter(World world, float x, float y, float z) {
			super(world, x, y, z);
		}

		@Override
		public void begin(World world) {
			world.playSound(x, y, z, Thermionics.SOUNDEVENT_SMAAAAAAASH, SoundCategory.BLOCKS, 1f, 0.7f + (rnd.nextFloat()*0.3f), false);
		}
		
		@Override
		public void update(World world) {
			if (ticks%3==0) {
				locationBuffer.clear();
				pickRing(0.5f, SEGMENTS);
				for(Vec2f vec : locationBuffer) {
					Particle particle = new ParticleVelocity(world,
							x+vec.x, y+0.5f, z+vec.y,
							0f, -0.3f, 0f
							);
					particle.setParticleTextureIndex(5); //Midway through redstone
					particle.setRBGColorF(0.7f, 0.2f, 0.7f);
					
					
					Minecraft.getMinecraft().effectRenderer.addEffect(particle);
				}
			}
			float sx = (rnd.nextFloat()*2) - 1;
			float sz = (rnd.nextFloat()*2) - 1;
			if (rnd.nextBoolean()) {
				Particle star = new ParticleVelocity(world,
						x+sx, y-0.5f, z+sz,
						0f, 0.35f, 0f
						);
				star.setParticleTextureIndex(65); //Crit stars
				Minecraft.getMinecraft().effectRenderer.addEffect(star);
			}
			//star.setRBGColorF(0.7f, 0.2f, 0.7f);
			
			if (ticks>9) die();
		}
		
	}
}
