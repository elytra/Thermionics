package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRoad extends BlockBase {
	public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 5);
	
	public BlockRoad() {
		super(Material.ROCK, EnumDyeColor.WHITE.getMapColor());
		
		this.setRegistryName("road");
		this.setUnlocalizedName("thermionics.road");
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.4f);
		this.setResistance(20f); //somewhat resistant to explosions
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT);
	}
	
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, meta);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase)entity;
			
			living.addPotionEffect(new PotionEffect(Thermionics.POTION_EFFORTLESS_SPEED, 5, 1));
			//living.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("speed"), 5, 2));
		}
	}
	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(int i : VARIANT.getAllowedValues()) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
}
