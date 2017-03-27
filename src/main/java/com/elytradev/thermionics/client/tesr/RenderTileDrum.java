package com.elytradev.thermionics.client.tesr;

import com.elytradev.thermionics.data.ObservableFluidStorage;
import com.elytradev.thermionics.tileentity.TileEntityDrum;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RenderTileDrum extends TileEntitySpecialRenderer<TileEntityDrum> {
	@Override
	public void renderTileEntityAt(TileEntityDrum te, double x, double y, double z, float partialTicks, int destroyStage) {
		ObservableFluidStorage storage = te.getFluidStorage();
		FluidStack stack = storage.getFluid();
		Fluid fluid = stack.getFluid();
		
		//super.bindTexture(fluid.getStill(stack));
		//GlStateManager.pushMatrix();
        //GlStateManager.translate(x, y, z);
		
		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
		
	}
}
