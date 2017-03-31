package com.elytradev.thermionics;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class PotionEffortlessSpeed extends Potion {

	public PotionEffortlessSpeed() {
		super(false, 0x428cae);
		setPotionName("effect.moveSpeed.effortless");
		setIconIndex(0, 0);
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.60000000298023224D, 2);
		setBeneficial();
	}
	
	
}
