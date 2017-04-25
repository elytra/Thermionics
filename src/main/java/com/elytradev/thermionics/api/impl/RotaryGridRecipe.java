package com.elytradev.thermionics.api.impl;

import com.elytradev.thermionics.api.IRotaryGridRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class RotaryGridRecipe implements IRotaryGridRecipe {
	private ItemStack[] ingredients = new ItemStack[9];
	private ItemStack result;
	
	@Override
	public boolean matches(IInventory inv) {
		if (inv.getSizeInventory()<9) return false;
		for(int i=0; i<9; i++) {
			if (!OreDictionary.itemMatches(ingredients[i], inv.getStackInSlot(i), false)) return false;
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return result;
	}

	@Override
	public boolean matches(IItemHandler inv) {
		if (inv.getSlots()<9) return false;
		for(int i=0; i<9; i++) {
			if (ingredients[i]==null || ingredients[i].isEmpty()) {
				if (!inv.getStackInSlot(i).isEmpty()) return false;
			} else {
				if (!OreDictionary.itemMatches(ingredients[i], inv.getStackInSlot(i), false)) return false;
				if (ingredients[i].getCount()>inv.getStackInSlot(i).getCount()) return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(IItemHandler inv) {
		return result;
	}

	@Override
	public ItemStack performCraft(IItemHandler inv) {
		
		for(int i=0; i<inv.getSlots(); i++) {
			if (ingredients[i]!=null && !ingredients[i].isEmpty()) {
				inv.extractItem(i, ingredients[i].getCount(), false);
			}
		}
		
		return result;
	}

}
