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

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class ItemBlockEquivalentState extends ItemBlock {

	public ItemBlockEquivalentState(Block block) {
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setHasSubtypes(true);
	}

	@SuppressWarnings("deprecation")
	public IBlockState getStateForItem(ItemStack item) {
		return block.getStateFromMeta(item.getItemDamage());
	}
	
	public String getStateStringForItem(ItemStack item) {
		return toVanilla(getStateForItem(item));
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
	
	public static String toVanilla(IBlockState state) {
		StringBuilder result = new StringBuilder();
		Set<Entry<IProperty<?>, Comparable<?>>> entries = state.getProperties().entrySet();
		Iterator<Entry<IProperty<?>, Comparable<?>>> iter = entries.iterator();
		while(iter.hasNext()) {
			Entry<IProperty<?>, Comparable<?>> entry = iter.next();
			result.append(entry.getKey().getName());
			result.append('=');
			Comparable<?> value = entry.getValue();
			if (value instanceof IStringSerializable) {
				result.append(((IStringSerializable)value).getName());
			} else {
				result.append(entry.getValue().toString());
			}
			
			if (iter.hasNext()) result.append(',');
		}
		
		return result.toString();
	}
}
