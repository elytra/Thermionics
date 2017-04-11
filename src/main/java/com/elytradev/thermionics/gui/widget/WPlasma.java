package com.elytradev.thermionics.gui.widget;

import com.elytradev.concrete.client.gui.GuiDrawing;
import com.elytradev.concrete.gui.widget.WWidget;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WPlasma extends WWidget {
	
	private int[] lut = { 0xFFb0b0b0, 0xFFc0c0c0, 0xFFc6c6c6, 0xFFd2d2d2, 0xFFd6d6d6 };
	private Term[] xTerms = { new Term(), new Term(), new Term() };
	private Term[] yTerms = { new Term(), new Term(), new Term() };
	
	
	@Override
	public boolean canResize() { return true; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for(int yi=0; yi<this.getHeight(); yi++) {
			for(int xi=0; xi<this.getWidth(); xi++) {
				float cell = 0f;
				for(Term term : xTerms) {
					cell += term.get(xi);
				}
				for(Term term : yTerms) {
					cell += term.get(yi);
				}
				
				cell += 10f; // -10..10 -> 0..20 UNCLAMPED
				cell /= 20f; // 0..20 -> 0..1 UNCLAMPED
				cell = clamp(cell); //0..1 UNCLAMPED -> [0..1) CLAMPED
				int idx = (int)(cell*lut.length);
				int col = lut[idx];
				
				GuiDrawing.rect(x+xi-1, y+yi-1, 1, 1, col);
			}
		}
		
		//TODO: Advancing lines here is a bad idea because it moves at different speeds on each computer. We need to
		//insert some kind of timing here.
		for(Term term : xTerms) {
			term.phase += term.v;
		}
		for(Term term : yTerms) {
			term.phase += term.v;
		}
	}
	
	
	private static class Term {
		public float amplitude = 10f + (float)Math.random()*5f;
		public float frequency = (float)(Math.PI*2)/(10f+(float)Math.random()*30f);
		public float phase = (float)Math.random()*20f; //Changes over time!
		public float dc = 0f;
		public float v = (float)(Math.random()-0.5f)*0.5f;
		
		public float get(int ofs) {
			return (float)(Math.sin((ofs+phase)*frequency) * amplitude + dc);
		}
	}
	
	/** The biggest possible float that's still less than 1.0f */
	private static final float LESS_THAN_ONE = Math.nextDown(1.0f);
	/** Clamp to [0..1) */
	public static float clamp(float in) {
		if (in<0.0f) return 0.0f;
		if (in>LESS_THAN_ONE) return LESS_THAN_ONE;
		return in;
	}
}
