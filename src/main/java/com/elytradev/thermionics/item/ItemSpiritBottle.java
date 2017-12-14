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

package com.elytradev.thermionics.item;

import javax.annotation.Nullable;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.Spirits;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemSpiritBottle extends Item {
	public ItemSpiritBottle() {
		this.setRegistryName("thermionics", "spiritbottle");
		this.setUnlocalizedName("thermionics.spiritbottle");
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}
	
	public boolean isEmpty(ItemStack bottle) {
		return getSpirit(bottle)==null && PotionUtils.getEffectsFromStack(bottle).isEmpty();
	}
	
	@Nullable
	public Spirit getSpirit(ItemStack bottle) {
		if (bottle.hasTagCompound() && bottle.getTagCompound().hasKey("Spirit")) {
			return Spirits.REGISTRY.getValue(new ResourceLocation(bottle.getTagCompound().getString("Spirit")));
		} else {
			return null;
		}
	}
	
	/*
	@Nullable
	public Potion getPotion(ItemStack bottle) {
		PotionType type = PotionUtils.getPotionFromItem(bottle);
		
		
		if (bottle.hasTagCompound() && bottle.getTagCompound().hasKey("Potion")) {
			return Potion.REGISTRY.getObject(new ResourceLocation(bottle.getTagCompound().getString("Potion")));
		} else {
			return null;
		}
	}*/
	
	/**
	 * Applies all effects in the bottle to the entity; if the bottle has both a potion and a spirit, both are applied.
	 */
	public void apply(ItemStack bottle, EntityLivingBase entity) {
		Spirit spirit = getSpirit(bottle);
		if (spirit!=null) {
			//TODO: Drunkenness system
		}
		
		for (PotionEffect potion : PotionUtils.getEffectsFromStack(bottle)) {
			if (potion.getPotion().isInstant()) {
                potion.getPotion().affectEntity(entity, entity, entity, potion.getAmplifier(), 1.0D);
            } else {
                entity.addPotionEffect(new PotionEffect(potion));
            }
		}
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;

        if (player == null || !player.capabilities.isCreativeMode)  {
            stack.shrink(1);
        }
		
        if (player instanceof EntityPlayerMP) {
        	//Trigger advancements! (Protip: there are no advancements for this)
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player, stack);
        }
		
        if (!world.isRemote) {
           apply(stack, entity);
        }
        
        if (player != null) {
            player.addStat(StatList.getObjectUseStats(this));
        }
        
        if (player == null || !player.capabilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(ThermionicsItems.SPIRIT_BOTTLE);
            }

            if (player != null) {
                player.inventory.addItemStackToInventory(new ItemStack(ThermionicsItems.SPIRIT_BOTTLE));
            }
        }
        
		return stack;
	}
}
