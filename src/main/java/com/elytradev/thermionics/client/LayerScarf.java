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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LayerScarf implements LayerRenderer<EntityLivingBase> {
	
	protected final RenderPlayer renderPlayer;
	
	private ArrayList<Vec3d> nodes = new ArrayList<>();
	
	public LayerScarf(RenderPlayer renderer) {
		renderPlayer = renderer;
		
		for(int i=0; i<4; i++) {
			nodes.add(new Vec3d(0,0,0));
		}
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		//TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		//TextureAtlasSprite stone = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("stone");
		//Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		//GlStateManager.color(1f, 0.0f, 0f);
		//legacyCube(-4,-1,-2.5f, 8, 2, 5f, sprite, true, true, true);
		
		//GlStateManager.pushMatrix();
		//GlStateManager.rotate(TAU/8, 1, 0, 0);
		//legacyCube(0,0,0, 16, 16, 16, sprite, true, true, true);
		
		//GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	private static void test() {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();
		
		
		
		tess.draw();
	}
	
	private static final float TAU = (float)(Math.PI*2);
	private static final float PI = (float)Math.PI;
	private static final float floatDist = 0.25f;
	private static final float nodeSz = 0.25f;
	private static final float vTerm = 0.8f;
	private static final float yTerm = 0.1f;
	private static final float gravity = 0.008f;
	private static final float damping = 0.02f;
	
	private static void simulate(World world, ScarfNode prime, ArrayList<ScarfNode> nodes, float partialTicks) {
		ScarfNode lastNode = prime;
		for(ScarfNode node : nodes) {
			float sx = node.x;
			float sy = node.y;
			float sz = node.z;
			
			node.x += node.vx * partialTicks;
			node.y += node.vy * partialTicks;
			node.z += node.vz * partialTicks;
			
			if (!world.isAirBlock(new BlockPos((int)node.x, (int)node.y, (int)node.z))) {
				//kick this node upwards out of the block. Will work unless you manage to snap your scarf up into a ceiling
				node.y = ((int)node.y) + 1f;
				node.vy = 0f;
			}
			//if (node.y < entity.posY) node.y = (float)entity.posY;
			
			node.vx = dampen(node.vx, damping*partialTicks);
			node.vy = dampen(node.vy, damping*partialTicks);
			node.vz = dampen(node.vz, damping*partialTicks);
			node.vy -= gravity;
			
			node.vx = clamp(node.vx, vTerm);
			if (node.vy>vTerm) node.vy = vTerm; if (node.vy<-yTerm) node.vy = -yTerm; //Asymmetric clamp
			node.vz = clamp(node.vz, vTerm);
			
			double dist = node.distanceTo(lastNode);
			//Vec3d placement = node;
			if (dist>=floatDist) {
				Vec3d displacement = lastNode.subtract(node).normalize().scale(dist-floatDist);
				node.addToNode(displacement);
			}
			
			float frameContribX = dampen(node.x - sx, damping*0.5f);
			float frameContribZ = dampen(node.z - sz, damping*0.5f);
			if (Math.abs(frameContribX) > Math.abs(node.vx)) node.vx = frameContribX;
			if (Math.abs(frameContribZ) > Math.abs(node.vz)) node.vz = frameContribZ;
			
			
			//nextNodes.add(placement);
			lastNode = node;
		}
	}
	
	private static void draw(World world, double dx, double dy, double dz, Entity entity, ScarfNode prime, ArrayList<ScarfNode> nodes) {
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/wool_colored_white");
		//TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		ScarfNode lastNode = prime;
		for(ScarfNode node : nodes) {
			//TODO: Swap out sprites each segment?
			
			float x1 = (float)(node.x-dx);
			float x2 = (float)(lastNode.x-dx);
			float y1 = (float)(node.y-dy);
			float y2 = (float)(lastNode.y-dy);
			float z1 = (float)(node.z-dz);
			float z2 = (float)(lastNode.z-dz);
			
			float light1 = world.getLight(new BlockPos((int)node.x, (int)node.y, (int)node.z)) / 15f;
			float light2 = world.getLight(new BlockPos((int)lastNode.x, (int)lastNode.y, (int)lastNode.z)) / 15f;
			
			ribbon(x1, y1, z1, x2, y2, z2, 0.25f, sprite, node.r, node.g, node.b, light1, light2);
			ribbon(x2, y2, z2, x1, y1, z1, 0.25f, sprite, node.r, node.g, node.b, light2, light1);
			lastNode = node;
		}
	}
	
	public static void renderScarf(double dx, double dy, double dz, Entity entity, ItemStack scarfItem, Scarf scarf, float partialTicks, World world) {
		if (scarf==null || (scarf.leftScarf.isEmpty() && scarf.rightScarf.isEmpty())) return; //Nothing to simulate/render
		
		Vec3d backwards = entity.getForward().scale(-1d).scale(0.2f);
		Vec3d leftShoulderOffset = backwards.rotateYaw(-TAU/4).scale(1.5f);
		Vec3d rightShoulderOffset = backwards.rotateYaw(TAU/4).scale(1.5f);
		
		ScarfNode leftNode = new ScarfNode(entity.getPositionEyes(partialTicks).add(backwards).add(leftShoulderOffset));
		ScarfNode rightNode = new ScarfNode(entity.getPositionEyes(partialTicks).add(backwards).add(rightShoulderOffset));
		leftNode.y -= 0.50f;
		rightNode.y -= 0.50f;
		
		if (entity instanceof EntityPlayer) {
			if (((EntityPlayer)entity).getTicksElytraFlying() > 0) {
				leftNode.addToNode(entity.getForward().scale(3.0));
				rightNode.addToNode(entity.getForward().scale(3.0));
			}
		}
		
		if (!scarf.leftScarf.isEmpty())  simulate(world, leftNode,  scarf.leftScarf,  partialTicks);
		if (!scarf.rightScarf.isEmpty()) simulate(world, rightNode, scarf.rightScarf, partialTicks);
		
		if (!scarf.leftScarf.isEmpty())  draw(world, dx, dy, dz, entity, leftNode, scarf.leftScarf);
		if (!scarf.rightScarf.isEmpty()) draw(world, dx, dy, dz, entity, rightNode, scarf.rightScarf);
	}
	
	private static float dampen(final float value, final float dampen) {
		float result = value;
		if (result>0) {
			result-=dampen;
			if (result<0) result=0;
		} else if (value<0){
			result+=dampen;
			if (result>0) result=0;
		}
		return result;
	}
	
	private static float clamp(final float value, final float limit) {
		if (value>limit) return limit;
		if (value<-limit) return -limit;
		return value;
	}
	
	private static void colorCube(float x, float y, float z, float w, float h, float d, float r, float g, float b) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();

		
		//float s = 1/16f;
		GlStateManager.color(r, g, b);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);

		//Sides
		vb.pos((x+0), (y+h), (z+0)).normal(0, 0, -1).endVertex();
		vb.pos((x+w), (y+h), (z+0)).normal(0, 0, -1).endVertex();
		vb.pos((x+w), (y+0), (z+0)).normal(0, 0, -1).endVertex();
		vb.pos((x+0), (y+0), (z+0)).normal(0, 0, -1).endVertex();
		
		vb.pos((x+w), (y+h), (z+d)).normal(0, 0, 1).endVertex();
		vb.pos((x+0), (y+h), (z+d)).normal(0, 0, 1).endVertex();
		vb.pos((x+0), (y+0), (z+d)).normal(0, 0, 1).endVertex();
		vb.pos((x+w), (y+0), (z+d)).normal(0, 0, 1).endVertex();
		
		
		vb.pos((x+0), (y+0), (z+d)).normal(-1, 0, 0).endVertex();
		vb.pos((x+0), (y+h), (z+d)).normal(-1, 0, 0).endVertex();
		vb.pos((x+0), (y+h), (z+0)).normal(-1, 0, 0).endVertex();
		vb.pos((x+0), (y+0), (z+0)).normal(-1, 0, 0).endVertex();
		
		vb.pos((x+w), (y+0), (z+0)).normal(1, 0, 0).endVertex();
		vb.pos((x+w), (y+h), (z+0)).normal(1, 0, 0).endVertex();
		vb.pos((x+w), (y+h), (z+d)).normal(1, 0, 0).endVertex();
		vb.pos((x+w), (y+0), (z+d)).normal(1, 0, 0).endVertex();
		
		
		//Bottom
		vb.pos((x+0), (y+0), (z+0)).normal(0, -1, 0).endVertex();
		vb.pos((x+w), (y+0), (z+0)).normal(0, -1, 0).endVertex();
		vb.pos((x+w), (y+0), (z+d)).normal(0, -1, 0).endVertex();
		vb.pos((x+0), (y+0), (z+d)).normal(0, -1, 0).endVertex();

		//Top
		vb.pos((x+0), (y+h), (z+0)).normal(0, 1, 0).endVertex();
		vb.pos((x+0), (y+h), (z+d)).normal(0, 1, 0).endVertex();
		vb.pos((x+w), (y+h), (z+d)).normal(0, 1, 0).endVertex();
		vb.pos((x+w), (y+h), (z+0)).normal(0, 1, 0).endVertex();
			
		
		tess.draw();
		GlStateManager.color(1, 1, 1);
	}
	
	private static void ribbon(float x1, float y1, float z1, float x2, float y2, float z2, float width, TextureAtlasSprite tas, float r, float g, float b, float light1, float light2) {
		
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();
		
		float minU = tas.getInterpolatedU(4);
		float minV = tas.getInterpolatedV(4);
		float maxU = tas.getInterpolatedU(12);
		float maxV = tas.getInterpolatedV(12);
		
		//GlStateManager.color(r, g, b);
		
		//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		//if (renderSides) {
		
		vb.pos(x1, y1, z1).tex(minU, maxV).color(r*light1, g*light1, b*light1, 1.0f).endVertex();
		vb.pos(x2, y2, z2).tex(maxU, maxV).color(r*light2, g*light2, b*light2, 1.0f).endVertex();
		vb.pos(x2, y2+width, z2).tex(maxU, minV).color(r*light2, g*light2, b*light2, 1.0f).endVertex();
		vb.pos(x1, y1+width, z1).tex(minU, minV).color(r*light1, g*light1, b*light1, 1.0f).endVertex();

		tess.draw();
	}
	
	private static void renderCube(float x, float y, float z, float w, float h, float d, TextureAtlasSprite tas, boolean renderTop, boolean renderBottom, boolean renderSides) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();
		
		float minV = tas.getInterpolatedV(0);
		float maxV = tas.getInterpolatedV(16);
		
		float minU = tas.getInterpolatedU(0);
		float maxU = tas.getInterpolatedU(16);
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		if (renderSides) {
			vb.pos((x+0), (y+h), (z+0)).tex(minU, maxV).normal(0, 0, -1).endVertex();
			vb.pos((x+w), (y+h), (z+0)).tex(maxU, maxV).normal(0, 0, -1).endVertex();
			vb.pos((x+w), (y+0), (z+0)).tex(maxU, minV).normal(0, 0, -1).endVertex();
			vb.pos((x+0), (y+0), (z+0)).tex(minU, minV).normal(0, 0, -1).endVertex();
			
			vb.pos((x+w), (y+h), (z+d)).tex(maxU, maxV).normal(0, 0, 1).endVertex();
			vb.pos((x+0), (y+h), (z+d)).tex(minU, maxV).normal(0, 0, 1).endVertex();
			vb.pos((x+0), (y+0), (z+d)).tex(minU, minV).normal(0, 0, 1).endVertex();
			vb.pos((x+w), (y+0), (z+d)).tex(maxU, minV).normal(0, 0, 1).endVertex();
			
			vb.pos((x+0), (y+0), (z+d)).tex(maxU, minV).normal(-1, 0, 0).endVertex();
			vb.pos((x+0), (y+h), (z+d)).tex(maxU, maxV).normal(-1, 0, 0).endVertex();
			vb.pos((x+0), (y+h), (z+0)).tex(minU, maxV).normal(-1, 0, 0).endVertex();
			vb.pos((x+0), (y+0), (z+0)).tex(minU, minV).normal(-1, 0, 0).endVertex();
			
			vb.pos((x+w), (y+0), (z+0)).tex(minU, minV).normal(1, 0, 0).endVertex();
			vb.pos((x+w), (y+h), (z+0)).tex(minU, maxV).normal(1, 0, 0).endVertex();
			vb.pos((x+w), (y+h), (z+d)).tex(maxU, maxV).normal(1, 0, 0).endVertex();
			vb.pos((x+w), (y+0), (z+d)).tex(maxU, minV).normal(1, 0, 0).endVertex();
		}
		
		if (renderBottom) {
			vb.pos((x+0), (y+0), (z+0)).tex(minU, minV).normal(0, -1, 0).endVertex();
			vb.pos((x+w), (y+0), (z+0)).tex(minU, maxV).normal(0, -1, 0).endVertex();
			vb.pos((x+w), (y+0), (z+d)).tex(maxU, maxV).normal(0, -1, 0).endVertex();
			vb.pos((x+0), (y+0), (z+d)).tex(maxU, minV).normal(0, -1, 0).endVertex();
		}
		
		if (renderTop) {
			vb.pos((x+0), (y+h), (z+0)).tex(minU, minV).normal(0, 1, 0).endVertex();
			vb.pos((x+0), (y+h), (z+d)).tex(maxU, minV).normal(0, 1, 0).endVertex();
			vb.pos((x+w), (y+h), (z+d)).tex(maxU, maxV).normal(0, 1, 0).endVertex();
			vb.pos((x+w), (y+h), (z+0)).tex(minU, maxV).normal(0, 1, 0).endVertex();
			
		}
		tess.draw();
	}
	
	private static void legacyCube(float x, float y, float z, float w, float h, float d, TextureAtlasSprite tas, boolean renderTop, boolean renderBottom, boolean renderSides) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vb = tess.getBuffer();
		
		float minVX = tas.getInterpolatedV(x);
		float maxVX = tas.getInterpolatedV(x+w);
		float minVY = tas.getInterpolatedV(y);
		float maxVY = tas.getInterpolatedV(y+h);
		
		float minUX = tas.getInterpolatedU(x);
		float maxUX = tas.getInterpolatedU(x+w);
		float minUZ = tas.getInterpolatedU(z);
		float maxUZ = tas.getInterpolatedU(z+d);
		
		float s = 1/16f;
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		if (renderSides) {
			vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUX, maxVY).normal(0, 0, -1).endVertex();
			vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(maxUX, maxVY).normal(0, 0, -1).endVertex();
			vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(maxUX, minVY).normal(0, 0, -1).endVertex();
			vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUX, minVY).normal(0, 0, -1).endVertex();
			
			vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUX, maxVY).normal(0, 0, 1).endVertex();
			vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(minUX, maxVY).normal(0, 0, 1).endVertex();
			vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(minUX, minVY).normal(0, 0, 1).endVertex();
			vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUX, minVY).normal(0, 0, 1).endVertex();
			
			
			vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVY).normal(-1, 0, 0).endVertex();
			vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVY).normal(-1, 0, 0).endVertex();
			vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVY).normal(-1, 0, 0).endVertex();
			vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVY).normal(-1, 0, 0).endVertex();
			
			vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVY).normal(1, 0, 0).endVertex();
			vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVY).normal(1, 0, 0).endVertex();
			vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVY).normal(1, 0, 0).endVertex();
			vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVY).normal(1, 0, 0).endVertex();
		}
		
		if (renderBottom) {
			vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVX).normal(0, -1, 0).endVertex();
			vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(minUZ, maxVX).normal(0, -1, 0).endVertex();
			vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUZ, maxVX).normal(0, -1, 0).endVertex();
			vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVX).normal(0, -1, 0).endVertex();
		}
		
		if (renderTop) {
			vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUZ, minVX).normal(0, 1, 0).endVertex();
			vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(maxUZ, minVX).normal(0, 1, 0).endVertex();
			vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVX).normal(0, 1, 0).endVertex();
			vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVX).normal(0, 1, 0).endVertex();
			
		}
		tess.draw();
	}
}
