package com.elytradev.thermionics.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.BlockPos;

public class Network {
	private List<NetworkPath> livePaths = new ArrayList<>();
	private Map<BlockPos, NetworkPath> pathMembership = new HashMap<>();
	
	public void clear() {
		livePaths.clear();
		pathMembership.clear();
	}
}
