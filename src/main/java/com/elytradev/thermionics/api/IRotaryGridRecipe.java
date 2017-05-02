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
	 * Gets the amount of wrench force that must be applied for any work to be done towards this recipe
	 */
	public float getRequiredTorque();
	
	/**
	 * Gets the number of revolutions (full, 360-degree turns of an axle) required to complete this recipe
	 */
	public float getRequiredRevolutions();
	
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
