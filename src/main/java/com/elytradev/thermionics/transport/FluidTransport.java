package com.elytradev.thermionics.transport;

import com.elytradev.thermionics.tileentity.TileEntityDrum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTransport {
	
	public static FluidActionResult tryEmptyOrFillContainer(ItemStack stack, IFluidHandler handler, int maxTransfer, EntityPlayer player) {
		FluidActionResult result = FluidUtil.tryEmptyContainer(stack, handler, maxTransfer, player, true);
		if (result.success) {
			return result;
			/*
			ItemStack remaining = result.result;
			if (remaining==null || remaining==ItemStack.EMPTY) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			} else {
				player.setHeldItem(hand, remaining);
			}
			System.out.println("SUCCESS: "+remaining);
			return true;*/
		} else {
			//System.out.println("UNSUCCESS.");
			
			FluidActionResult resultFill = FluidUtil.tryFillContainer(stack, handler, maxTransfer, player, true);
			return resultFill;
			/*
			if (resultFill.success) {
				ItemStack remaining = resultFill.result;
				if (remaining==null || remaining==ItemStack.EMPTY) {
					player.setHeldItem(hand, ItemStack.EMPTY);
				} else {
					player.setHeldItem(hand, remaining);
				}
				System.out.println("SUCCESS-Empty: "+remaining);
				return true;
			}
			System.out.println("UNSUCCESS-Empty");
			player.swingArm(hand);
			return true;*/
		}
	}
	
	public static boolean handleRightClickFluidStorage(EntityPlayer player, EnumHand hand, IFluidHandler tank, int maxTransfer) {
		ItemStack fluidItem = player.getHeldItem(hand);
		if (fluidItem==null || fluidItem.isEmpty()) return false;
		if (!fluidItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			return false;
		}
		
		//System.out.println("Handling fluid interaction...");
		
		FluidActionResult result = tryEmptyOrFillContainer(fluidItem, tank, maxTransfer, player);
		if (result.isSuccess()) {
			//System.out.println("Successful fluid interaction! Result is: "+result.result.toString());
			player.setHeldItem(hand, result.result);
			return true;
		} else {
			//System.out.println("Unsuccessful fluid interaction.");
			return false;
		}
	}
}
