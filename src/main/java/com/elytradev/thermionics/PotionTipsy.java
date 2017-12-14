package com.elytradev.thermionics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionTipsy extends Potion {
	private static final ResourceLocation TEXTURE_TIPSY = new ResourceLocation("thermionics", "textures/effect/tipsy.png");
	
	public PotionTipsy() {
		super(false, 0x5fc38e);
		
		setPotionName("thermionics.tipsy");
		setRegistryName("thermionics", "tipsy");
		registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "B8A789C1-AD15-40FF-9064-FB70EA274CF8", -0.2D, 0); //It goes without saying that Tipsy makes you unsteady
	}
	
	@SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) {
		mc.renderEngine.bindTexture(TEXTURE_TIPSY);
		GlStateManager.color(1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x+6, y+7, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		mc.renderEngine.bindTexture(TEXTURE_TIPSY);
		GlStateManager.color(1, 1, 1, alpha);
		Gui.drawModalRectWithCustomSizedTexture(x+3, y+3, 0, 0, 18, 18, 18, 18);
	}
}
