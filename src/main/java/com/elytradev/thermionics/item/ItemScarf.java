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

package com.elytradev.thermionics.item;

import com.elytradev.thermionics.Thermionics;

import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemScarf extends Item implements IBauble {
	public ItemScarf() {
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setRegistryName("scarf");
		this.setUnlocalizedName("thermionics.bauble.scarf");
	}
	
	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack arg0) {
		return baubles.api.BaubleType.AMULET;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab.equals(this.getCreativeTab())) {
			{ //Construct a sample scarf
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagList leftScarf = new NBTTagList();
				NBTTagList rightScarf = new NBTTagList();
				for(int i=0; i<14; i++) {
					NBTTagCompound node = new NBTTagCompound();
					int col = 0x60d9bb;
					if (i%2==1) col = 0x1b5c64;
					node.setInteger("Color", col);
					leftScarf.appendTag(node);
					rightScarf.appendTag(node.copy());
				}
				tag.setTag("LeftScarf", leftScarf);
				tag.setTag("RightScarf", rightScarf);
				ItemStack stack = new ItemStack(this, 1);
				stack.setTagCompound(tag);
				list.add(stack);
			}
		}
	}
}
