/*
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

package com.elytradev.thermionics.tileentity;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.compat.ProbeDataSupport;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityOmniDuct extends TileEntity implements ITickable {
	public static double MAX_FLUID_INDUCTION = 10;
	public static double MAX_RF_INDUCTION    = 10;
	public static double MAX_HEAT_INDUCTION  = 10;
	
	public static int MAX_ITEMS =    999;
	public static int MAX_FLUID =  8_000;
	public static int MAX_RF    = 80_000;
	public static int MAX_HEAT  =  8_000;
	
	protected Vec3d rfInduction = Vec3d.ZERO;
	protected Vec3d fluidInduction = Vec3d.ZERO;
	protected Vec3d heatInduction = Vec3d.ZERO;
	protected ItemStack heldItem = ItemStack.EMPTY;
	protected FluidStack heldFluid = null;
	protected int heldHeat = 0;
	protected int heldRF = 0;
	
	protected DuctItemHandler cap = new DuctItemHandler();
	protected DuctFluidHandler fluidCap = new DuctFluidHandler();
	protected DuctEnergyHandler energyCap = new DuctEnergyHandler();
	protected DuctHeatHandler heatCap = new DuctHeatHandler();
	protected Object probeSupport = null;
	
	public TileEntityOmniDuct() {
		probeSupport = ProbeDataSupport.getGenericSupport(this);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		if (capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
		if (capability==CapabilityEnergy.ENERGY) return true;
		if (capability==Thermionics.CAPABILITY_HEATSTORAGE) return true;
		if (probeSupport!=null && capability==ProbeDataSupport.PROBE_CAPABILITY) return true;
		
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) { //Note to modders: DO NOT CACHE these capabilities! They will induce weird wrong currents
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			cap.incomingFlow = (facing==null) ? EnumFacing.DOWN : facing.getOpposite();
			return (T) cap;
		}
		
		if (capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			fluidCap.incomingFlow = (facing==null) ? EnumFacing.DOWN : facing.getOpposite();
			return (T) fluidCap;
		}
		
		if (capability==CapabilityEnergy.ENERGY) {
			energyCap.incomingFlow = (facing==null) ? EnumFacing.DOWN : facing.getOpposite();
			return (T) energyCap;
		}
		
		if (capability==Thermionics.CAPABILITY_HEATSTORAGE) {
			heatCap.incomingFlow = (facing==null) ? EnumFacing.DOWN : facing.getOpposite();
			return (T) heatCap;
		}
		
		if (capability==ProbeDataSupport.PROBE_CAPABILITY) {
			return (T) probeSupport;
		}
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag("ItemStack", heldItem.writeToNBT(new NBTTagCompound()));
		if (heldFluid!=null) {
			tag.setTag("FluidStack", heldFluid.writeToNBT(new NBTTagCompound()));
		} else {
			tag.removeTag("FluidStack"); //Just in case.
		}
		tag.setInteger("Heat", heldHeat);
		tag.setInteger("Energy", heldRF);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		if (compound.hasKey("ItemStack")) {
			heldItem.deserializeNBT(compound.getCompoundTag("ItemStack"));
			if (heldItem.isEmpty()) heldItem = ItemStack.EMPTY;
		} else {
			heldItem = ItemStack.EMPTY;
		}
		if (compound.hasKey("FluidStack")) {
			heldFluid = FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("FluidStack"));
			if (heldFluid.amount==0) heldFluid = null;
		} else {
			heldFluid = null;
		}
		
		if (compound.hasKey("Heat")) heldHeat = compound.getInteger("Heat");
		if (compound.hasKey("Energy")) heldRF = compound.getInteger("Energy");
	}
	
	public void dropItem() {
		if (!world.isRemote && !heldItem.isEmpty() && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
			double d1 = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
			double d2 = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
			EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, heldItem);
			entityitem.setDefaultPickupDelay();
			world.spawnEntity(entityitem);
		}
	}
	
	@Override
	public void update() {
		if (heldItem==ItemStack.EMPTY && heldFluid==null) {
			decayFluidInduction();
		} else {
			EnumFacing flow = EnumFacing.getFacingFromVector((float)fluidInduction.x, (float)fluidInduction.y, (float)fluidInduction.z);
			
			if (heldItem!=ItemStack.EMPTY) {
				pushTo(flow, false);
				
				if (heldItem!=ItemStack.EMPTY) {
					for(EnumFacing facing : EnumFacing.VALUES) {
						if (facing==flow || facing==flow.getOpposite()) continue;
						pushTo(facing, false);
						if (heldItem==ItemStack.EMPTY) break; 
					}
				
					if (heldItem!=ItemStack.EMPTY) {
						pushTo(flow.getOpposite(), false);
						
						if (heldItem!=ItemStack.EMPTY) {
							decayFluidInduction();
						}
					}
				}
			}
			
			if (heldFluid!=null) {
				pushFluidTo(flow, false);
				
				if (heldFluid!=null) {
					for(EnumFacing facing : EnumFacing.VALUES) {
						if (facing==flow || facing==flow.getOpposite()) continue;
						pushFluidTo(facing, false);
						if (heldFluid==null) break; 
					}
				
					if (heldFluid!=null) {
						pushFluidTo(flow.getOpposite(), false);
						
						if (heldFluid!=null) {
							decayFluidInduction();
						}
					}
				}
			}
		}
		
		if (heldRF<=0) {
			decayRFInduction();
		} else {
			EnumFacing flow = EnumFacing.getFacingFromVector((float)rfInduction.x, (float)rfInduction.y, (float)rfInduction.z);
			
			pushEnergyTo(flow, false);
			
			if (heldRF>0) {
				for(EnumFacing facing : EnumFacing.VALUES) {
					if (facing==flow || facing==flow.getOpposite()) continue;
					pushEnergyTo(facing, false);
					if (heldRF<=0) break; 
				}
			
				if (heldRF>0) {
					pushEnergyTo(flow.getOpposite(), false);
					
					if (heldRF>0) {
						decayRFInduction();
					}
				}
			}
		}
		
		if (heldHeat<=0) {
			decayHeatInduction();
		} else {
			EnumFacing flow = EnumFacing.getFacingFromVector((float)heatInduction.x, (float)heatInduction.y, (float)heatInduction.z);
			
			pushHeatTo(flow, false);
			
			if (heldHeat>0) {
				for(EnumFacing facing : EnumFacing.VALUES) {
					if (facing==flow || facing==flow.getOpposite()) continue;
					pushHeatTo(facing, false);
					if (heldHeat<=0) break; 
				}
			
				if (heldHeat>0) {
					pushHeatTo(flow.getOpposite(), false);
					
					if (heldHeat>0) {
						decayHeatInduction();
					}
				}
			}
		}
	}
	
	public void decayFluidInduction() {
		if (fluidInduction!=Vec3d.ZERO) {
			fluidInduction = fluidInduction.scale(0.8);
			if (fluidInduction.lengthSquared()<=1) fluidInduction = Vec3d.ZERO;
			markDirty();
		}
	}
	
	public void decayRFInduction() {
		if (rfInduction!=Vec3d.ZERO) {
			rfInduction = rfInduction.scale(0.8);
			if (rfInduction.lengthSquared()<=1) rfInduction = Vec3d.ZERO;
			markDirty();
		}
	}
	
	public void decayHeatInduction() {
		if (heatInduction!=Vec3d.ZERO) {
			heatInduction = heatInduction.scale(0.8);
			if (heatInduction.lengthSquared()<=1) heatInduction = Vec3d.ZERO;
			markDirty();
		}
	}
	
	private ItemStack pushTo(EnumFacing facing, boolean simulate) {
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if (te==null || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
			return heldItem;
		}
		
		IItemHandler dest = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
		ItemStack result = ItemHandlerHelper.insertItem(dest, heldItem, simulate);
		if (!simulate && result.getCount()!=heldItem.getCount()) {
			//Induce flow
			fluidInduction = fluidInduction.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()));
			if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
				fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
			}
			
			heldItem = result;
			markDirty();
		}
		
		return result;
	}
	
	private FluidStack pushFluidTo(EnumFacing facing, boolean simulate) {
		if (heldFluid==null) return null;
		
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if (te==null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
			return heldFluid;
		}
		
		IFluidHandler dest = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
		int result = dest.fill(heldFluid, !simulate);
		int remaining = heldFluid.amount - result;
		if (!simulate && result!=0) {
			//Induce flow
			fluidInduction = fluidInduction.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()));
			if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
				fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
			}
			
			heldFluid.amount = remaining;
			if (heldFluid.amount<=0) heldFluid = null;
			markDirty();
			
			return heldFluid;
		}
		
		//Simulate the remaining stack
		FluidStack stack = heldFluid.copy();
		stack.amount = remaining;
		return stack;
	}
	
	private int pushEnergyTo(EnumFacing facing, boolean simulate) {
		if (heldRF==0) return 0;
		
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if (te==null || !te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
			return heldRF;
		}
		
		IEnergyStorage dest = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
		int received = dest.receiveEnergy(heldRF, simulate);
		if (!simulate && received>0) {
			heldRF -= received;
			
			//Induce flow
			rfInduction = rfInduction.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()));
			if (rfInduction.length() > MAX_RF_INDUCTION) {
				rfInduction = rfInduction.normalize().scale(MAX_RF_INDUCTION);
			}
			
			markDirty();
			
			return heldRF;
		}
		
		return heldRF - Math.max(received,0);
	}
	
	private int pushHeatTo(EnumFacing facing, boolean simulate) {
		if (heldHeat==0) return 0;
		
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if (te==null || !te.hasCapability(Thermionics.CAPABILITY_HEATSTORAGE, facing.getOpposite())) {
			return heldHeat;
		}
		
		IHeatStorage dest = te.getCapability(Thermionics.CAPABILITY_HEATSTORAGE, facing.getOpposite());
		int received = dest.receiveHeat(heldHeat, simulate);
		if (!simulate && received>0) {
			heldHeat -= received;
			
			//Induce flow
			heatInduction = heatInduction.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()));
			if (heatInduction.length() > MAX_HEAT_INDUCTION) {
				heatInduction = heatInduction.normalize().scale(MAX_HEAT_INDUCTION);
			}
			
			markDirty();
			
			return heldHeat;
		}
		
		return heldHeat - Math.max(received,0);
	}
	
	private class DuctItemHandler implements IItemHandler {
		private EnumFacing incomingFlow = null;
		
		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if (slot!=0) return ItemStack.EMPTY;
			return heldItem;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			
			if (stack==ItemStack.EMPTY) return ItemStack.EMPTY; //Sure. Nothing in, nothing out.
			
			//Defaults to reject-all
			ItemStack result = (heldItem==ItemStack.EMPTY) ? ItemStack.EMPTY : heldItem.copy();
			ItemStack extra = stack.copy();
			
			
			
			if (result==ItemStack.EMPTY) {
				//Internal buffer was empty, so just plonk it in.
				result = extra;
				extra = ItemStack.EMPTY;
				
				//Fix overstacked input item
				int limit = Math.min(result.getMaxStackSize(), MAX_ITEMS);
				if (result.getCount() > limit) {
					int leftover = result.getCount() - limit;
					extra = ItemHandlerHelper.copyStackWithSize(result, leftover);
					result.setCount(limit);
				}
			} else {
				if (ItemHandlerHelper.canItemStacksStack(result, extra)) {
					int count = result.getCount() + extra.getCount();
					int limit = Math.min(result.getMaxStackSize(), MAX_ITEMS);
					
					if (count > limit) {
						//Move as many as are allowed into the internal stack
						result.setCount(limit);
						extra.setCount(count-limit);
					} else {
						//Move all the things into the internal stack
						result.setCount(count);
						extra = ItemStack.EMPTY;
					}
				} else {
					//reject-all was the appropriate response. Leave it as it is.
				}
			}
			
			if (!simulate) {
				heldItem = result;
				
				//Induce flow
				fluidInduction = fluidInduction.add(new Vec3d(incomingFlow.getXOffset(), incomingFlow.getYOffset(), incomingFlow.getZOffset()));
				if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
					fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
				}
				
				markDirty();
			}
			
			return extra;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot!=0 || amount==0 || heldItem.isEmpty()) return ItemStack.EMPTY;
			
			ItemStack result = ItemHandlerHelper.copyStackWithSize(heldItem, Math.min(heldItem.getCount(), amount));
			if (!simulate) {
				if (result.getCount() < heldItem.getCount()) {
					heldItem.setCount(heldItem.getCount() - result.getCount());
				} else {
					heldItem = ItemStack.EMPTY;
				}
				
				//Induce flow
				EnumFacing outgoingFlow = incomingFlow.getOpposite();
				fluidInduction = fluidInduction.add(new Vec3d(outgoingFlow.getXOffset(), outgoingFlow.getYOffset(), outgoingFlow.getZOffset()));
				if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
					fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
				}
			}
			
			return result;
		}

		@Override
		public int getSlotLimit(int slot) {
			return MAX_ITEMS; //most items set a 64x limit individually, but a mod might like to change certain stack sizes and we support that.
		}
		
	}
	
	private class DuctFluidHandler implements IFluidHandler {
		private EnumFacing incomingFlow = null;
		
		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[] {
					new FluidTankProperties(heldFluid, MAX_FLUID, true, true)
			};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if (resource == null || resource.amount==0) {
				return 0;
			}
			
			int result = 0;
			if (heldFluid==null) {
				//We can accept a tank-full
				result = Math.min(resource.amount, MAX_FLUID);
				if (doFill) {
					heldFluid = resource.copy();
					heldFluid.amount = result;
					
					//Induce flow
					fluidInduction = fluidInduction.add(new Vec3d(incomingFlow.getXOffset(), incomingFlow.getYOffset(), incomingFlow.getZOffset()));
					if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
						fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
					}
					
					markDirty();
				}
			} else {
				//See if we can combine the stacks
				if (heldFluid.getFluid()!=resource.getFluid()) return 0;
				if (!FluidStack.areFluidStackTagsEqual(heldFluid, resource)) return 0;
				
				int newStored = Math.min(resource.amount + heldFluid.amount, MAX_FLUID);
				if (newStored>heldFluid.amount) {
					result = newStored - heldFluid.amount;
					
					if (doFill) {
						heldFluid.amount = newStored;
						
						//Induce flow
						fluidInduction = fluidInduction.add(new Vec3d(incomingFlow.getXOffset(), incomingFlow.getYOffset(), incomingFlow.getZOffset()));
						if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
							fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
						}
						
						markDirty();
					}
				}
			}
			
			return result;
			
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (heldFluid==null) return null;
			if (!heldFluid.isFluidEqual(resource)) return null;
			if (!FluidStack.areFluidStackTagsEqual(heldFluid, resource)) return null;
			
			return drain(resource.amount, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			int toDrain = Math.min(heldFluid.amount, maxDrain);
			if (toDrain<=0) return null;
			
			int newStored = heldFluid.amount - toDrain;
			if (doDrain) {
				if (newStored<=0) {
					heldFluid = null;
				} else {
					heldFluid.amount = newStored;
				}
				
				//Induce flow
				EnumFacing outgoingFlow = incomingFlow.getOpposite();
				fluidInduction = fluidInduction.add(new Vec3d(outgoingFlow.getXOffset(), outgoingFlow.getYOffset(), outgoingFlow.getZOffset()));
				if (fluidInduction.length() > MAX_FLUID_INDUCTION) {
					fluidInduction = fluidInduction.normalize().scale(MAX_FLUID_INDUCTION);
				}
				
				markDirty();
			}
			FluidStack result = heldFluid.copy();
			result.amount = toDrain;
			return result;
		}
	}
	
	public class DuctEnergyHandler implements IEnergyStorage {
		private EnumFacing incomingFlow = EnumFacing.NORTH;
		
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int result = heldRF + maxReceive;
			if (result>MAX_RF) result = MAX_RF;
			if (result<=heldRF) return 0; //no net change
			
			int received = result - heldRF;
			if (!simulate) {
				heldRF = received;
				
				//Induce flow
				rfInduction = rfInduction.add(new Vec3d(incomingFlow.getXOffset(), incomingFlow.getYOffset(), incomingFlow.getZOffset()));
				if (rfInduction.length() > MAX_RF_INDUCTION) {
					rfInduction = rfInduction.normalize().scale(MAX_RF_INDUCTION);
				}
				
				markDirty();
			}
			
			return received;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			int toExtract = Math.min(heldRF, maxExtract);
			if (toExtract<=0) return 0;
			
			int newStored = heldRF - toExtract;
			if (!simulate) {
				heldRF = newStored;
				
				EnumFacing outgoingFlow = incomingFlow.getOpposite();
				rfInduction = rfInduction.add(new Vec3d(outgoingFlow.getXOffset(), outgoingFlow.getYOffset(), outgoingFlow.getZOffset()));
				if (rfInduction.length() > MAX_RF_INDUCTION) {
					rfInduction = rfInduction.normalize().scale(MAX_RF_INDUCTION);
				}
				
				markDirty();
			}
			
			return toExtract;
		}

		@Override
		public int getEnergyStored() {
			return heldRF;
		}

		@Override
		public int getMaxEnergyStored() {
			return MAX_RF;
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
		
	}
	
	public class DuctHeatHandler implements IHeatStorage {
		private EnumFacing incomingFlow = null;
		
		@Override
		public int receiveHeat(int amount, boolean simulate) {
			int result = heldHeat + amount;
			if (result>MAX_HEAT) result = MAX_HEAT;
			if (result<=heldHeat) return 0; //no net change
			
			int received = result - heldHeat;
			if (!simulate) {
				heldHeat = result;
				
				//Induce flow
				heatInduction = heatInduction.add(new Vec3d(incomingFlow.getXOffset(), incomingFlow.getYOffset(), incomingFlow.getZOffset()));
				if (heatInduction.length() > MAX_HEAT_INDUCTION) {
					heatInduction = heatInduction.normalize().scale(MAX_HEAT_INDUCTION);
				}
				
				markDirty();
			}
			
			return received;
		}

		@Override
		public int extractHeat(int amount, boolean simulate) {
			int toExtract = Math.min(heldHeat, amount);
			if (toExtract<=0) return 0;
			
			int newStored = heldHeat - toExtract;
			if (!simulate) {
				heldHeat = newStored;
				
				EnumFacing outgoingFlow = incomingFlow.getOpposite();
				heatInduction = heatInduction.add(new Vec3d(outgoingFlow.getXOffset(), outgoingFlow.getYOffset(), outgoingFlow.getZOffset()));
				if (heatInduction.length() > MAX_HEAT_INDUCTION) {
					heatInduction = heatInduction.normalize().scale(MAX_HEAT_INDUCTION);
				}
				
				markDirty();
			}
			
			return toExtract;
		}
		
		@Override
		public int getHeatStored() {
			return heldHeat;
		}

		@Override
		public int getMaxHeatStored() {
			return MAX_HEAT;
		}

		@Override
		public boolean canReceiveHeat() {
			return true;
		}

		@Override
		public boolean canExtractHeat() {
			return true;
		}
	}
}
