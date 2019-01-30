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

package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.item.FluidSpirit;
import com.elytradev.thermionics.tileentity.TileEntityBattery;
import com.elytradev.thermionics.tileentity.TileEntityBatteryCreative;
import com.elytradev.thermionics.tileentity.TileEntityCableHeat;
import com.elytradev.thermionics.tileentity.TileEntityCableRF;
import com.elytradev.thermionics.tileentity.TileEntityConvectionMotor;
import com.elytradev.thermionics.tileentity.TileEntityDrum;
import com.elytradev.thermionics.tileentity.TileEntityFirebox;
import com.elytradev.thermionics.tileentity.TileEntityHammerMill;
import com.elytradev.thermionics.tileentity.TileEntityMashTun;
import com.elytradev.thermionics.tileentity.TileEntityOmniDuct;
import com.elytradev.thermionics.tileentity.TileEntityOreWasher;
import com.elytradev.thermionics.tileentity.TileEntityOven;
import com.elytradev.thermionics.tileentity.TileEntityPotStill;
import com.elytradev.thermionics.tileentity.TileEntityRFMotor;
import com.elytradev.thermionics.tileentity.TileEntityRotaryGenerator;
import com.elytradev.thermionics.tileentity.TileEntitySerger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ThermionicsBlocks {
	//Cabling
	public static BlockCableRF         CABLE_RF;
	public static BlockHeatPipe        CABLE_HEAT;
	public static BlockOmniDuct        OMNI_DUCT;
	
	//Fluid Storage
	public static BlockDrum            DRUM;
	
	//Heatmachines
	public static BlockFirebox         FIREBOX;
	public static BlockOven            OVEN;
	public static BlockMashTun         MASH_TUN;
	public static BlockPotStill        POT_STILL;
	public static BlockOreWasher       ORE_WASHER;
	
	//Motors
	public static BlockConvectionMotor MOTOR_CONVECTION;
	public static BlockRFMotor         MOTOR_RF;
	
	//Gears & Meshing
	public static BlockGearbox         GEARBOX;
	public static BlockAxle            AXLE_WOOD;
	public static BlockAxle            AXLE_IRON;
	
	//Rotarymachines
	public static BlockHammerMill      HAMMER_MILL;
	public static BlockSerger          SERGER;
	public static BlockRotaryGenerator GENERATOR_ROTARY;
	
	//RFmachines
	public static BlockBattery         BATTERY_LEAD;
	public static BlockBatteryCreative BATTERY_CREATIVE;
	
	//Explosives
	public static BlockTNTCreative     TNT_CREATIVE;
	
	//Randoms
	public static BlockScaffold        SCAFFOLD_BASIC;
	public static BlockRoad            ROAD;
	public static BlockRoad            ROAD_COMPRESSED;
	
	public static Fluid                FLUID_SPIRITS;
	public static Fluid                FLUID_HOOTCH;
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Block> event) {
		//Thermionics.LOG.info("Registering blocks");
		IForgeRegistry<Block> r = event.getRegistry();
		
		//Cabling
		CABLE_RF         = block(r, new BlockCableRF("rf"));
		CABLE_HEAT       = block(r, new BlockHeatPipe());
		OMNI_DUCT        = block(r, new BlockOmniDuct());
		
		//Fluid Storage
		DRUM             = block(r, new BlockDrum());
		
		//Heatmachines
		FIREBOX          = block(r, new BlockFirebox());
		OVEN             = block(r, new BlockOven());
		MASH_TUN         = block(r, new BlockMashTun());
		POT_STILL        = block(r, new BlockPotStill());
		ORE_WASHER       = block(r, new BlockOreWasher());
		
		//Motors
		MOTOR_CONVECTION = block(r, new BlockConvectionMotor());
		MOTOR_RF         = block(r, new BlockRFMotor());
		
		//Gears & Meshing
		GEARBOX          = block(r, new BlockGearbox());
		AXLE_WOOD        = block(r, new BlockAxle(Material.WOOD, "wood")).withHardness(1.0f).withHarvestLevel("axe", 0);
		AXLE_IRON        = block(r, new BlockAxle(Material.IRON, "iron"));
		
		//Rotarymachines
		HAMMER_MILL      = block(r, new BlockHammerMill());
		SERGER           = block(r, new BlockSerger());
		GENERATOR_ROTARY = block(r, new BlockRotaryGenerator());
		
		//RFmachines
		BATTERY_LEAD     = block(r, new BlockBattery("lead"));
		BATTERY_CREATIVE = block(r, new BlockBatteryCreative());
		
		//Explosives
		TNT_CREATIVE     = block(r, new BlockTNTCreative());
		
		//Randoms
		SCAFFOLD_BASIC   = block(r, new BlockScaffold("basic"));
		ROAD             = block(r, new BlockRoad(0));
		ROAD_COMPRESSED  = block(r, new BlockRoad(1));
			
		//registerBlock(new BlockMotorBase("redstone"));
		
		GameRegistry.registerTileEntity(TileEntityCableRF.class,         new ResourceLocation("thermionics:cable"));
		GameRegistry.registerTileEntity(TileEntityBattery.class,         new ResourceLocation("thermionics:battery.lead"));
		GameRegistry.registerTileEntity(TileEntityBatteryCreative.class, new ResourceLocation("thermionics:battery.creative"));
		GameRegistry.registerTileEntity(TileEntityDrum.class,            new ResourceLocation("thermionics:drum"));
		GameRegistry.registerTileEntity(TileEntityFirebox.class,         new ResourceLocation("thermionics:machine.firebox"));
		GameRegistry.registerTileEntity(TileEntityOven.class,            new ResourceLocation("thermionics:machine.oven"));
		GameRegistry.registerTileEntity(TileEntityCableHeat.class,       new ResourceLocation("thermionics:cable.heat"));
		GameRegistry.registerTileEntity(TileEntityOmniDuct.class,        new ResourceLocation("thermionics:omniduct"));
		GameRegistry.registerTileEntity(TileEntityConvectionMotor.class, new ResourceLocation("thermionics:machine.convectionmotor"));
		GameRegistry.registerTileEntity(TileEntityHammerMill.class,      new ResourceLocation("thermionics:machine.hammermill"));
		GameRegistry.registerTileEntity(TileEntitySerger.class,          new ResourceLocation("thermionics:machine.serger"));
		GameRegistry.registerTileEntity(TileEntityMashTun.class,         new ResourceLocation("thermionics:machine.mash_tun"));
		GameRegistry.registerTileEntity(TileEntityPotStill.class,        new ResourceLocation("thermionics:machine.pot_still"));
		GameRegistry.registerTileEntity(TileEntityRotaryGenerator.class, new ResourceLocation("thermionics:machine.generator"));
		GameRegistry.registerTileEntity(TileEntityOreWasher.class,       new ResourceLocation("thermionics:machine.orewasher"));
		GameRegistry.registerTileEntity(TileEntityRFMotor.class,         new ResourceLocation("thermionics:machine.rf_motor"));
		
		//GameRegistry.registerTileEntity(TileEntityCableSignal.class, "thermionics:cable.signal");
		
		
		
		/* Base stats come from ethanol; most common spirits irl behave in-between these numbers and water. Because
		 * they're alcohol mixed with water. I err on the side of alcohol to give the fluid more distinct properties.
		 */
		FLUID_SPIRITS = new FluidSpirit("spirit",
				new ResourceLocation("thermionics:fluids/medium_spirit"),
				new ResourceLocation("thermionics:fluids/medium_spirit"))
				.setDensity(789)     //ethanol is ~789 kg/m^3 at 20C
				.setLuminosity(0)    //liquor does not emit light
				.setTemperature(293) //cold! Best enjoyed at 20C
				.setViscosity(1250)  //A touch slower than water at 1.250 centipoise at 20C (to water's 1.0 at room temp)
				.setRarity(EnumRarity.UNCOMMON) //Not *rare*, but highly coveted.
				;
		FluidRegistry.registerFluid(FLUID_SPIRITS);
		
		FLUID_HOOTCH = new FluidSpirit("hootch",
				new ResourceLocation("thermionics:fluids/medium_hootch"),
				new ResourceLocation("thermionics:fluids/medium_hootch"))
				.setDensity(1400)             //almost molasses at 1400kg/m^3, and often actually is alcoholic molasses
				.setLuminosity(0)             //nope
				.setTemperature(300)          //disgustingly room-temperature, but often warmer
				.setViscosity(4_000_000)      //I hope you're prepared to wait. Molasses clocks in at 5K centipoise, this is only 4K
				.setRarity(EnumRarity.COMMON) //Can be produced safely in any ordinary backyard.
				;
		FluidRegistry.registerFluid(FLUID_HOOTCH);
	}
	
	public static <T extends Block> T block(IForgeRegistry<Block> registry, T t) {
		registry.register(t);
		Thermionics.instance().needItemRegistration.add(t);
		return t;
	}
}
