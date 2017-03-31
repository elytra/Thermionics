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
import com.elytradev.thermionics.api.IRotaryPower;
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.api.impl.DefaultHeatStorageSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryPowerSerializer;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.RotaryPower;
import com.elytradev.thermionics.block.BlockAxle;
import com.elytradev.thermionics.block.BlockBase;
import com.elytradev.thermionics.block.BlockBattery;
import com.elytradev.thermionics.block.BlockBatteryCreative;
import com.elytradev.thermionics.block.BlockCableRF;
import com.elytradev.thermionics.block.BlockConvectionMotor;
import com.elytradev.thermionics.block.BlockDrum;
import com.elytradev.thermionics.block.BlockFirebox;
import com.elytradev.thermionics.block.BlockGearbox;
import com.elytradev.thermionics.block.BlockHeatPipe;
import com.elytradev.thermionics.block.BlockOven;
import com.elytradev.thermionics.block.BlockRoad;
import com.elytradev.thermionics.block.BlockScaffold;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.item.ItemBlockBattery;
import com.elytradev.thermionics.item.ItemBlockEquivalentState;
import com.elytradev.thermionics.tileentity.TileEntityBattery;
import com.elytradev.thermionics.tileentity.TileEntityBatteryCreative;
import com.elytradev.thermionics.tileentity.TileEntityCableRF;
import com.elytradev.thermionics.tileentity.TileEntityConvectionMotor;
import com.elytradev.thermionics.tileentity.TileEntityDrum;
import com.elytradev.thermionics.tileentity.TileEntityFirebox;
import com.elytradev.thermionics.tileentity.TileEntityOven;
import com.elytradev.thermionics.tileentity.TileEntityCableHeat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid=Thermionics.MODID, version="@VERSION@", name="Thermionics|Core")
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
	@CapabilityInject(IRotaryPower.class)
	public static Capability<IRotaryPower> CAPABILITY_ROTARYPOWER;
	@CapabilityInject(ISignalStorage.class)
	public static Capability<ISignalStorage> CAPABILITY_SIGNALSTORAGE;
	
	public static Potion POTION_EFFORTLESS_SPEED;
	
	public static CreativeTabs TAB_THERMIONICS = new CreativeTabs("thermionics") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ThermionicsBlocks.FIREBOX);
		}
	};
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		LOG = LogManager.getLogger(Thermionics.MODID);
		
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new DefaultHeatStorageSerializer(), HeatStorage::new);
		CapabilityManager.INSTANCE.register(IRotaryPower.class, new DefaultRotaryPowerSerializer(), RotaryPower::new);
		
		ProbeDataSupport.init();
		
		//Locomotion
		registerBlock(new BlockScaffold("basic"));
		registerBlock(new BlockRoad());
		
		//RF
		registerBlock(new BlockCableRF("rf"));
		
		BlockBattery leadBattery = new BlockBattery("lead");
		registerBlockAndItem(leadBattery, new ItemBlockBattery(leadBattery));
		BlockBatteryCreative creativeBattery = new BlockBatteryCreative();
		registerBlockAndItem(creativeBattery, new ItemBlockBattery(creativeBattery));
		
		//Fluid Handlers
		registerBlock(new BlockDrum());
		
		//Heat
		registerBlock(new BlockFirebox());
		registerBlock(new BlockHeatPipe());
		registerBlock(new BlockOven());
		registerBlock(new BlockConvectionMotor());
		
		//Rotary
		BlockAxle woodAxle = new BlockAxle(Material.WOOD, "wood");
		woodAxle.setHardness(1.0f).setHarvestLevel("axe", 0);
		registerBlock(woodAxle);
		registerBlock(new BlockAxle(Material.IRON, "iron"));
		registerBlock(new BlockGearbox());
		//registerBlock(new BlockMotorBase("redstone"));

		GameRegistry.registerTileEntity(TileEntityCableRF.class,         "thermionics:cable");
		GameRegistry.registerTileEntity(TileEntityBattery.class,         "thermionics:battery.lead");
		GameRegistry.registerTileEntity(TileEntityBatteryCreative.class, "thermionics:battery.creative");
		GameRegistry.registerTileEntity(TileEntityDrum.class,            "thermionics:drum");
		GameRegistry.registerTileEntity(TileEntityFirebox.class,         "thermionics:machine.firebox");
		GameRegistry.registerTileEntity(TileEntityOven.class,            "thermionics:machine.oven");
		GameRegistry.registerTileEntity(TileEntityCableHeat.class,       "thermionics:cable.heat");
		GameRegistry.registerTileEntity(TileEntityConvectionMotor.class, "thermionics:machine.convectionmotor");
		//GameRegistry.registerTileEntity(TileEntityCableSignal.class, "thermionics:cable.redstone");
		
		
		POTION_EFFORTLESS_SPEED = new PotionEffortlessSpeed();
		Potion.REGISTRY.register(0, new ResourceLocation("thermionics","effortless_speed"), POTION_EFFORTLESS_SPEED);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.CABLE_RF,8),
				"wlw", 'w', new ItemStack(Blocks.WOOL,1,OreDictionary.WILDCARD_VALUE), 'l', "ingotLead"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.SCAFFOLD_BASIC,4),
				"x x", " x ", "x x", 'x', "ingotIron"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.FIREBOX,1),
				"xxx", "x x", "xxx", 'x', "ingotIron"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.OVEN,1),
				"xxx", "x x", "xcx", 'x', "ingotIron", 'c', "ingotCopper"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.BATTERY_LEAD,1),
				" c ", "pLp", " r ", 'L', "blockLead", 'c', "ingotCopper", 'r', new ItemStack(Items.REDSTONE), 'p', new ItemStack(Items.PAPER)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.AXLE_WOOD,4),
				"w", "w", "w", 'w', new ItemStack(Blocks.PLANKS)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.AXLE_IRON,4),
				"i", "i", "i", 'i', "ingotIron"
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.CABLE_HEAT,4),
				"c", "c", "c", 'c', "ingotCopper"
				));
		
		GameRegistry.addSmelting(Blocks.GRAVEL, new ItemStack(ThermionicsBlocks.ROAD), 0);
		
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.FIREBOX.getRegistryName());
	}
	
	public static Thermionics instance() {
		return instance;
	}
	
	public void registerBlock(BlockBase block) {
		GameRegistry.register(block);
		ItemBlockEquivalentState itemBlock = new ItemBlockEquivalentState(block);
		GameRegistry.register(itemBlock);
		proxy.registerItemModel(itemBlock);
	}
	
	public void registerBlock(Block block) {
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		
		GameRegistry.register(block);
		GameRegistry.register(item);
		proxy.registerItemModel(item);
	}
	
	public void registerBlockAndItem(BlockBase block, Item item) {
		GameRegistry.register(block);
		GameRegistry.register(item);
		proxy.registerItemModel(item);
	}
	
	@SubscribeEvent
	public void updateFOV(FOVUpdateEvent event) {
		if (event.getEntity().getActivePotionEffect(Thermionics.POTION_EFFORTLESS_SPEED)!=null) {
			event.setNewfov(1.0f);
		} else {
			if (event.getFov() > event.getNewfov()) event.setNewfov(1.0f);
			
		}
	}
}
