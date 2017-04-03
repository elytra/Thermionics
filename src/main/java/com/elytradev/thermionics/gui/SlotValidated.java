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
package com.elytradev.thermionics.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class SlotValidated extends Slot {
	private IItemHandler inventory;
	private int index;
	private boolean readonly = false;
	
	public SlotValidated(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(null, index, xPosition, yPosition);
		this.inventory = inventoryIn;
		this.index = index;
	}

	@Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
    	return inventory.extractItem(index, stack.getCount(), false);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
    	return inventory.insertItem(index, stack, true).isEmpty();
    }

    @Override
    public ItemStack getStack() {
    	return inventory.getStackInSlot(index);
    }

    @Override
    public boolean getHasStack() {
        return !getStack().isEmpty();
    }

    @Override
    public void putStack(ItemStack stack) {
    	//This method is sadly lacking in validation! If the inserted item is rejected, *it disappears*. So be careful.
    	inventory.insertItem(index, stack, false);
    }

    @Override
    public void onSlotChanged() {
    	//With the Capability interface, we can safely assume that this happens "under the hood" through messages fired
    	//from the bound data.
    }


    @Override
    public int getSlotStackLimit() {
        return inventory.getSlotLimit(this.slotNumber);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return Math.min(getSlotStackLimit(), stack.getMaxStackSize());
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public String getSlotTexture() {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
    	return inventory.extractItem(index, amount, false);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
    	return slotIn==index; //we can't verify whether the IInventory is correct!
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
    	
        return (!this.readonly) && (inventory.extractItem(index, 1, true).isEmpty());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canBeHovered() {
    	return this.readonly;
    }

    /* Overridden Forge stuff. Mostly just hacking off the optional slot background textures. */

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.util.ResourceLocation getBackgroundLocation() {
        return net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setBackgroundLocation(net.minecraft.util.ResourceLocation texture) { }

    @Override
    public void setBackgroundName(String name) {}

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.client.renderer.texture.TextureAtlasSprite getBackgroundSprite() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected net.minecraft.client.renderer.texture.TextureMap getBackgroundMap() {
        return null;
    }

    /**
     * Returns the slot number in the host IItemHandler (NOT the Slot index in the GuiContainer!)
     */
    @Override
    public int getSlotIndex() {
        return this.index;
    }

    @Override
    public boolean isSameInventory(Slot other) {
    	if (other instanceof SlotValidated) {
    		return ((SlotValidated)other).inventory==this.inventory;
    	} else {
    		return false;
    	}
    }
}
