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
package com.elytradev.thermionics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.impl.DefaultHeatStorageSerializer;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.block.BlockFirebox;
import com.elytradev.thermionics.block.BlockHeatPipe;
import com.elytradev.thermionics.block.BlockMotorBase;
import com.elytradev.thermionics.tileentity.TileEntityFirebox;
import com.elytradev.thermionics.tileentity.TileEntityHeatStorage;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=Thermionics.MODID, version="@VERSION@")
public class Thermionics {
	public static final String MODID = "thermionics";
	public static Logger LOG;
	public static Configuration CONFIG;
	@Instance(MODID)
	private static Thermionics instance;
	@SidedProxy(clientSide="com.elytradev.thermionics.ClientProxy", serverSide="com.elytradev.thermionics.Proxy")
	public static Proxy proxy;
	@CapabilityInject(IHeatStorage.class)
	public static Capability<IHeatStorage> CAPABILITY_HEATSTORAGE;
	
	public static CreativeTabs TAB_THERMIONICS = new CreativeTabs("thermionics") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Blocks.IRON_BLOCK); //TODO: Replace with a Thermionics block
		}
	};
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		LOG = LogManager.getLogger(Thermionics.MODID);
		
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new DefaultHeatStorageSerializer(), HeatStorage::new);
		
		registerBlock(new BlockFirebox());
		registerBlock(new BlockHeatPipe());
		registerBlock(new BlockMotorBase("redstone"));
		
		GameRegistry.registerTileEntity(TileEntityHeatStorage.class, "thermionics:machine.heatstorage");
		GameRegistry.registerTileEntity(TileEntityFirebox.class,     "thermionics:machine.firebox");
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		
	}
	
	public static Thermionics instance() {
		return instance;
	}
	
	
	
	public void registerBlock(Block block) {
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		
		GameRegistry.register(block);
		GameRegistry.register(item);
		proxy.registerItemModel(item);
	}
}
