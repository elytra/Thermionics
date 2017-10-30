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

package com.elytradev.thermionics.api.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class OreItems {
	public static final boolean matches(Object template, ItemStack item) {
		boolean templatePresent = template!=null;
		boolean itemPresent     = item!=null     && !item.isEmpty();
		if (!templatePresent &&  itemPresent) return false; // Empty    !=  NonEmpty
		if ( templatePresent && !itemPresent) return false; // NonEmpty !=  Empty
		if (!templatePresent && !itemPresent) return true;  // Empty    ==  Empty
		
		if (template instanceof ItemStack) {
			return matches((ItemStack)template, item);
		} else if (template instanceof String) {
			return matches((String)template, item);
		}
		return false;
	}
	
	public static final boolean matches(ItemStack template, ItemStack item) {
		boolean templatePresent = template!=null && !template.isEmpty();
		boolean itemPresent     = item!=null     && !item.isEmpty();
		if (!templatePresent &&  itemPresent) return false; // Empty    !=  NonEmpty
		if ( templatePresent && !itemPresent) return false; // NonEmpty !=  Empty
		if (!templatePresent && !itemPresent) return true;  // Empty    ==  Empty
		
		if (template.getItemDamage()==OreDictionary.WILDCARD_VALUE) {
			return ItemStack.areItemsEqualIgnoreDurability(template, item) &&
					ItemStack.areItemStackTagsEqual(template, item);
		} else {
			return ItemStack.areItemsEqual(template, item) &&
					ItemStack.areItemStackTagsEqual(template, item);
		}
	}
	
	public static final boolean matches(String template, ItemStack item) {
		boolean templatePresent = template!=null && !template.isEmpty();
		boolean itemPresent     = item!=null     && !item.isEmpty();
		if (!templatePresent &&  itemPresent) return false; // Empty    !=  NonEmpty
		if ( templatePresent && !itemPresent) return false; // NonEmpty !=  Empty
		if (!templatePresent && !itemPresent) return true;  // Empty    ==  Empty
		
		if (!OreDictionary.doesOreNameExist(template)) return false;
		NonNullList<ItemStack> ores = OreDictionary.getOres(template);
		return OreDictionary.containsMatch(false, ores, item);
	}
}
