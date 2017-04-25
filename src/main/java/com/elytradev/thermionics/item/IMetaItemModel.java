package com.elytradev.thermionics.item;

/** Represents an item which is aware of the names of its models */
public interface IMetaItemModel {
	/**
	 * Each element in the returned array should be a valid item-model path.
	 * The item will be registered with <code>meta 0 -> getModelLocations()[0]</code> and so on.
	 **/
	public String[] getModelLocations();
}
