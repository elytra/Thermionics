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
package com.elytradev.concrete.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Base/internal class for managing item storage.
 * 
 * <h2>Validation</h2>
 * 
 * The ConcreteItemStorage itself <em>does not</em> perform any kind of validation on access, because it's an
 * administrative object that needs to support unfiltered serialization and deserialization. Instead, it provides
 * the same set of validators to all "view" wrappers, so that consistency is maintained between automated access and
 * player/GUI access. When dealing with automation, use a {@link ValidatedItemHandlerView}, and in order to create an
 * IInventory that a ConcreteContainer can use, use a {@link ValidatedInventoryView}. Set your access limitations on
 * this object with {@link #withValidators(Predicate...)} and stack extraction controls with
 * {@link #setCanExtract(int, boolean)}, and these two view objects will always know what kinds of items are allowed
 * where.
 *  
 * <h2>Serialization and Deserialization</h2>
 * 
 * <p>If you're using this object to manage the inventory in a TileEntity, it takes three small tweaks to get no-fuss
 * serialization. In your constructor, add
 * 
 * <code><pre>itemStorage.listen(this::markDirty);</pre></code>
 * 
 * <p>This will mark your tile dirty any time the inventory changes, so that Minecraft won't skip serialization. Then in
 * writeToNBT:
 * 
 * <code><pre>tagOut.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemStorage, null));</pre></code>
 * 
 * <p>and in readFromNBT:
 * 
 * <code><pre>CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemStorage, null, tag.getTag("inventory"));</pre></code>
 * 
 * <p>Where <code>itemStorage</code> is your ConcreteItemStorage object. At that point the Forge Capability system will
 * do all the work for you on this, and you can focus on the interesting part of making your tile do what it's supposed to.
 * 
 * <h2>Exposing as a capability</h2>
 * 
 * <p>This was mentioned above in the Validation section, but although this object supplies the IItemHandler interface,
 * generally you want to create a ValidatedItemHandlerView wrapper instead. These wrappers are okay to cache or memoize,
 * and merely provide a succinct delegation based on the access rules this object provides.
 * 
 * <p>Simplifying hasCapability and getCapability are outside the scope of this object... but remember that "null" is a
 * valid side, and often represents the side a probe observer accesses, so plan your views accordingly.
 * 
 */
public class ConcreteItemStorage extends ItemStackHandler implements IObservableItemHandler {
	private ArrayList<Runnable> listeners = new ArrayList<>();
	private ArrayList<Predicate<ItemStack>> validators = new ArrayList<>();
	private boolean[] extractMask;
	private String name = "";
	
	public ConcreteItemStorage(int slots) {
		super(slots);
		extractMask = new boolean[slots];
		Arrays.fill(extractMask, true);
	}
	
	public final ConcreteItemStorage withName(String name) {
		this.name = name;
		return this;
	}
	
	@SafeVarargs
	public final ConcreteItemStorage withValidators(Predicate<ItemStack>... predicates) {
		validators.clear();
		for(Predicate<ItemStack> predicate : predicates) validators.add(predicate);
		return this;
	}
	
	public final ConcreteItemStorage setCanExtract(int index, boolean canExtract) {
		if (index<extractMask.length) extractMask[index] = canExtract;
		return this;
	}

	public void markDirty() {
		for(Runnable r : listeners) {
			r.run();
		}
	}
	
	@Override
	public void listen(@Nonnull Runnable r) {
		listeners.add(r);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = super.extractItem(slot, amount, simulate);
		if (!simulate) markDirty();
		return stack;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
		ItemStack result = super.insertItem(slot, itemStack, simulate);
		if (!simulate) markDirty();
		return result;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack itemStack) {
		super.setStackInSlot(slot, itemStack);
		markDirty();
	}
	
	@Nullable
	public String getName() {
		return name;
	}
	
	public boolean hasValidator(int slot) {
		return validators.size()>slot;
	}
	
	/**
	 * Returns a validator for the indicated slot if one is set, otherwise returns a validator which accepts anything.
	 */
	@Nonnull
	public Predicate<ItemStack> getValidator(int slot) {
		if (validators.size()<=slot) return Validators.ANYTHING;
		return validators.get(slot);
	}
	
	public boolean getCanExtract(int slot) {
		if (extractMask.length<=slot) return false;
		return extractMask[slot];
	}
}
