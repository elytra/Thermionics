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

import com.elytradev.thermionics.api.Spirits;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("deprecation")
public class FluidSpirit extends Fluid {

	public FluidSpirit(String fluidName, ResourceLocation still, ResourceLocation flowing) {
		super(fluidName, still, flowing);
	}

	@Override
	public int getColor(FluidStack stack) {
		String spirit = stack.tag.getString("Spirit");
		if (spirit!=null) {
			Spirit actual = Spirits.REGISTRY.getValue(new ResourceLocation(spirit));
			if (actual!=null) return actual.getColor();
		}
		
		String potion = stack.tag.getString("Potion");
		if (potion!=null) {
			Potion actual = Potion.getPotionFromResourceLocation(potion);
			if (actual!=null) return actual.getLiquidColor();
		}
		
		return 0x20FFFFFF; //Non-subtyped spirits are clear, colorless ethanol.
	}
	
	@Override
	public String getUnlocalizedName(FluidStack stack) {
		String spirit = stack.tag.getString("Spirit");
		if (spirit!=null) {
			Spirit actual = Spirits.REGISTRY.getValue(new ResourceLocation(spirit));
			if (actual!=null) return actual.getUnlocalizedDistilledName();
		}
		getLocalizedName(stack);
		String potion = stack.tag.getString("Potion");
		if (potion!=null) {
			Potion actual = Potion.getPotionFromResourceLocation(potion);
			if (actual!=null) return actual.getName();
		}
		return "spirit.ethanol.name";
	}
	
	@Override
	public ResourceLocation getStill(FluidStack stack) {
		if (stack==null || stack.tag==null) return Spirit.Clarity.MEDIUM.getStillSpiritIcon();
		
		String spirit = stack.tag.getString("Spirit");
		if (spirit!=null) {
			Spirit actual = Spirits.REGISTRY.getValue(new ResourceLocation(spirit));
			if (actual!=null) return actual.getClarity().getStillSpiritIcon();
		}
		return Spirit.Clarity.MEDIUM.getStillSpiritIcon();
	}
	
	@Override
	public ResourceLocation getFlowing(FluidStack stack) {
		String spirit = stack.tag.getString("Spirit");
		if (spirit!=null) {
			Spirit actual = Spirits.REGISTRY.getValue(new ResourceLocation(spirit));
			if (actual!=null) return actual.getClarity().getStillSpiritIcon();
		}
		return Spirit.Clarity.MEDIUM.getFlowingSpiritIcon();
	}
	
	@Override
	public String getLocalizedName(FluidStack stack) {
        String s = this.getUnlocalizedName(stack);
        return s == null ? "" : I18n.translateToLocal(s);
    }
}
