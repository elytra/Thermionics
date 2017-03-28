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
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.tileentity.TileEntityCableRF;
import com.elytradev.thermionics.tileentity.TileEntityMachine;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

public class ProbeDataSupport {
	public static boolean PROBE_PRESENT = false;
	public static IUnit UNIT_SIGNAL;
	@CapabilityInject(IProbeDataProvider.class)
	public static final Capability<IProbeDataProvider> PROBE_CAPABILITY = null;
	
	public static void init() {
		if (Loader.isModLoaded("probedataprovider")) {
			PROBE_PRESENT = true;
			UNIT_SIGNAL = new SIUnit("Signal", "", 0xFFCCCC);
			UnitDictionary.getInstance().register(UNIT_SIGNAL);
		}
	}
	
	public static class MachineInspector implements IProbeDataProvider {
		private final TileEntityMachine machine;
		
		public MachineInspector(TileEntityMachine machine) {
			this.machine = machine;
		}
		
		@Override
		public void provideProbeData(List<IProbeData> data) {
			data.add(new ProbeData("MEMES: 9001"));
		}
	}
	
	public static class RFCableInspector implements IProbeDataProvider {
		private final TileEntityCableRF cable;
		
		public RFCableInspector(TileEntityCableRF cable) {
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
