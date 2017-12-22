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

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Scarf {
	ArrayList<ScarfNode> leftScarf  = new ArrayList<>();
	ArrayList<ScarfNode> rightScarf = new ArrayList<>();
	
	public Scarf() {}
	
	public void readFromNBT(NBTTagCompound tag) {
		leftScarf.clear();
		rightScarf.clear();
		if (tag==null) return;
		if (tag.hasKey("LeftScarf")) {
			unpackNodes(tag.getTagList("LeftScarf", 10), leftScarf, null);
		}
		
		if (tag.hasKey("RightScarf")) {
			unpackNodes(tag.getTagList("RightScarf", 10), rightScarf, null);
		}
	}
	
	public void updateFromNBT(NBTTagCompound tag) { 
		if (tag==null) {
			leftScarf.clear();
			rightScarf.clear();
			return;
		}
		
		ArrayList<ScarfNode> newLeftScarf = new ArrayList<ScarfNode>();
		ArrayList<ScarfNode> newRightScarf = new ArrayList<ScarfNode>();
		if (tag.hasKey("LeftScarf")) {
			unpackNodes(tag.getTagList("LeftScarf", 10), newLeftScarf, leftScarf);
		}
		
		if (tag.hasKey("RightScarf")) {
			unpackNodes(tag.getTagList("RightScarf", 10), newRightScarf, rightScarf);
		}
		leftScarf = newLeftScarf;
		rightScarf = newRightScarf;
	}
	
	private void unpackNodes(NBTTagList list, ArrayList<ScarfNode> scarf, ArrayList<ScarfNode> existing) {
		if (list.hasNoTags()) return;
		for(NBTBase nbt : list) {
			if (!(nbt instanceof NBTTagCompound)) break;
			ScarfNode node  = unpackSquare((NBTTagCompound)nbt);
			if (existing!=null && !existing.isEmpty()) {
				ScarfNode prior = existing.remove(0);
				node.inheritMotion(prior);
			}
			
			scarf.add(node);
		}
	}
	
	private ScarfNode unpackSquare(NBTTagCompound tag) {
		ScarfNode node = new ScarfNode();
		int col = tag.getInteger("Color");
		node.r = ((col >> 16)&0xFF) / 255f;
		node.g = ((col >>  8)&0xFF) / 255f;
		node.b = ((col >>  0)&0xFF) / 255f;
		
		return node;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagCompound result = (tag==null) ? new NBTTagCompound() : tag;
		NBTTagList leftList = new NBTTagList();
		packNodes(leftList, leftScarf);
		tag.setTag("LeftScarf", leftList);
		
		NBTTagList rightList = new NBTTagList();
		packNodes(rightList, rightScarf);
		tag.setTag("RightScarf", rightList);
		
		return result;
	}
	
	private void packNodes(NBTTagList list, ArrayList<ScarfNode> scarf) {
		for(ScarfNode node : scarf) {
			list.appendTag(packSquare(node));
		}
	}
	
	private NBTTagCompound packSquare(ScarfNode node) {
		NBTTagCompound tag = new NBTTagCompound();
		int col = ((int)(node.r*255)) << 16;
		col |=    ((int)(node.g*255)) <<  8;
		col |=    ((int)(node.b*255)) <<  0;
		tag.setInteger("Color", col);
		return tag;
	}
}
