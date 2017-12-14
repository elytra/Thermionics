package com.elytradev.thermionics.api;

import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public interface IWeaponSkillInfo {
	/** Standard Weaponskill resource that is expected to recharge over time and be expended by spell abilities */
	public static final ResourceLocation RESOURCE_MANA = new ResourceLocation("thermionics", "mana");
	/** Standard Weaponskill resource that is expected to recharge when an entity dies near the player */
	public static final ResourceLocation RESOURCE_BLOOD = new ResourceLocation("thermionics", "blood");
	/** Standard Weaponskill resource that is expected to slowly recharge while in battle, and quickly recharge when damage is taken or dealt. */
	public static final ResourceLocation RESOURCE_RAGE = new ResourceLocation("thermionics", "rage");
	/** Standard Weaponskill resource that is expected to hover near zero, but rise as chaotic spells are cast, and
	 * cause more risky / detrimental effects the closer it gets to 100. At 100, it engulfs the caster entirely and
	 * spawns a short-lived chaos storm. */
	public static final ResourceLocation RESOURCE_CHAOS = new ResourceLocation("thermionics", "chaos");
	/** Standard Weaponskill resource which instantly charges to ~40 whenever an attack is blocked while cooldown is up.
	 * Vengeance  decays by 1 each tick, and attacking while any Vengeance is present will consume it all and guarantee
	 * a critical hit. */
	public static final ResourceLocation RESOURCE_VENGEANCE = new ResourceLocation("thermionics", "vengeance");
	
	/**
	 * Gets the current level of a Weaponskill resource
	 * @param id The globally-unique name of the resource
	 * @return The amount; valid range and semantics depend on the resource.
	 */
	public int getResource(ResourceLocation id);
	
	/**
	 * "Spends" a resource. That is, if there is {@code amount} of that resource present, decrements the resource by
	 * that amount and returns the amount spent. If there isn't enough, none is spent and zero is returned
	 * @param id     The globally-unique name of the resource
	 * @param amount The amount to spend
	 * @return       The amount spent, or zero if not enough is available.
	 */
	public int spend(ResourceLocation id, int amount);
	
	public void set(ResourceLocation id, int amount);
	
	/**
	 * Gets the last primary skill cooldown set which exceeded the current one. This is used for rendering the cooldown
	 * indicator.
	 * @return The max cooldown amount which we're counting down from
	 */
	public int getMaxCooldown();
	
	public void setMaxCooldown(int max);
	
	/**
	 * Gets the weaponskill user's current "primary skill cooldown". This prevents weapon skills like Bleed, Vengeance,
	 * and Smash from triggering one after the other with rapid weapon switches.
	 * @return The primary skill cooldown. If this method returns zero or lower, weaponskills are ready to fire.
	 */
	public int getCooldown();
	
	public void setCooldown(int cd);
	
	/**
	 * This method is equivalent to Map::entrySet for all non-primary-cooldown values
	 * @return all the currently set weaponskill entries
	 */
	public Set<Map.Entry<ResourceLocation, Integer>> entrySet();
}
