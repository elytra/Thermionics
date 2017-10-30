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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.elytradev.thermionics.api.IRotaryGridRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RotaryGridRecipe implements IRotaryGridRecipe {
	private Object[] ingredients = new Object[9];
	private ItemStack result;
	private float torque = 8;
	private float revolutions = 300;
	
	/** Internal method for assembling most important parts of the recipe */
	@SuppressWarnings("unused")
	private RotaryGridRecipe(ItemStack result, float torque, float revolutions) {
		this.result = result;
		this.torque = torque;
		this.revolutions = revolutions;
	}
	
	public RotaryGridRecipe(ItemStack result, float torque, float revolutions, Object... ingredients) {
		HashMap<Character, Object> ingredientList = new HashMap<>();
		ArrayList<String> arrangement = new ArrayList<>();
		
		@Nullable Character lastKey = null;
		for(Object o : ingredients) {
			if (o instanceof String) {
				if (lastKey!=null) {
					//any tuple<Character, String> found in the sequence is an ingredient identifier
					ingredientList.put(lastKey, o);
				} else {
					//any non-Character-prefixed String is a recipe line identifier
					arrangement.add((String) o);
				}
				lastKey = null;
			} else if (o instanceof Character) {
				Validate.isTrue(lastKey==null, "Orphaned character key '%1$c' in recipe!", Optional.ofNullable(lastKey));
				lastKey = (Character)o;
			} else if (o instanceof ItemStack) {
				Validate.notNull(lastKey, "ItemStack '%1$s' in recipe must be prefixed with a Character key!", o);
				ingredientList.put(lastKey, o);
				lastKey = null;
			}
		}
		int arrangementHeight = Math.min(3, arrangement.size());
		for(int y=0; y<arrangementHeight; y++) {
			for(int x=0; x<Math.min(3, arrangement.get(y).length()); x++) {
				char ch = arrangement.get(y).charAt(x);
				@Nullable Object ingredient = ingredientList.get(ch);
				//System.out.println("Storing "+ingredient+" for character '"+ch+"' at location "+x+","+y);
				this.ingredients[y*3+x] = ingredient;
			}
		}
		
		this.result = result;
		this.torque = torque;
		this.revolutions = revolutions;
		
		this.explanation = "{result:\""+result+"\" recipe:\"";
		for(int i=0; i<arrangement.size(); i++) {
			explanation+=arrangement.get(i);
			if (i!=arrangement.size()-1) explanation += " | ";
		}
		this.explanation+="\"}";
	}
	
	//public static RotaryGridRecipe parse(String recipe) {
		
	//}
	
	/* WORK IN PROGRESS: Purely String-based oredict recipes
	public static RotaryGridRecipe parse(ItemStack result, float torque, float revolutions, String ingredients) {
		RotaryGridRecipe recipe = new RotaryGridRecipe(result, torque, revolutions);
		
		for(String line : ingredients.split("|")) {
			int index = 0;
			for(String elem : line.trim().split("\\s")) {
				if (index>=3) break;
			}
		}
		
		return recipe;
	}*/
	
	@Override
	public float getRequiredTorque() { return torque; }
	@Override
	public float getRequiredRevolutions() { return revolutions; }
	
	@Override
	public boolean matches(IInventory inv) {
		if (inv.getSizeInventory()<9) return false;
		for(int i=0; i<9; i++) {
			if (ingredients[i]==null) {
				if (!inv.getStackInSlot(i).isEmpty()) return false;
			} else {
				if (!OreItems.matches(ingredients[i], inv.getStackInSlot(i))) return false;
			}
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
			if (!OreItems.matches(ingredients[i], inv.getStackInSlot(i))) return false;
			//if (ingredients[i]==null) {
			//	if (!inv.getStackInSlot(i).isEmpty()) return false;
			//} else {
			//	if (!OreItems.matches(ingredients[i], inv.getStackInSlot(i))) return false;
			//}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(IItemHandler inv) {
		return result;
	}

	@Override
	public ItemStack performCraft(IItemHandler inv) {
		
		for(int i=0; i<9; i++) {
			if (ingredients[i]!=null) {
				if (ingredients[i] instanceof ItemStack) {
					inv.extractItem(i, ((ItemStack)ingredients[i]).getCount(), false);
				} else {
					inv.extractItem(i, 1, false);
				}
			}
		}
		
		return result.copy();
	}
	
	private String explanation = null;
	public String toString() {
		if (explanation==null) {
			explanation = "{result:"+result.toString()+"}";
		}
		
		return explanation;
	}
}
