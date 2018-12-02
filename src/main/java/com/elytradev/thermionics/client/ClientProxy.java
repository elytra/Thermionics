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

import java.util.concurrent.TimeUnit;

import com.elytradev.thermionics.Proxy;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.IPreferredRenderState;
import com.elytradev.thermionics.item.IMetaItemModel;
import com.elytradev.thermionics.item.ItemBlockEquivalentState;
import com.elytradev.thermionics.item.Spirit;
import com.elytradev.thermionics.item.ThermionicsItems;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy extends Proxy {
	
	//private final Accessor<Map<String, RenderPlayer>> skinMap = Accessors.findField(RenderManager.class, "field_178636_l", "skinMap");
	
	@Override
	public void preInit() {
		OBJLoader.INSTANCE.addDomain(Thermionics.MODID);
	}
	
	
	@Override
	public void init() {
	}
	
	@Override
	public void registerItemModel(Item item) {
		ResourceLocation loc = Item.REGISTRY.getNameForObject(item);
		NonNullList<ItemStack> variantList = NonNullList.create();
		item.getSubItems(Thermionics.TAB_THERMIONICS, variantList);
		if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IPreferredRenderState) {
			String state = ((IPreferredRenderState)((ItemBlock)item).getBlock()).getPreferredRenderState();
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, state));
		} else if (item instanceof ItemBlockEquivalentState) {
			ItemBlockEquivalentState itemBlock = (ItemBlockEquivalentState)item;
			for(ItemStack stack : variantList) {
				String state = itemBlock.getStateStringForItem(stack);
				ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(loc, state));
			}
		} else if (item instanceof ItemTool) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, "inventory"));
		} else if (item instanceof IMetaItemModel) {
			String[] models = ((IMetaItemModel) item).getModelLocations();
			for(int i=0; i<models.length; i++) {
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(Thermionics.MODID, models[i]), "inventory"));
			}
		} else {
			if (variantList.size()==1) {
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(loc, "inventory"));
			} else {
				for(ItemStack subItem : variantList) {
					ModelLoader.setCustomModelResourceLocation(item, subItem.getItemDamage(), new ModelResourceLocation(loc, "variant="+subItem.getItemDamage()));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRegisterModel(ModelRegistryEvent event) {
		for(Item i : Thermionics.instance().needModelRegistration) {
			registerItemModel(i);
		}
	}
	
	public void postInit() {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (tintIndex!=1) return 0xFFFFFF;
				//if (ThermionicsItems.SPIRIT_BOTTLE.isEmpty(stack)) return 0x00000000;
				Spirit spirit = ThermionicsItems.SPIRIT_BOTTLE.getSpirit(stack);
				if (spirit==null) return 0xFFFFFF;
				
				return spirit.getColor();
			}
		}, ThermionicsItems.SPIRIT_BOTTLE);
		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (stack==null || stack.isEmpty()) return 0xFFFFFF;
				NBTTagCompound tag = stack.getTagCompound();
				if (tag==null || !tag.hasKey("Color")) return 0xFFFFFF;
				
				return tag.getInteger("Color");
			}
		}, ThermionicsItems.FABRIC_SQUARE);
		/*
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		Map<String, RenderPlayer> renders = skinMap.get(manager);
		for (Map.Entry<String, RenderPlayer> en : renders.entrySet()) {
			en.getValue().addLayer(new LayerScarf(en.getValue()));
		}*/
	}
	
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/clear_hootch"));
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/medium_hootch"));
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/dark_hootch"));
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/clear_spirit"));
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/medium_spirit"));
		event.getMap().registerSprite(new ResourceLocation("thermionics", "fluids/dark_spirit"));
	}
	
	private float swayTheta = 0f;
	private float swaySpeed = 0.05f;
	private float swayAmplitudePerTipsy = 0.1f;
	@SubscribeEvent
	public void onClientTick(TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player==null) return; //We're not in-game
		
		if (event.phase == TickEvent.Phase.START) {
			FX.update(Minecraft.getMinecraft().world);
			
			
			if (player.isPotionActive(Thermionics.POTION_TIPSY)) {
				swayTheta += swaySpeed * event.renderTickTime;
				
				int magnitude = player.getActivePotionEffect(Thermionics.POTION_TIPSY).getAmplifier();
				if (magnitude>10) {
					//TODO: Extra-magnitude effects. My idea of inducing nausea was vetoed by people clearly unfamiliar with the effects of ten forties of vodka.
					
				}
				
				if (player.movementInput.forwardKeyDown) {
					//Modulate the player direction
					float swayBase = MathHelper.cos(swayTheta);
					float sway = swayAmplitudePerTipsy * player.getActivePotionEffect(Thermionics.POTION_TIPSY).getAmplifier() * swayBase;
					player.rotationYaw += sway;
				}
			}
			
			if (player.isPotionActive(Thermionics.POTION_EFFORTLESS_SPEED)) {
				System.out.println(player.motionY);
				
				
				if (player.motionY < 0 && player.motionY > -0.08) { //Air has no sliding friction, so let's not have jumps send us rocketing forwards faster than running.
					int magnitude = player.getActivePotionEffect(Thermionics.POTION_EFFORTLESS_SPEED).getAmplifier();
					
					if (player.movementInput.forwardKeyDown) {
						Vec3d motionXZ = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw).scale(0.05f * (magnitude+1));
						player.motionX += motionXZ.x;
						player.motionZ += motionXZ.z;
						
					}
				}
			}
		}
	}
	
	private double softCap(double existing, double cap, double add) {
		if (cap<0) cap = -cap;
		
		if (existing<cap && existing>-cap) return hardCap(existing, cap, add);
		
		
		if (existing>0) { //existing > cap
			if (add>0) {
				return existing; //can't add past the cap
			} else {
				double result = existing+add;
				if (result<-cap) {
					//We snapped out past the other boundary
					return -cap;
				} else {
					//We're either in-bounds, or closer to in-bounds than we were before.
					return result;
				}
			}
		} else { // existing < -cap
			if (add<0) {
				return existing; //can't add past the cap
			} else {
				double result = existing+add;
				if (result>cap) {
					//We snapped out past the other boundary
					return cap;
				} else {
					//We're either in-bounds, or closer to in-bounds than we were before.
					return result;
				}
			}
		}
	}
	
	private double hardCap(double existing, double cap, double add) {
		double result = existing+add;
		if (result>cap) return cap;
		if (result<-cap) return -cap;
		return result;
	}
	
	private Cache<Entity, Scarf> scarfCache = CacheBuilder.newBuilder()
			.weakKeys()
			.concurrencyLevel(1)
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.build();
	
	private static final int BAUBLE_AMULET = 0;
	//private static final int BAUBLE_RING1 = 1;
	//private static final int BAUBLE_RING2 = 2;
	//private static final int BAUBLE_BELT = 3;
	//private static final int BAUBLE_CROWN = 4;
	//private static final int BAUBLE_BODY = 5;
	//private static final int BAUBLE_CHARM = 6;
	
	@SubscribeEvent
	public void onPostRender(RenderWorldLastEvent evt) {
		if (!Loader.isModLoaded("baubles")) return;
		
		EntityPlayer thePlayer = Minecraft.getMinecraft().player;
		
		for(EntityPlayer entity : Minecraft.getMinecraft().world.playerEntities) {
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entity);
			ItemStack scarfStack = baubles.getStackInSlot(BAUBLE_AMULET);
			if (scarfStack==null || scarfStack.isEmpty() || scarfStack.getItem()!=ThermionicsItems.SCARF) return;
			Scarf scarf = scarfCache.getIfPresent(entity);
			if (scarf==null) {
				scarf = new Scarf();
				scarfCache.put(entity, scarf);
				scarf.readFromNBT(scarfStack.getTagCompound());
				//System.out.println("Created scarf:"+(scarf.leftScarf.size()+scarf.rightScarf.size())+" nodes.");
			} else {
				
				scarf.updateFromNBT(scarfStack.getTagCompound());
				//System.out.println("Updated scarf:"+(scarf.leftScarf.size()+scarf.rightScarf.size())+" nodes.");
			}
			
			double dx = thePlayer.prevPosX + (thePlayer.posX - thePlayer.prevPosX) * evt.getPartialTicks();
			double dy = thePlayer.prevPosY + (thePlayer.posY - thePlayer.prevPosY) * evt.getPartialTicks();
			double dz = thePlayer.prevPosZ + (thePlayer.posZ - thePlayer.prevPosZ) * evt.getPartialTicks();
			
			LayerScarf.renderScarf(
					dx, dy, dz,
					entity, null, scarf, evt.getPartialTicks(),
					Minecraft.getMinecraft().world);
		}
	}
}
