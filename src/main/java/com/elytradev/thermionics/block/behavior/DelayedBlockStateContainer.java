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

package com.elytradev.thermionics.block.behavior;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;

public class DelayedBlockStateContainer extends BlockStateContainer {
	private boolean frozen = false;
	
	private Block block;
	private Map<String, PropertyValueMap<?>> values = new HashMap<>();
	
	private BlockStateContainer delegate = null;
	
	public DelayedBlockStateContainer(Block block) {
		super(block);
		this.block = block;
	}

	/* DELAYED MEMES */
	
	public <T extends Comparable<T>> DelayedBlockStateContainer add(IProperty<T> property) {
		if (frozen) throw new IllegalStateException("Tried to add property "+property.getName()+" to an immutable BlockStateContainer.");
		validateProperty(this.getBlock(), property);
		//properties.put(property.getName(), property);
		values.put(property.getName(), new PropertyValueMap<>(property));
		return this;
	}

	public DelayedBlockStateContainer freeze(BlockStateBehavior behavior) {
		frozen = true;
		if (behavior!=null) {
			delegate = behavior.createBlockState(block);
		} else {
			values = ImmutableMap.copyOf(values);
			IProperty<?>[] properties = new IProperty<?>[values.size()];
			int i = 0;
			for(PropertyValueMap<?> map : values.values()) {
				properties[i] = map.getKey();
				i++;
			}
			delegate = new BlockStateContainer(block, properties);
		}
		
		return this;
	}
	
	/* IMPL / PASSTHROUGH */
	
	public ImmutableList<IBlockState> getValidStates() {
		if (delegate==null) return ImmutableList.of();
		return delegate.getValidStates();
	}

	public IBlockState getBaseState() {
		if (delegate==null) return super.getBaseState();
		//if (delegate==null) return null;
		//Validate.notNull(delegate, "Cannot get the base state of an unbuilt BlockStateContainer.");
		return delegate.getBaseState();
	}

	public Block getBlock() {
		return block;
	}

	public Collection<IProperty<?>> getProperties() {
		return ImmutableList.copyOf(
				values
				.values()
				.stream()
				.map(it->it.getKey())
				.iterator());
	}

	public String toString() {
		return "{block:\""+Block.REGISTRY.getNameForObject(this.block)+"\" properties:"
				+this.values.keySet()+"}";
	}

	@Nullable
	public IProperty<?> getProperty(String propertyName) {
		return values.get(propertyName).getKey();
	}
	
	/* BITFIELD LOGIC */
	
	public static class PropertyValueMap<T extends Comparable<T>> {
		private int bitOffset = 0;
		private int bitWidth = 0;
		private int bitMask = 0x0;
		BitSet bits;
		
		private IProperty<T> property;
		private T[] values;
		
		@SuppressWarnings("unchecked")
		public PropertyValueMap(IProperty<T> property) {
			this.property = property;
			values = (T[])property.getAllowedValues().toArray();
			bitWidth = (int)Math.ceil(MathHelper.log2(values.length));
			setBitOffset(0);
		}
		
		public void setBitOffset(int offset) {
			bitOffset = offset;
			bitMask = 0x0;
			for(int i=0; i<bitWidth; i++) {
				bitMask = bitMask << 1;
				bitMask |= 1;
			}
		}
		
		public int width() {
			return bitWidth;
		}
		
		@Nonnull
		public IProperty<T> getKey() {
			return property;
		}
		
		@Nullable
		public T getValue(long bits) {
			int index = (int)((bits >> bitOffset) & bitMask);
			if (index>=values.length) return null;
			return values[index];
		}
		
		@Nullable
		public T getValue(@Nonnull BitSet bits) {
			int index = (int) longValue(bits.get(bitOffset, bitOffset+bitWidth));
			if (index>=values.length) return null;
			return values[index];
			
		}
		
		/** Returns the lowest-order long in a BitSet **/
		private static long longValue(@Nonnull BitSet bits) {
			if (bits.length()>64) return Long.MAX_VALUE;
			return bits.toLongArray()[0];
		}
	}
}
