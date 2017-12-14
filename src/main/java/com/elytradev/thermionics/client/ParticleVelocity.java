package com.elytradev.thermionics.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleVelocity extends Particle {

	
	public ParticleVelocity(World world, float x, float y, float z, float vx, float vy, float vz) {
		super(world, x, y, z, vx, vy, vz);
		this.motionX = vx;
		this.motionY = vy;
		this.motionZ = vz;
	}


}
