package com.elytradev.thermionics.item;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;

public class ItemMistcloak extends ItemBodyBauble implements IMetaItemModel {
	private static final String NAME_TASSELCLOAK = "item.thermionics.bauble.tasselcloak";
	private static final String NAME_MISTCLOAK = "item.thermionics.bauble.mistcloak";
	
	public ItemMistcloak() {
		super("cloak");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}

	public boolean isAllomantic(ItemStack stack) {
		return (stack.getItemDamage()==1);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (isAllomantic(stack)) ? NAME_MISTCLOAK : NAME_TASSELCLOAK;
	}
	
	public ItemStack createMistcloak() {
		return new ItemStack(this, 1, 1);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getItemDamage()==1;
	}
	
	@Override
	public String[] getModelLocations() {
		return new String[]{ "bauble.cloak.tasselcloak", "bauble.cloak.mistcloak" };
	}
	
	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player.world.isRemote) return;
		
		if (stack.getItemDamage()==1) {
			Potion invis = Potion.getPotionFromResourceLocation("minecraft:invisibility");
			
			if (player.getBrightness(0f) < 0.4375f) {
				player.addPotionEffect(new PotionEffect(invis, 5));
			}
		}
	}
}
