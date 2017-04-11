package com.elytradev.concrete.inventory;

import javax.annotation.Nonnull;

import net.minecraftforge.items.IItemHandler;

public interface IObservableItemHandler extends IItemHandler {
	public void listen(@Nonnull Runnable r);
}
