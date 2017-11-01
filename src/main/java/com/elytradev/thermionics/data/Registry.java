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

package com.elytradev.thermionics.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Lighter-weight 
 * @param <T>
 */
public class Registry<T> implements Iterable<T> {
	private BiMap<ResourceLocation, T> items = HashBiMap.create();
	private final Class<T> superType;
	
	public Registry(Class<T> superType) {
		this.superType = superType;
	}
	
	public void register(ResourceLocation id, T t) {
		items.put(id, t);
	}
	
	//XXX This might explode horribly in some cases, I'm not sure.
	@SuppressWarnings("unchecked")
	public <Q extends IForgeRegistryEntry<T>> void register(Q q) {
		if (!superType.isAssignableFrom(q.getClass())) throw new IllegalArgumentException("Cannot cast "+q.getClass().getCanonicalName()+" to "+superType.getCanonicalName());
		items.put(q.getRegistryName(), (T) q);
	}
	
	@Override
	public Iterator<T> iterator() {
		return ImmutableSet.copyOf(items.values()).iterator();
	}
	
	public boolean containsKey(ResourceLocation key) {
		return items.containsKey(key);
	}
	
	public boolean containsValue(T value) {
		return items.containsValue(value);
	}
	
	@Nullable
	public T getValue(ResourceLocation key) {
		return items.get(key);
	}
	
	@Nullable
	public ResourceLocation getKey(T value) {
		return items.inverse().get(value);
	}
	
	public Set<ResourceLocation> getKeys() {
		return ImmutableSet.copyOf(items.keySet());
	}
	
	public Set<T> getValues() {
		return ImmutableSet.copyOf(items.values());
	}
	
	public Set<Map.Entry<ResourceLocation, T>> getEntries() {
		return ImmutableSet.copyOf(items.entrySet());
	}
	
	public Class<T> getSuperType() {
		return superType;
	}
}
