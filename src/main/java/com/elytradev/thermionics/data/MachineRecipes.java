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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.elytradev.concrete.recipe.IIngredient;
import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.ItemStackIngredient;
import com.elytradev.concrete.recipe.impl.OreItemIngredient;
import com.elytradev.concrete.recipe.impl.ShapedInventoryRecipe;
import com.elytradev.concrete.recipe.impl.ShapelessInventoryRecipe;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.IRotaryRecipe;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

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
	
	
	public static ItemStack itemStackFromString(String str) {
		if (OreDictionary.doesOreNameExist(str)) {
			NonNullList<ItemStack> ores = OreDictionary.getOres(str);
			if (ores.size()==0) return ItemStack.EMPTY;
			return ores.get(0).copy();
		} else {
			Item item = Item.getByNameOrId(str);
			if (item!=null) {
				return new ItemStack(item);
			} else {
				return ItemStack.EMPTY;
			}
		}
	}
	
	public static ItemIngredient ingredientFromString(String str) {
		if (OreDictionary.doesOreNameExist(str)) {
			return new OreItemIngredient(str, 1);
		} else {
			Item item = Item.getByNameOrId(str);
			if (item!=null) {
				return new ItemStackIngredient(new ItemStack(item));
			} else {
				return null;
			}
		}
	}
	
	public static ItemIngredient ingredientFromObject(JsonObject obj) {
		int meta = OreDictionary.WILDCARD_VALUE;
		if (obj.containsKey("meta")) {
			String metaString = obj.get(String.class, "meta");
			if (metaString!=null && metaString.equals("*")) {
				meta = OreDictionary.WILDCARD_VALUE;
			} else {
				Integer metaInt = obj.get(Integer.class, "meta");
				if (metaInt!=null) {
					meta = metaInt;
				}
			}
		}
		
		if (obj.containsKey("item")) {
			ItemStack stack = itemStackFromString(obj.get(String.class, "item"));
			if (stack.isEmpty()) {
				//System.out.println("Can't get stack for "+obj.get(String.class, "item"));
				return null;
			}
			stack.setItemDamage(meta);
			if (obj.containsKey("count")) {
				Integer count = obj.get(Integer.class, "count");
				if (count!=null) {
					stack.setCount(count);
				}
			}
			if (obj.containsKey("ignore_nbt")) {
				Boolean ignoreNbt = obj.get(Boolean.class, "ignore_nbt");
				if (ignoreNbt!=null && ignoreNbt) {
					return new WildcardNBTIngredient(stack);
				}
			}
			return new ItemStackIngredient(stack);
		} else if (obj.containsKey("tag")) {
			//Tags don't exist yet; treat as oredict
			String tag = obj.get(String.class, "tag");
			if (OreDictionary.doesOreNameExist(tag)) {
				int count = 1;
				if (obj.containsKey("count")) {
					Integer countObj = obj.get(Integer.class, "count");
					if (countObj!=null) count = countObj;
				}
				
				return new OreItemIngredient(tag, count);
			} else {
				
				return null;
			}
		}
		return null;
	}
	
	public static ItemStack itemStackFromJson(JsonElement elem) {
		if (elem instanceof JsonPrimitive) {
			String resultString = ((JsonPrimitive) elem).asString();
			return itemStackFromString(resultString);
		} else if (elem instanceof JsonObject) {
			JsonObject obj = (JsonObject)elem;
			if (obj.containsKey("item")) {
				ItemStack stack = itemStackFromString(obj.get(String.class, "item"));
				if (stack.isEmpty()) return ItemStack.EMPTY;
				if (obj.containsKey("count")) {
					Integer count = obj.get(Integer.class, "count");
					if (count!=null) {
						stack.setCount(count);
					}
				}
				if (obj.containsKey("meta")) {
					String metaString = obj.get(String.class, "meta");
					if (metaString!=null && metaString.equals("*")) {
						stack.setItemDamage(OreDictionary.WILDCARD_VALUE);
					} else {
						Integer meta = obj.get(Integer.class, "meta");
						if (meta!=null) {
							stack.setItemDamage(meta);
						}
					}
				}
				
				return stack;
			} else if (obj.containsKey("tag")) {
				//Tags don't exist yet; treat as oredict
				NonNullList<ItemStack> ores = OreDictionary.getOres(obj.get(String.class, "tag"));
				if (ores.size()==0) return ItemStack.EMPTY;
				ItemStack stack = ores.get(0).copy();
				
				if (obj.containsKey("count")) {
					Integer count = obj.get(Integer.class, "count");
					if (count!=null) {
						stack.setCount(count);
					}
				}
				if (obj.containsKey("meta")) {
					String metaString = obj.get(String.class, "meta");
					if (metaString!=null && metaString.equals("*")) {
						stack.setItemDamage(OreDictionary.WILDCARD_VALUE);
					} else {
						Integer meta = obj.get(Integer.class, "meta");
						if (meta!=null) {
							stack.setItemDamage(meta);
						}
					}
				}
				
				return stack;
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	public static InspectableShapedInventoryRecipe shapedFromJson(JsonElement elem, String recipeName, int requiredWidth, int requiredHeight) {
		if (!(elem instanceof JsonObject)) return null;
		JsonObject json = (JsonObject)elem;
		//Integer width = json.get(Integer.class, "width"); if (width==null) width = 3;
		//Integer height = json.get(Integer.class, "height"); if (height==null) height = 3;
		
		ItemStack result = itemStackFromJson(json.get("result"));
		if (result==null || result.isEmpty()) {
			Thermionics.LOG.warn("Problem building result for recipe \""+recipeName+"\": Can't understand item \""+json.get("result").toJson(false, false)+"\"");
			return null;
		}
		
		JsonElement patternElem = json.get("pattern");
		if (!(patternElem instanceof JsonArray)) {
			Thermionics.LOG.warn("Problem building pattern for recipe \""+recipeName+"\": 'pattern' must be an array of strings.");
			return null;
		}
		//Get dimensions of pattern
		JsonArray pattern = (JsonArray)patternElem;
		int height = pattern.size();
		int width = 0;
		for(JsonElement patternItem : pattern) {
			if (patternItem instanceof JsonPrimitive) {
				width = Math.max(width, ((JsonPrimitive)patternItem).asString().length());
			} else {
				Thermionics.LOG.warn("Problem building pattern for recipe \""+recipeName+"\": non-string element found in 'pattern' array.");
				return null;
			}
		}
		if (width==0 || height==0) {
			Thermionics.LOG.warn("Problem building pattern for recipe \""+recipeName+"\": pattern was empty!");
			return null;
		}
		if (width>requiredWidth || height>requiredHeight) {
			Thermionics.LOG.warn("Problem building pattern for recipe \""+recipeName+"\": pattern was "+width+"x"+height+" which was too big to fit in "+requiredWidth+"x"+requiredHeight+"!");
			return null;
		}
		
		//Build key
		HashMap<String, ItemIngredient> key = new HashMap<>();
		JsonElement keyElem = json.get("key");
		if (!(keyElem instanceof JsonObject)) {
			Thermionics.LOG.warn("Problem building pattern for recipe \""+recipeName+"\": 'key' must be an object ({}).");
			return null;
		}
		for(Map.Entry<String, JsonElement> entry : ((JsonObject) keyElem).entrySet()) {
			String entryKey = entry.getKey();
			if (entryKey.isEmpty()) {
				Thermionics.LOG.warn("Problem building pattern-key for recipe \""+recipeName+"\": blank keys are invalid!");
				return null;
			} else if (entryKey.length()>1) {
				Thermionics.LOG.warn("Non-fatal problem building pattern-key for recipe \""+recipeName+"\": Key \""+entryKey+"\" is longer than one character; subsequent characters will be ignored!");
			}
			
			JsonElement jsonIngredient = entry.getValue();
			if (jsonIngredient instanceof JsonPrimitive) {
				ItemIngredient ingredient = ingredientFromString(((JsonPrimitive)jsonIngredient).asString());
				if (ingredient!=null) {
					key.put(entryKey.substring(0, 1), ingredient);
				} else {
					Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": No item found for identifier \""+((JsonPrimitive)jsonIngredient).asString()+"\".");
					return null;
				}
			} else if (jsonIngredient instanceof JsonObject) {
				ItemIngredient ingredient = ingredientFromObject((JsonObject)jsonIngredient);
				if (ingredient==null) {
					Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": No item found for object "+jsonIngredient.toJson(false, false));
					return null;
				}
				key.put(entryKey.substring(0, 1), ingredient);
			} else {
				Thermionics.LOG.warn("There was a problem building pattern-key for recipe \""+recipeName+"\": Was looking for String or Object, but found \""+elem.toJson(false, false)+"\"");
				return null;
			}
		}
		
		//Interpret pattern
		ItemIngredient[] plan = new ItemIngredient[width*height];
		int index = 0;
		for(int y=0; y<height; y++) {
			String row = ((JsonPrimitive)pattern.get(y)).asString();
			for(int x=0; x<width; x++) {
				if (row.length()<=x) {
					plan[index] = null;
				} else {
					String cellKey = ""+row.charAt(x);
					if (cellKey.equals(" ")) {
						plan[index] = null;
					} else {
						ItemIngredient ingredient = key.get(cellKey);
						if (ingredient==null) {
							Thermionics.LOG.warn("There was a problem interpreting the pattern for recipe \""+recipeName+"\": Couldn't find a key for character \""+cellKey+"\"");
							return null;
						}
						plan[index] = ingredient;
					}
				}
				
				index++;
			}
		}
		//System.out.println("Built "+width+"x"+height+" pattern out of json "+elem.toJson(false, false));
		boolean flippable = false;
		if (json.containsKey("flippable")) {
			Boolean flip = json.get(Boolean.class, "flippable");
			if (flip!=null) flippable = flip;
		}
		
		return new InspectableShapedInventoryRecipe(result, requiredWidth, requiredHeight, width, height, flippable, plan);
	}
	
	public static ShapelessInventoryRecipe shapelessFromJson(JsonElement elem, String recipeName) {
		if (!(elem instanceof JsonObject)) return null;
		JsonObject json = (JsonObject)elem;
		
		ItemStack result = itemStackFromJson(json.get("result"));
		if (result==null || result.isEmpty()) {
			Thermionics.LOG.warn("Problem building result for recipe \""+recipeName+"\": Can't understand item \""+json.get("result").toJson(false, false)+"\"");
			return null;
		}
		
		ArrayList<ItemIngredient> ingredients = new ArrayList<>();
		JsonElement jsonIngredients = json.get("ingredients");
		if (jsonIngredients==null) {
			Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": 'ingredients' must be present and non-null in a shapeless recipe.");
			return null;
		}
		if (!(jsonIngredients instanceof JsonArray)) {
			Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": 'ingredients' must be an array ([])");
			return null;
		}
				
		for(JsonElement jsonIngredient : (JsonArray)jsonIngredients) {
			
			
			if (jsonIngredient instanceof JsonPrimitive) {
				ItemIngredient ingredient = ingredientFromString(((JsonPrimitive)jsonIngredient).asString());
				if (ingredient!=null) {
					ingredients.add(ingredient);
				} else {
					Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": No item found for identifier \""+((JsonPrimitive)jsonIngredient).asString()+"\".");
					return null;
				}
			} else if (jsonIngredient instanceof JsonObject) {
				ItemIngredient ingredient = ingredientFromObject((JsonObject)jsonIngredient);
				if (ingredient!=null) {
					Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": No item found for object "+jsonIngredient.toJson(false, false));
					return null;
				}
				ingredients.add(ingredient);
			} else {
				Thermionics.LOG.warn("There was a problem building ingredients for recipe \""+recipeName+"\": Was looking for String or Object, but found \""+elem.toJson(false, false)+"\"");
				return null;
			}
		}
		
		ShapelessInventoryRecipe recipe = new ShapelessInventoryRecipe(result, ingredients.toArray(new ItemIngredient[ingredients.size()]));
		return recipe;
	}
}
