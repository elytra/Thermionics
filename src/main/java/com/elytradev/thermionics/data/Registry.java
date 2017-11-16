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

public class Registry<K extends Comparable<K>, V> implements Iterable<V> {
	private BiMap<K, V> items = HashBiMap.create();
	private final Class<V> superType;
	
	public Registry(Class<V> superType) {
		this.superType = superType;
	}
	
	public void register(K id, V t) {
		items.put(id, t);
	}
	
	@Override
	public Iterator<V> iterator() {
		return ImmutableSet.copyOf(items.values()).iterator();
	}
	
	public boolean containsKey(K key) {
		return items.containsKey(key);
	}
	
	public boolean containsValue(V value) {
		return items.containsValue(value);
	}
	
	@Nullable
	public V getValue(K key) {
		return items.get(key);
	}
	
	@Nullable
	public K getKey(V value) {
		return items.inverse().get(value);
	}
	
	public Set<K> getKeys() {
		return ImmutableSet.copyOf(items.keySet());
	}
	
	public Set<V> getValues() {
		return ImmutableSet.copyOf(items.values());
	}
	
	public Set<Map.Entry<K, V>> getEntries() {
		return ImmutableSet.copyOf(items.entrySet());
	}
	
	public Class<V> getSuperType() {
		return superType;
	}
}
