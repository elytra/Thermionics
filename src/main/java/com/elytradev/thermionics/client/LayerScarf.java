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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
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
	
	private static final float TAU = (float)(Math.PI*2);
	//private static final float PI = (float)Math.PI;
	private static final float floatDist = 0.25f;
	private static final float vTerm = 40.0f;
	private static final float yTerm = 10.0f;
	private static final float gravity = 1.00f;
	private static final float damping = 8.00f;
	private static final float initialDamping = 0.0f;
	private static final float mass = 2.00f;
	
	private static void simulate(World world, ScarfNode prime, ArrayList<ScarfNode> nodes, float frameTime) {
		ScarfNode lastNode = prime;
		float resist = 4.0f;
		for(ScarfNode node : nodes) {

			node.x += node.vx * frameTime;
			node.y += node.vy * frameTime;
			node.z += node.vz * frameTime;
			
			//Grab start position before tugging but after inertia
			float sx = node.x;
			float sz = node.z;
			if (sx==0&&sz==0) { //both coords exactly at the origin? Unlikely!
				sx=prime.x;
				sz=prime.z;
				node.x = prime.x;
				node.z = prime.z;
			}
			
			
			BlockPos pos = new BlockPos((int)node.x, (int)node.y, (int)node.z);
			if (blocksScarves(world, pos)) {
				//kick this node upwards out of the block. Will work unless you manage to snap your scarf up into a ceiling
				node.y = ((int)node.y) + 1f;
				node.vy = 0f;
			}
			//if (node.y < entity.posY) node.y = (float)entity.posY;
			
			node.vx = dampen(node.vx, damping*frameTime);
			node.vy = dampen(node.vy, damping*frameTime);
			node.vz = dampen(node.vz, damping*frameTime);
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
			
			//Add the force we were tugged with to our inertia
			float frameX = (node.x - sx)*mass*(float)Math.random();
			float frameZ = (node.z - sz)*mass*(float)Math.random();
			node.vx = clamp(dampen(node.vx+frameX, resist), vTerm);
			node.vz = clamp(dampen(node.vz+frameZ, resist), vTerm);
			//float frameContribX = dampen(node.x - sx, damping*initialDamping) * mass;
			//float frameContribZ = dampen(clamp(node.z - sz, vTerm), damping*initialDamping) * mass;
			//if (Math.abs(frameContribX) > Math.abs(node.vx)) node.vx = frameContribX;
			//if (Math.abs(frameContribZ) > Math.abs(node.vz)) node.vz = frameContribZ;
			
			
			//nextNodes.add(placement);
			lastNode = node;
			resist -= 0.1f; if (resist<0f) resist=0f;
		}
	}
	
	private static boolean blocksScarves(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) return false;
		IBlockState state = world.getBlockState(pos);
		return state.causesSuffocation();
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
		
		float frameTime = PartialTickTime.getFrameTime();
		
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
		
		if (!scarf.leftScarf.isEmpty())  simulate(world, leftNode,  scarf.leftScarf,  frameTime);
		if (!scarf.rightScarf.isEmpty()) simulate(world, rightNode, scarf.rightScarf, frameTime);
		
		if (!scarf.leftScarf.isEmpty())  draw(world, dx, dy, dz, entity, leftNode, scarf.leftScarf);
		if (!scarf.rightScarf.isEmpty()) draw(world, dx, dy, dz, entity, rightNode, scarf.rightScarf);
		
		PartialTickTime.endFrame();
	}
	
	private static float dampen(final float value, final float dampen) {
		float result = value;
		if (result>0) {
			result-=dampen;
			if (result<0) result=0;
		} else if (result<0){
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
	
}
