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

package com.elytradev.thermionics.data;

import java.util.List;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IProbeDataProvider;
import com.elytradev.probe.api.IUnit;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import com.elytradev.probe.api.impl.SIUnit;
import com.elytradev.probe.api.impl.Unit;
import com.elytradev.thermionics.CapabilityProvider;
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.IRotaryPowerSupply;
import com.elytradev.thermionics.tileentity.TileEntityMachine;
import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ProbeDataSupport {
	public static boolean PROBE_PRESENT = false;
	
	@CapabilityInject(IProbeDataProvider.class)
	public static final Object PROBE_CAPABILITY = null;
	private static Internal internal;
	
	public static void init() {
		if (Loader.isModLoaded("probedataprovider")) {
			internal = new InternalActual();
			PROBE_PRESENT = true;
		} else {
			internal = new InternalDummy();
		}
		
		internal.init();
	}
	
	public static void registerMachineInspector(TileEntityMachine te, CapabilityProvider provider) {
		internal.registerMachineInspector(te,provider);
	}
	
	public static void registerRFInspector(TileEntity te, CapabilityProvider provider) {
		internal.registerRFInspector(te, provider);
	}
	
	private static interface Internal {
		void init();
		void registerMachineInspector(TileEntityMachine te, CapabilityProvider provider);
		void registerRFInspector(TileEntity te, CapabilityProvider provider);
	}
	
	private static class InternalDummy implements Internal {
		@Override
		public void init() {}
		
		@Override
		public void registerMachineInspector(TileEntityMachine te, CapabilityProvider provider) {}

		@Override
		public void registerRFInspector(TileEntity te, CapabilityProvider provider) {}
	}
	
	private static class InternalActual implements Internal {
		public static Object UNIT_SIGNAL;
		public static Object UNIT_ENTHALPY;
		public static Object UNIT_TORQUE;
		
		@Override
		public void init() {
			UNIT_SIGNAL = new SIUnit("Signal", "", 0xFF0066);
			UnitDictionary.getInstance().register((com.elytradev.probe.api.IUnit)UNIT_SIGNAL);
			UNIT_ENTHALPY = new SIUnit("Enthalpy", "H", 0xFF7700);
			UnitDictionary.getInstance().register((com.elytradev.probe.api.IUnit)UNIT_ENTHALPY);
			UNIT_TORQUE = new SIUnit("Torque", "T", 0xFFFF00);
			UnitDictionary.getInstance().register((com.elytradev.probe.api.IUnit)UNIT_TORQUE);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void registerMachineInspector(TileEntityMachine te, CapabilityProvider provider) {
			MachineInspector inspector = new MachineInspector(te);
			provider.registerForAllSides((Capability<Object>)PROBE_CAPABILITY, (()->inspector));
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void registerRFInspector(TileEntity te, CapabilityProvider provider) {
			RFInspector inspector = new RFInspector(te);
			provider.registerForAllSides((Capability<Object>)PROBE_CAPABILITY, (()->inspector));
		}
		
		
		
		@Optional.Interface(modid="probedataprovider", iface="com.elytradev.probe.api.IProbeDataProvider")
		public static class MachineInspector implements IProbeDataProvider {
			private final TileEntityMachine machine;
			
			public MachineInspector(TileEntityMachine machine) {
				this.machine = machine;
			}
			
			@Override
			public void provideProbeData(List<IProbeData> data) {
				if (machine.hasCapability(Thermionics.CAPABILITY_HEATSTORAGE, null)) {
					addHeatData(machine.getCapability(Thermionics.CAPABILITY_HEATSTORAGE, null), data);
				}
				
				if (machine.hasCapability(CapabilityEnergy.ENERGY, null)) {
					addRFData(machine.getCapability(CapabilityEnergy.ENERGY, null), data);
				}
				
				if (machine.hasCapability(Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY, null)) {
					addRotarySupplierData(machine.getCapability(Thermionics.CAPABILITY_ROTARYPOWER_SUPPLY, null), data);
				}
				
				if (machine instanceof IMachineProgress) {
					float progress = ((IMachineProgress)machine).getMachineProgress();
					data.add(new ProbeData()
							.withLabel(new TextComponentTranslation("thermionics.data.progress"))
							.withBar(0, (int)(progress*100), 100, UnitDictionary.PERCENT));
				}
				
				if (machine.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
					addInventoryData(machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), data);
				}
				
				if (machine.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)) {
					addFluidData(machine.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP), data);
				}
				
				//data.add(new ProbeData("MEMES: 9001"));
			}
		}
		
		@Optional.Interface(modid="probedataprovider", iface="com.elytradev.probe.api.IProbeDataProvider")
		public static class RFInspector implements IProbeDataProvider {
			private final TileEntity cable;
			
			public RFInspector(TileEntity cable) {
				this.cable = cable;
			}
			
			@Override
			public void provideProbeData(List<IProbeData> data) {
				addRFData(cable.getCapability(CapabilityEnergy.ENERGY, null), data);
			}
		}
		
		public static void addRFData(IEnergyStorage storage, List<IProbeData> list) {
			list.add(new ProbeData(new TextComponentTranslation("thermionics.data.energystorage"))
					.withBar(0, storage.getEnergyStored(), storage.getMaxEnergyStored(), UnitDictionary.FORGE_ENERGY));
			if (storage instanceof ITransferRate) {
				ITransferRate o = (ITransferRate) storage;
				
				list.add(new ProbeData(new TextComponentTranslation("thermionics.data.energystorage.maxtransfer"))
						.withBar(0, o.getCurTransfer(), o.getMaxTransfer(), UnitDictionary.FU_PER_TICK));
			}
		}
		
		public static void addHeatData(IHeatStorage storage, List<IProbeData> list) {
			list.add(new ProbeData(new TextComponentTranslation("thermionics.data.heatstorage"))
					.withBar(0, storage.getHeatStored(), storage.getMaxHeatStored(), (com.elytradev.probe.api.IUnit)UNIT_ENTHALPY)
					);
		}
		
		public static void addInventoryData(IItemHandler storage, List<IProbeData> list) {
			
			if (storage.getSlots()==10) {
				//Special case: This is a serger-like grid recipe machine!
				ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
				for(int i=0; i<9; i++) builder.add(storage.getStackInSlot(i));
				list.add(new ProbeData().withInventory(builder.build()));
				
				list.add(new ProbeData().withInventory(ImmutableList.of(storage.getStackInSlot(9))));
				
				return;
			}
			ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
			for(int i=0; i<storage.getSlots(); i++) {
				builder.add(storage.getStackInSlot(i));
			}
			list.add(
					new ProbeData().withInventory(builder.build())
					);
		}
		
		public static void addRotarySupplierData(IRotaryPowerSupply storage, List<IProbeData> list) {
			list.add(new ProbeData(new TextComponentTranslation("thermionics.data.torque"))
					.withBar(0, storage.getTorqueSetting(), storage.getMaxBufferedPower(), (com.elytradev.probe.api.IUnit)UNIT_TORQUE)
					);
		}
		
		public static void addFluidData(IFluidHandler storage, List<IProbeData> list) {
			for(IFluidTankProperties info : storage.getTankProperties()) {
				FluidStack cur = info.getContents();
				int capacity = info.getCapacity();
				if (cur!=null) {
					IUnit unit = UnitDictionary.getInstance().getUnit(info.getContents().getFluid());
					list.add(new ProbeData().withBar(0, cur.amount, capacity, unit));
				} else {
					IUnit unit = UnitDictionary.BUCKETS_ANY;
					list.add(new ProbeData().withBar(0, 0, capacity, unit));
				}
			}
		}
		
		/*
		public static class BandwidthHandler implements IProbeDataProvider {
			private ObservableEnergyStorage storage;
			public BandwidthHandler(ObservableEnergyStorage storage) {
				this.storage = storage;
			}
			
			@Override
			public void provideProbeData(List<IProbeData> data) {
				data.add(new ProbeData()
						.withLabel(new TextComponentTranslation("data.energystorage.maxtransfer").appendSibling(new TextComponentString(" "+storage.getMaxTransfer())))
						);
			}
		}
		
		public static class SignalHandler implements IProbeDataProvider {
			private ISignalStorage storage;
			
			public SignalHandler(ISignalStorage storage) {
				this.storage = storage;
			}
			
			@Override
			public void provideProbeData(List<IProbeData> data) {
				data.add(new ProbeData()
						.withBar(0, storage.getSignal(),storage.getMaxSignal(), (com.elytradev.probe.api.IUnit)UNIT_SIGNAL)
						);
			}
		}*/
	}
}
