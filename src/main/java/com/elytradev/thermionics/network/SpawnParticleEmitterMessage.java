package com.elytradev.thermionics.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.client.FX;
import com.elytradev.thermionics.data.EnumWeaponSkill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class SpawnParticleEmitterMessage extends Message {
	private EnumWeaponSkill id = EnumWeaponSkill.SMAAAAAAASH;
	private float x = 0;
	private float y = 0;
	private float z = 0;
	
	public SpawnParticleEmitterMessage() {
		super(Thermionics.CONTEXT);
	}
	
	public SpawnParticleEmitterMessage(NetworkContext ctx, EnumWeaponSkill skill, Entity entity) {
		this(ctx, skill, (float)entity.posX, (float)entity.posY+entity.getEyeHeight(), (float)entity.posZ);
	}
	
	public SpawnParticleEmitterMessage(NetworkContext ctx, EnumWeaponSkill skill, float x, float y, float z) {
		super(ctx);
		this.id = skill;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		FX.handle(this);
	}
	
	public EnumWeaponSkill getSkill() { return id; }
	public float getX() { return x; }
	public float getY() { return y; }
	public float getZ() { return z; }
}
