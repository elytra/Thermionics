package com.elytradev.concrete.inventory;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class Validators {
	public static final Predicate<ItemStack> ANYTHING = (it)->true;
	public static final Predicate<ItemStack> NOTHING = (it)->false;
	public static final Predicate<ItemStack> FURNACE_FUELS = TileEntityFurnace::isItemFuel; //This is actually the most correct/accurate way to read the furnace registry!
	public static final Predicate<ItemStack> SMELTABLE = (it)->{
		return !FurnaceRecipes.instance().getSmeltingResult(it).isEmpty();
	};
	public static final Predicate<ItemStack> FLUID_CONTAINERS = (it)->{
		return it.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	};
}
