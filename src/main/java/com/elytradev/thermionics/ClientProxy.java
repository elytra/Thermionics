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
package com.elytradev.thermionics;

import com.elytradev.thermionics.client.tesr.RenderTileDrum;
import com.elytradev.thermionics.data.IPreferredRenderState;
import com.elytradev.thermionics.item.IMetaItemModel;
import com.elytradev.thermionics.item.ItemBlockEquivalentState;
import com.elytradev.thermionics.tileentity.TileEntityDrum;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends Proxy {
	@Override
	public void init() {
		//TODO: register cable and machine TESRs
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrum.class, new RenderTileDrum());
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
}
