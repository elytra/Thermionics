package io.github.elytra.thermionics.block;

import io.github.elytra.thermionics.block.behavior.BlockStateBehavior;
import net.minecraft.block.material.Material;

public class BlockMotorBase extends BlockImpl {

	public BlockMotorBase(String id) {
		super(Material.IRON, "machine.motor."+id, BlockStateBehavior.DIRECTIONAL);
	}
}
