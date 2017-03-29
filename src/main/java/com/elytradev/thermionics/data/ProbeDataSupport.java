/**
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
import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.tileentity.TileEntityCableRF;
import com.elytradev.thermionics.tileentity.TileEntityMachine;
import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ProbeDataSupport {
	public static boolean PROBE_PRESENT = false;
	public static IUnit UNIT_SIGNAL;
	public static IUnit UNIT_ENTHALPY;
	@CapabilityInject(IProbeDataProvider.class)
	public static final Capability<IProbeDataProvider> PROBE_CAPABILITY = null;
	
	public static void init() {
		if (Loader.isModLoaded("probedataprovider")) {
			PROBE_PRESENT = true;
			UNIT_SIGNAL = new SIUnit("Signal", "", 0xFF0066);
			UnitDictionary.getInstance().register(UNIT_SIGNAL);
			UNIT_ENTHALPY = new SIUnit("Enthalpy", "H", 0xFF7700);
			UnitDictionary.getInstance().register(UNIT_ENTHALPY);
		}
	}
	
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
			
			if (machine instanceof IMachineProgress) {
				float progress = ((IMachineProgress)machine).getMachineProgress();
				data.add(new ProbeData()
						.withLabel(new TextComponentTranslation("thermionics.data.progress"))
						.withBar(0, (int)(progress*100), 100, UnitDictionary.PERCENT));
			}
			
			if (machine.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				addInventoryData(machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), data);
			}
			
			//data.add(new ProbeData("MEMES: 9001"));
		}
	}
	
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
				.withBar(0, storage.getHeatStored(), storage.getMaxHeatStored(), UNIT_ENTHALPY)
				);
	}
	
	public static void addInventoryData(IItemHandler storage, List<IProbeData> list) {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		for(int i=0; i<storage.getSlots(); i++) {
			builder.add(storage.getStackInSlot(i));
		}
		list.add(
				new ProbeData().withInventory(builder.build())
				);
	}
	
	//public static void addProgressData(TileEntityMachine machine, List<IProbeData> list) {
	//	
	//}
	
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
					.withBar(0, storage.getSignal(),storage.getMaxSignal(), UNIT_SIGNAL)
					);
		}
	}
}
