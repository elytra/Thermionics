/**
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
package com.elytradev.thermionics.client.tesr;

import com.elytradev.thermionics.data.ObservableFluidStorage;
import com.elytradev.thermionics.tileentity.TileEntityDrum;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RenderTileDrum extends TileEntitySpecialRenderer<TileEntityDrum> {
	@Override
	public void render(TileEntityDrum te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ObservableFluidStorage storage = te.getFluidStorage();
		FluidStack stack = storage.getFluid();
		Fluid fluid = stack.getFluid();
		
		//super.bindTexture(fluid.getStill(stack));
		//GlStateManager.pushMatrix();
        //GlStateManager.translate(x, y, z);
		
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		
	}
}
