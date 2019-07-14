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

package com.elytradev.libasplod;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BigExplosionHandler {
	private static final int BLOCK_BUDGET = 16000;
	private static BigExplosionHandler INSTANCE = null;
	private HashMap<Integer, Scheduler> schedulers = new HashMap<>();
	private int inc = 0;
	
	@Nullable
	public Scheduler getOrCreateScheduler(World w) {
		if (w.isRemote) return null;
		int wid = w.provider.getDimension();
		synchronized(schedulers) {
			if (schedulers.containsKey(wid)) return schedulers.get(wid);
			Scheduler scheduler = new Scheduler(wid);
			schedulers.put(w.getWorldType().getId(), scheduler);
			return scheduler;
		}
	}
	
	private BigExplosionHandler() {}
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase!=TickEvent.Phase.START) return;
		inc++;
		if (inc>=10) { //Only pulse about twice a second.
			inc = 0;
			synchronized(schedulers) {
				for(Scheduler scheduler : schedulers.values()) {
					scheduler.tick();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (event.getWorld().isRemote) return;
		Integer worldid = event.getWorld().provider.getDimension();
		synchronized(schedulers) {
			if (schedulers.containsKey(worldid)) {
				String worldName = event.getWorld().getWorldInfo().getWorldName();
				System.out.println("Clean unload for scheduler on world '"+worldName+"'.");
				//TODO: Save data to world
				schedulers.remove(worldid);
			}
		}
	}
	
	public static BigExplosionHandler instance() {
		if (INSTANCE==null) INSTANCE = new BigExplosionHandler();
		return INSTANCE;
	}
	
	public static class Scheduler {
	
		private int worldid;
		private ArrayList<BigExplosion> activeAsplosions = new ArrayList<BigExplosion>();
		private ArrayList<BigExplosion> outgoing = new ArrayList<BigExplosion>();
		
		private Scheduler(int worldid) {
			this.worldid = worldid;
			//TODO: Load pending tasks from worldSavedData
		}
		
		public void schedule(BigExplosion asplosion) {
			activeAsplosions.add(asplosion);
		}
		
		private void tick() {
			World w = DimensionManager.getWorld(worldid);
			if (w==null) {
				System.out.println("Warning: Unclean world unload! Explosions will be lost for dimension "+worldid);
				return;
			}
			
			int blockBudget = BLOCK_BUDGET;
			while(!activeAsplosions.isEmpty() && blockBudget>0) {
				for(BigExplosion a : activeAsplosions) {
					blockBudget -= a.tick(w);
					if (a.isDead()) outgoing.add(a);
				}
				for(BigExplosion a : outgoing) {
					System.out.println("Finished an explosion.");
					activeAsplosions.remove(a);
				}
			}
		}
	}
}
