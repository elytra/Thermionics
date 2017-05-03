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
