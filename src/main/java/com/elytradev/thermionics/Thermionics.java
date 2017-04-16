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

import java.time.LocalDateTime;
import java.time.Month;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.concrete.client.gui.ConcreteGui;
import com.elytradev.concrete.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.thermionics.api.HammerMillRecipes;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.IRotaryPowerConsumer;
import com.elytradev.thermionics.api.IRotaryPowerSupply;
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.api.impl.DefaultHeatStorageSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryConsumerSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryPowerSerializer;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.RotaryOreRecipe;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.api.impl.RotaryPowerSupply;
import com.elytradev.thermionics.api.impl.RotaryRecipe;
import com.elytradev.thermionics.block.BlockAxle;
import com.elytradev.thermionics.block.BlockBase;
import com.elytradev.thermionics.block.BlockBattery;
import com.elytradev.thermionics.block.BlockBatteryCreative;
import com.elytradev.thermionics.block.BlockCableRF;
import com.elytradev.thermionics.block.BlockConvectionMotor;
import com.elytradev.thermionics.block.BlockDrum;
import com.elytradev.thermionics.block.BlockFirebox;
import com.elytradev.thermionics.block.BlockGearbox;
import com.elytradev.thermionics.block.BlockHammerMill;
import com.elytradev.thermionics.block.BlockHeatPipe;
import com.elytradev.thermionics.block.BlockOven;
import com.elytradev.thermionics.block.BlockRoad;
import com.elytradev.thermionics.block.BlockScaffold;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.EnumDyeSource;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.gui.ContainerFirebox;
import com.elytradev.thermionics.gui.ContainerHammerMill;
import com.elytradev.thermionics.gui.ContainerMotor;
import com.elytradev.thermionics.gui.ContainerOven;
import com.elytradev.thermionics.item.ItemBlockBattery;
import com.elytradev.thermionics.item.ItemBlockEquivalentState;
import com.elytradev.thermionics.item.ItemHammer;
import com.elytradev.thermionics.item.ThermionicsItems;
import com.elytradev.thermionics.tileentity.TileEntityBattery;
import com.elytradev.thermionics.tileentity.TileEntityBatteryCreative;
import com.elytradev.thermionics.tileentity.TileEntityCableRF;
import com.elytradev.thermionics.tileentity.TileEntityConvectionMotor;
import com.elytradev.thermionics.tileentity.TileEntityDrum;
import com.elytradev.thermionics.tileentity.TileEntityFirebox;
import com.elytradev.thermionics.tileentity.TileEntityHammerMill;
import com.elytradev.thermionics.tileentity.TileEntityOven;
import com.elytradev.thermionics.tileentity.TileEntityCableHeat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
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
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
	@CapabilityInject(IRotaryPowerSupply.class)
	public static Capability<IRotaryPowerSupply> CAPABILITY_ROTARYPOWER_SUPPLY;
	@CapabilityInject(IRotaryPowerConsumer.class)
	public static Capability<IRotaryPowerConsumer> CAPABILITY_ROTARYPOWER_CONSUMER;
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
		CONFIG = new Configuration(e.getSuggestedConfigurationFile());
		
		LOG = LogManager.getLogger(Thermionics.MODID);
		
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new DefaultHeatStorageSerializer(), HeatStorage::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerSupply.class, new DefaultRotaryPowerSerializer(), RotaryPowerSupply::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerConsumer.class, new DefaultRotaryConsumerSerializer(), RotaryPowerConsumer::new);
		
		ProbeDataSupport.init();
		
		//Since we can't rely on the oredict for toolmaterials...
		//PS, since these are enum constants, fully-capitalized is the correct Oracle convention.
		/*                                                     name    level  uses   eff   dmg  enchant */
		/*                                                     "iron"      2,  250,   6f,   2f,     14 */
		//ToolMaterial toolCopper = EnumHelper.addToolMaterial("COPPER",     2,  200,   7f,   1f,     20);
		//ToolMaterial toolLead   = EnumHelper.addToolMaterial("LEAD",       2,  250,   5f,   2f,     14);
		
		//Hammertime!
		
		
		
		
		
		registerItem(new ItemHammer(ToolMaterial.IRON, "iron"));
		registerItem(new ItemHammer(ToolMaterial.GOLD, "gold"));
		registerItem(new ItemHammer(ToolMaterial.DIAMOND, "diamond"));
		
		//Since we can't *safely*, *cleanly* rendezvous with other mods about Item.ToolMaterial properties, make our own
		                           /*name      repairOre     level  uses eff dmg ench */
		                           /*iron      ingotIron     2      250  6f  2f  14   */
		registerItem(new ItemHammer("copper", "ingotCopper", 2,     200, 7f, 1f, 20));
		registerItem(new ItemHammer("lead",   "ingotLead",   2,    1550, 4f, 2f, 8)); //Some specialcasing here, lead is durable but stone harvest speed and hard to enchant
		registerItem(new ItemHammer("invar",  "ingotInvar",  2,     300, 6f, 2f, 14));
		
		//Locomotion
		registerBlock(new BlockScaffold("basic"));
		registerBlock(new BlockRoad(0));
		registerBlock(new BlockRoad(1));
		
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
		registerBlock(new BlockHammerMill());
		//registerBlock(new BlockMotorBase("redstone"));

		GameRegistry.registerTileEntity(TileEntityCableRF.class,         "thermionics:cable");
		GameRegistry.registerTileEntity(TileEntityBattery.class,         "thermionics:battery.lead");
		GameRegistry.registerTileEntity(TileEntityBatteryCreative.class, "thermionics:battery.creative");
		GameRegistry.registerTileEntity(TileEntityDrum.class,            "thermionics:drum");
		GameRegistry.registerTileEntity(TileEntityFirebox.class,         "thermionics:machine.firebox");
		GameRegistry.registerTileEntity(TileEntityOven.class,            "thermionics:machine.oven");
		GameRegistry.registerTileEntity(TileEntityCableHeat.class,       "thermionics:cable.heat");
		GameRegistry.registerTileEntity(TileEntityConvectionMotor.class, "thermionics:machine.convectionmotor");
		GameRegistry.registerTileEntity(TileEntityHammerMill.class,      "thermionics:machine.hammermill");
		//GameRegistry.registerTileEntity(TileEntityCableSignal.class, "thermionics:cable.redstone");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {
			private ConcreteContainer getContainer(int id, IInventory player, IInventory tile) {
				switch(id) {
				case ContainerFirebox.ID:
				default:
					return new ContainerFirebox(player, tile);
				case ContainerOven.ID:
					return new ContainerOven(player, tile);
				case ContainerMotor.ID:
					return new ContainerMotor(player, tile);
				case ContainerHammerMill.ID:
					return new ContainerHammerMill(player, tile);
				}
			}
			
			@Override
			public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
				
				if (te!=null && (te instanceof IContainerInventoryHolder)) {
					ConcreteContainer container = getContainer(id, player.inventory, ((IContainerInventoryHolder)te).getContainerInventory());
					container.validate();
					return container;
				}
				
				System.out.println("NULL SERVER ELEMENT.");
				return null; //For now!
			}

			@Override
			public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
				ConcreteContainer container = null;
				if (te!=null && (te instanceof IContainerInventoryHolder)) {
					container = getContainer(id, player.inventory, ((IContainerInventoryHolder)te).getContainerInventory());
				}
				
				return new ConcreteGui(container);
			}
			
		});
		POTION_EFFORTLESS_SPEED = new PotionExpedience();
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.MOTOR_CONVECTION),
				"IcI", "ctc", "IsI",
				'I', "blockIron",
				'c', "ingotCopper",
				't', new ItemStack(Blocks.REDSTONE_TORCH),
				's', "ingotSilver"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.GEARBOX),
				"IiI", "i i", "IsI", 'I', "blockIron", 'i', "ingotIron", 's', "ingotSilver"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsBlocks.HAMMER_MILL),
				"IiI", "ifi", "IsI", 'I', "blockIron", 'i', "ingotIron", 's', "ingotSilver", 'f', new ItemStack(Items.FLINT)));
		
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
		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_IRON,1),
				"I", "s", "s", 'I', "blockIron", 's', "stickWood"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_GOLD,1),
				"I", "s", "s", 'I', "blockGold", 's', "stickWood"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_DIAMOND,1),
				"I", "s", "s", 'I', "blockDiamond", 's', "stickWood"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_COPPER,1),
				"I", "s", "s", 'I', "blockCopper", 's', "stickWood"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_LEAD,1),
				"I", "s", "s", 'I', "blockLead", 's', "stickWood"
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ThermionicsItems.HAMMER_INVAR,1),
				"I", "s", "s", 'I', "blockInvar", 's', "stickWood"
				));
		
		GameRegistry.addSmelting(Blocks.GRAVEL, new ItemStack(ThermionicsBlocks.ROAD), 0);
		
		/*
		NonNullList<ItemStack> variants = NonNullList.create();
		ThermionicsBlocks.ROAD.getSubBlocks(ItemBlock.getItemFromBlock(ThermionicsBlocks.ROAD), Thermionics.TAB_THERMIONICS, variants);
		ItemStack prev = variants.remove(0);
		for(ItemStack item : variants) {
			GameRegistry.addShapelessRecipe(item.copy(), prev.copy());
			prev = item;
		}*/
		registerCraftingCircle(ThermionicsBlocks.ROAD);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ThermionicsBlocks.ROAD_COMPRESSED),
				"xxx", "xgx", "xxx",
				'x', new ItemStack(ThermionicsBlocks.ROAD, 1, OreDictionary.WILDCARD_VALUE),
				'g', "ingotGold"));
		
		//Create a second list for compressed road and make the crafting cycle again
		registerCraftingCircle(ThermionicsBlocks.ROAD_COMPRESSED);
		
		registerMillRecipes("Iron");
		registerMillRecipes("Copper");
		registerMillRecipes("Lead");
		registerMillRecipes("Nickel");
		registerMillRecipes("Gold");
		registerMillRecipes("Silver");
		registerMillRecipes("Tin");
		registerMillRecipes("Zinc");
		
		registerMillRecipe("oreCoal", new ItemStack(Items.COAL,2), 8f, 20f);
		
		for(EnumDyeSource dyeSource : EnumDyeSource.values()) {
			HammerMillRecipes.registerRecipe(new RotaryRecipe(dyeSource.getExemplar(), dyeSource.createOutputStack(), 2f, 20f)); 
		}
		//for(EnumDyeColor col : EnumDyeColor.values()) {
		//}
		
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
	
	public void registerItem(Item item) {
		GameRegistry.register(item);
		proxy.registerItemModel(item);
	}
	
	public void registerCraftingCircle(BlockBase block) {
		NonNullList<ItemStack> variants = NonNullList.create();
		block.getVariants(ItemBlock.getItemFromBlock(block), variants);
		ItemStack first = variants.remove(0);
		ItemStack prev = first;
		for(ItemStack item : variants) {
			GameRegistry.addShapelessRecipe(item.copy(), prev.copy());
			prev = item;
		}
		GameRegistry.addShapelessRecipe(first.copy(), variants.get(variants.size()-1).copy());
	}
	
	public void registerMillRecipes(String key) {
		if (OreDictionary.doesOreNameExist("dust"+key)) {
			NonNullList<ItemStack> dusts = OreDictionary.getOres("dust"+key);
			ItemStack oneDust = dusts.get(0).copy();
			ItemStack twoDust = oneDust.copy(); twoDust.setCount(2);
			HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ore"+key, twoDust, 10f, 30f));
			HammerMillRecipes.registerRecipe(new RotaryOreRecipe("ingot"+key, oneDust, 10f, 10f));
		}
	}
	
	public void registerMillRecipe(String key, ItemStack result, float torque, float rotations) {
		HammerMillRecipes.registerRecipe(new RotaryOreRecipe(key, result, torque, rotations));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void updateFOV(FOVUpdateEvent event) {
		if (event.getEntity().getActivePotionEffect(Thermionics.POTION_EFFORTLESS_SPEED)!=null) {
			event.setNewfov(1.0f);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onFovModifier(EntityViewRenderEvent.FOVModifier event) {
		if (event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase)event.getEntity();
			if (living.getActivePotionEffect(Thermionics.POTION_EFFORTLESS_SPEED)!=null) {
				event.setFOV( Minecraft.getMinecraft().gameSettings.fovSetting );
				
				//event.setFOV(1.0f);
			}
		}
	}
	
	public static boolean isAprilFools() {
		LocalDateTime now = LocalDateTime.now();
		return now.getDayOfMonth()==1 && now.getMonth()==Month.APRIL;
	}
}
