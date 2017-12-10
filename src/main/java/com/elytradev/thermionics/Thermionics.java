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

package com.elytradev.thermionics;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.libasplod.BigExplosionHandler;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.IRotaryPowerConsumer;
import com.elytradev.thermionics.api.IRotaryPowerSupply;
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.api.impl.DefaultHeatStorageSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryConsumerSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryPowerSerializer;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.api.impl.RotaryPowerSupply;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.data.ProbeDataSupport;
import com.elytradev.thermionics.gui.EnumGui;
import com.elytradev.thermionics.item.ThermionicsItems;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid=Thermionics.MODID, version="@VERSION@", name="Thermionics|Core")
public class Thermionics {
	public static final String MODID = "thermionics";
	public static Logger LOG;
	public static Configuration CONFIG;
	public static boolean CONFIG_ENFORCE_COMPATIBILITY = true;
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
	
	public List<Block> needItemRegistration = new ArrayList<>();
	public List<Item> needModelRegistration = new ArrayList<>();

	
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		
		CONFIG = new Configuration(e.getSuggestedConfigurationFile());
		CONFIG.get("regisration", "register-compatibility-blocks", CONFIG_ENFORCE_COMPATIBILITY);
		
		LOG = e.getModLog();
		//LOG = LogManager.getLogger(Thermionics.MODID);
		
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new DefaultHeatStorageSerializer(), HeatStorage::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerSupply.class, new DefaultRotaryPowerSerializer(), RotaryPowerSupply::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerConsumer.class, new DefaultRotaryConsumerSerializer(), RotaryPowerConsumer::new);
		
		proxy.preInit();
		
		
		ProbeDataSupport.init();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(proxy);
		
		/* (See https://mcforge.readthedocs.io/en/latest/events/intro/ for docs on static event handlers)
		 * Because the registry events are forced on us, we might as well delegate to the former ObjectHolder classes
		 * so they can flow in a more Enum-y way.
		 */
		MinecraftForge.EVENT_BUS.register(ThermionicsBlocks.class);
		MinecraftForge.EVENT_BUS.register(ThermionicsItems.class);
		MinecraftForge.EVENT_BUS.register(ThermionicsRecipes.class);
		
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {
			@Override
			public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
				
				if (te!=null && (te instanceof IContainerInventoryHolder)) {
					ConcreteContainer container = EnumGui.forId(id).createContainer(
							player.inventory,
							((IContainerInventoryHolder)te).getContainerInventory(),
							te);
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
					container = EnumGui.forId(id).createContainer(
							player.inventory,
							((IContainerInventoryHolder)te).getContainerInventory(),
							te);
				}
				
				return new ConcreteGui(container);
			}
			
		});
		
		MinecraftForge.EVENT_BUS.register(this);
		BigExplosionHandler.instance().init();
	}
	
	@SubscribeEvent
	public void onRegisterPotions(RegistryEvent.Register<Potion> event) {
		IForgeRegistry<Potion> r = event.getRegistry();
		
		POTION_EFFORTLESS_SPEED = potion(r, new PotionExpedience());
	}
	
	public static <T extends Potion> T potion(IForgeRegistry<Potion> registry, T t) {
		registry.register(t);
		return t;
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		//TODO: When smores has ore voting stabilized, update and re-enable this
		//NBTTagCompound oresTag = new NBTTagCompound();
		//oresTag.setBoolean("oreCopper", true);
		//oresTag.setBoolean("ingotCopper", true);
		//oresTag.setBoolean("dustCopper", true);
		//oresTag.setBoolean("gearCopper", true);
		//oresTag.setBoolean("plateCopper", true);
		//FMLInterModComms.sendMessage("smores", "recipeVote", oresTag);
		
		//Thermionics machines are *unusually* safe for charset to carry around
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.FIREBOX.getRegistryName());
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.OVEN.getRegistryName());
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.BATTERY_LEAD.getRegistryName()); //Avoid creative battery carry!
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.HAMMER_MILL.getRegistryName());
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.MOTOR_CONVECTION.getRegistryName());
		FMLInterModComms.sendMessage("charset", "addCarry", ThermionicsBlocks.SERGER.getRegistryName());
	}
	
	public static Thermionics instance() {
		return instance;
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
			}
		}
	}
	
	public static boolean isAprilFools() {
		LocalDateTime now = LocalDateTime.now();
		return now.getDayOfMonth()==1 && now.getMonth()==Month.APRIL;
	}
}
