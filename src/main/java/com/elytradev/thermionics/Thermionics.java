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
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.libasplod.BigExplosionHandler;
import com.elytradev.thermionics.api.IHeatStorage;
import com.elytradev.thermionics.api.IRotaryPowerConsumer;
import com.elytradev.thermionics.api.IRotaryPowerSupply;
import com.elytradev.thermionics.api.ISignalStorage;
import com.elytradev.thermionics.api.IWeaponSkillInfo;
import com.elytradev.thermionics.api.impl.DefaultHeatStorageSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryConsumerSerializer;
import com.elytradev.thermionics.api.impl.DefaultRotaryPowerSerializer;
import com.elytradev.thermionics.api.impl.DefaultWeaponSkillInfoSerializer;
import com.elytradev.thermionics.api.impl.HeatStorage;
import com.elytradev.thermionics.api.impl.ISkillActivating;
import com.elytradev.thermionics.api.impl.RotaryPowerConsumer;
import com.elytradev.thermionics.api.impl.RotaryPowerSupply;
import com.elytradev.thermionics.api.impl.WeaponSkillInfo;
import com.elytradev.thermionics.block.ThermionicsBlocks;
import com.elytradev.thermionics.compat.ChiselCompat;
import com.elytradev.thermionics.compat.ProbeDataSupport;
import com.elytradev.thermionics.gui.EnumGui;
import com.elytradev.thermionics.item.ThermionicsItems;
import com.elytradev.thermionics.network.SpawnParticleEmitterMessage;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid=Thermionics.MODID, version="@VERSION@", name="Thermionics|Core")
public class Thermionics {
	public static final String MODID = "thermionics";
	public static Logger LOG;
	public static Configuration CONFIG;
	//public static boolean CONFIG_ENFORCE_COMPATIBILITY = true;
	public static final SoundEvent SOUNDEVENT_SMAAAAAAASH  = new SoundEvent(new ResourceLocation("thermionics","smash")).setRegistryName("smash");
	@Instance(MODID)
	private static Thermionics instance;
	@SidedProxy(clientSide="com.elytradev.thermionics.client.ClientProxy", serverSide="com.elytradev.thermionics.Proxy")
	public static Proxy proxy;
	@CapabilityInject(IHeatStorage.class)
	public static Capability<IHeatStorage> CAPABILITY_HEATSTORAGE;
	@CapabilityInject(IRotaryPowerSupply.class)
	public static Capability<IRotaryPowerSupply> CAPABILITY_ROTARYPOWER_SUPPLY;
	@CapabilityInject(IRotaryPowerConsumer.class)
	public static Capability<IRotaryPowerConsumer> CAPABILITY_ROTARYPOWER_CONSUMER;
	@CapabilityInject(ISignalStorage.class)
	public static Capability<ISignalStorage> CAPABILITY_SIGNALSTORAGE;
	@CapabilityInject(IWeaponSkillInfo.class)
	public static Capability<IWeaponSkillInfo> CAPABILITY_WEAPONSKILL;
	
	public static Potion POTION_EFFORTLESS_SPEED;
	public static Potion POTION_TIPSY;
	
	public static CreativeTabs TAB_THERMIONICS = new CreativeTabs("thermionics") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ThermionicsBlocks.FIREBOX);
		}
	};
	
	public static NetworkContext CONTEXT;
	
	public List<Block> needItemRegistration = new ArrayList<>();
	public List<Item> needModelRegistration = new ArrayList<>();
	
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		//Skip for now; we have no keys yet
		//CONFIG = new Configuration(e.getSuggestedConfigurationFile());
		//CONFIG.save();
		
		LOG = e.getModLog();
		//LOG = LogManager.getLogger(Thermionics.MODID);
		
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new DefaultHeatStorageSerializer(), HeatStorage::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerSupply.class, new DefaultRotaryPowerSerializer(), RotaryPowerSupply::new);
		CapabilityManager.INSTANCE.register(IRotaryPowerConsumer.class, new DefaultRotaryConsumerSerializer(), RotaryPowerConsumer::new);
		CapabilityManager.INSTANCE.register(IWeaponSkillInfo.class, new DefaultWeaponSkillInfoSerializer(), WeaponSkillInfo::new);
		
		proxy.preInit();
		
		CONTEXT = NetworkContext.forChannel("tmxfx");
		CONTEXT.register(SpawnParticleEmitterMessage.class);
		
		if (Loader.isModLoaded("probedataprovider")) {
			ProbeDataSupport.init();
		}
		
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
		POTION_TIPSY = potion(r, new PotionTipsy());
	}
	
	public static <T extends Potion> T potion(IForgeRegistry<Potion> registry, T t) {
		registry.register(t);
		return t;
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e) {
		proxy.init();
		
		ChiselCompat.init(); //Doesn't need Loader check, only uses IMC
		
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
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.postInit();
	}
	
	public static Thermionics instance() {
		return instance;
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onEntityAttack(LivingAttackEvent e) {
		if (e.getEntity().getEntityWorld().isRemote) return;
		
		Entity attacker = e.getSource().getTrueSource();
		
		if (attacker==null) return; //It's probably just some bat dying in lava
		if (attacker.hasCapability(CAPABILITY_WEAPONSKILL, EnumFacing.UP)) {
			IWeaponSkillInfo skillInfo = attacker.getCapability(CAPABILITY_WEAPONSKILL, EnumFacing.UP);
			if (skillInfo.getCooldown()<=0) {
				//Search for activations
				if (attacker instanceof EntityLivingBase) {
					
					//Tool activations
					if (e.getSource().damageType.equals("player")) {
						ItemStack weapon = ((EntityLivingBase)attacker).getHeldItem(((EntityLivingBase) attacker).getActiveHand());
						if (weapon.getItem() instanceof ISkillActivating) {
							int activated = ((ISkillActivating) weapon.getItem()).activateSkill(skillInfo, (EntityLivingBase)attacker, weapon, e.getSource(), (EntityLiving) e.getEntityLiving());
							if (activated>0) {
								// TODO: Stats? Advancements?
								skillInfo.setCooldown(activated);
							}
						}
					}
				}
			}
		}
		
		//TODO: Check the defender for armor WeaponSkills
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		if (entity.hasCapability(CAPABILITY_WEAPONSKILL, null)) {
			IWeaponSkillInfo skillInfo = entity.getCapability(CAPABILITY_WEAPONSKILL, null);
			skillInfo.setCooldown(Math.max(skillInfo.getCooldown()-1,0));
		}
	}
	
	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(new ResourceLocation("thermionics", "weaponskill"), new ICapabilityProvider() {
				private WeaponSkillInfo info = new WeaponSkillInfo();
				
				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return (capability==CAPABILITY_WEAPONSKILL);
				}
	
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					if (capability==CAPABILITY_WEAPONSKILL) {
						return (T) info;
					} else {
						return null;
					}
				}
			});
		}
	}
	
	@SubscribeEvent
	public void onRegisterSounds(RegistryEvent.Register<SoundEvent> evt) {
		evt.getRegistry().register(SOUNDEVENT_SMAAAAAAASH);
	}
	
	public static boolean isAprilFools() {
		LocalDateTime now = LocalDateTime.now();
		return now.getDayOfMonth()==1 && now.getMonth()==Month.APRIL;
	}
}
