package com.elytradev.thermionics.item;

import java.util.ArrayList;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/** Mostly used for inert ingredients */
public class ItemSubtyped<T extends Enum<T>> extends Item implements IMetaItemModel {
	
	private Enum<T>[] enumValues;
	private String id;
	private boolean glowing;
	
	public ItemSubtyped(String id, T[] subtypes, boolean glowing) {
		this.setRegistryName(id);
		this.setUnlocalizedName("thermionics."+id);
		this.id = id;
		enumValues = subtypes;
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.glowing = glowing;
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage() % enumValues.length;
		return "item.thermionics."+id+"."+enumValues[meta].name().toLowerCase();
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(int i=0; i<enumValues.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		if (glowing) return true;
		return (super.hasEffect(stack));
	}

	@Override
	public String[] getModelLocations() {
		ArrayList<String> variants = new ArrayList<String>();
		for(Enum<T> t : enumValues) {
			if (t instanceof IStringSerializable) {
				variants.add(id + "." + ((IStringSerializable)t).getName() );
			} else {
				variants.add(id + "." + t.name().toLowerCase());
			}
		}
		return variants.toArray(new String[variants.size()]);
		
	}
}
