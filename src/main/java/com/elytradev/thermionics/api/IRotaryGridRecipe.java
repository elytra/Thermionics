package com.elytradev.thermionics.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Represents a recipe, which uses a 3x3 crafting grid, performed inside a machine that uses rotary power.
 * 
 * <p>GUI access is done through IInventory. Methods which accept an IInventory MUST NOT alter the inventory in any way.
 * 
 * <p>Internal access is done through IItemHandler. Methods which accept an IItemHandler MAY alter the inventory to
 * perform the crafting operation.
 * 
 * <p>The crafting grid is assumed to be the first 9 slots of the provided inventory in both access methods.
 */
public interface IRotaryGridRecipe {
	
	/**
	 * Returns true if the items in the grid will trigger crafting under this recipe. Read-only.
	 */
	public boolean matches(IInventory inv);
	
	/**
	 * Gets the crafting result that the input items will produce. Read-only.
	 */
	ItemStack getCraftingResult(IInventory inv);
	
	/**
	 * Returns true if the items in the grid will trigger crafting under this recipe. Read-only.
	 */
	public boolean matches(IItemHandler inv);
	
	/**
	 * Gets the crafting result that the input items will produce. Read-only.
	 */
	ItemStack getCraftingResult(IItemHandler inv);
	
	/**
	 * Decrement the stacks in the inventory according to the recipe. Will change inventory contents.
	 */
	public ItemStack performCraft(IItemHandler inv);
}
