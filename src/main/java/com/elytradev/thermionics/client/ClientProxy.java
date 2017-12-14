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

import com.elytradev.thermionics.Proxy;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.Spirits;
import com.elytradev.thermionics.data.IPreferredRenderState;
import com.elytradev.thermionics.item.IMetaItemModel;
import com.elytradev.thermionics.item.ItemBlockEquivalentState;
import com.elytradev.thermionics.item.Spirit;
import com.elytradev.thermionics.item.ThermionicsItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy extends Proxy {
	
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
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (tintIndex!=1) return 0xFFFFFFFF;
				if (ThermionicsItems.SPIRIT_BOTTLE.isEmpty(stack)) return 0x00000000;
				Spirit spirit = ThermionicsItems.SPIRIT_BOTTLE.getSpirit(stack);
				if (spirit==null) return 0x70000000;
				
				return spirit.getColor();
			}
		}, ThermionicsItems.SPIRIT_BOTTLE);
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
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			FX.update(Minecraft.getMinecraft().world);
		}
	}
}
