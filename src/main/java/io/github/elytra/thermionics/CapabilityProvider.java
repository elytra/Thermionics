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
package io.github.elytra.thermionics;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import io.github.elytra.thermionics.data.RelativeDirection;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Provides capabilities on behalf of a block.
 */

public class CapabilityProvider {
	private ArrayList<Entry<?>> entries = new ArrayList<>();
	
	public boolean canProvide (@Nullable RelativeDirection side, @Nonnull Capability<?> capability) {
		Validate.notNull(capability);
		
		for(Entry<?> entry : entries) {
			if (side!=null) {
				if (entry.directions.contains(side) && capability.equals(entry.capability)) return true;
			} else {
				if (capability.equals(entry.capability)) return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T provide(@Nonnull RelativeDirection side, @Nonnull Capability<T> capability) {
		Validate.notNull(capability);
		
		for(Entry<?> entry : entries) {
			if (side!=null) {
				if (entry.directions.contains(side) && capability.equals(entry.capability)) return (T)entry.provide();
			} else {
				if (capability.equals(entry.capability)) return (T)entry.provide();
			}
		}
		
		return null;
	}
	
	public <T> void registerForAllSides(Capability<T> cap, Supplier<T> provider) {
		entries.add(new Entry<T>(cap,provider));
	}
	
	public static class Entry<T> {
		private Capability<T> capability;
		private Supplier<T> supplier;
		private T provided;
		private EnumSet<RelativeDirection> directions;
		
		public Entry(Capability<T> cap, Supplier<T> supplier, RelativeDirection... directions) {
			this.capability = cap;
			this.supplier = supplier;
			this.directions = EnumSet.noneOf(RelativeDirection.class);
			for(RelativeDirection d : directions) this.directions.add(d);
		}
		
		public Entry(Capability<T> cap, Supplier<T> supplier) {
			this.capability = cap;
			this.supplier = supplier;
			this.directions = EnumSet.allOf(RelativeDirection.class);
		}
		
		public T provide() {
			if (provided==null) {
				provided = supplier.get();
				
			}
			
			return provided;
		}
	}
}
