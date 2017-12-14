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
