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

package com.elytradev.thermionics.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.IRotaryRecipe;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class MachineRecipes {
	protected static Set<MashTunRecipe> mashTun = new HashSet<MashTunRecipe>();
	protected static Set<PotStillRecipe> potStill = new HashSet<PotStillRecipe>();
	protected static Set<SergerRecipe> serger = new HashSet<SergerRecipe>();
	
	public static void register(MashTunRecipe recipe) {
		mashTun.add(recipe);
	}
	
	public static void register(PotStillRecipe recipe) {
		potStill.add(recipe);
	}
	
	public static void register(SergerRecipe recipe) {
		serger.add(recipe);
	}
	
	@Nullable
	public static PotStillRecipe getPotStill(FluidTank tank) {
		for(PotStillRecipe recipe : potStill) {
			if (recipe.matches(tank)) return recipe;
		}
		
		return null;
	}
	
	@Nullable
	public static MashTunRecipe getMashTun(FluidTank tank, IItemHandler inv) {
		for(MashTunRecipe recipe : mashTun) {
			if (recipe.matches(tank, inv)) return recipe;
		}
		
		return null;
	}
	
	@Nullable
	public static SergerRecipe getSerger(IItemHandler inv) {
		for(SergerRecipe recipe : serger) {
			if (recipe.matches(inv)) return recipe;
		}
		
		return null;
	}

	public static Collection<IRotaryRecipe> allHammerMill() {
		return HammerMillRecipes.all();
	}

	public static Collection<SergerRecipe> allSerger() {
		return serger;
	}
}
