package com.elytradev.thermionics.api;

public interface IOreRepair {
	/**
	 * Gets the ore dictionary key for the material that can repair this item on an anvil. 
	 * @return a String, such as "ingotCopper" for an item that can be repaired by a copper bar.
	 */
	public String getOreRepairMaterial();
}
