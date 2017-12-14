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

package com.elytradev.thermionics.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.api.IAuxDestroyBlock;
import com.elytradev.thermionics.api.IOreRepair;
import com.elytradev.thermionics.api.IWeaponSkillInfo;
import com.elytradev.thermionics.api.impl.ISkillActivating;
import com.elytradev.thermionics.data.EnumWeaponSkill;
import com.elytradev.thermionics.network.SpawnParticleEmitterMessage;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHammer extends ItemTool implements IAuxDestroyBlock, IOreRepair, ISkillActivating {
	String fakeToolMaterial = "ingotIron";
	
	//Whoever in Mojang specialcased literally every effective pickaxe block inside ItemPickaxe can die in a fire.
	private static Set<Block> VANILLA_WHITELIST = Sets.newHashSet(new Block[] {
			Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE,
			Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL,
			Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB,
			Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE});

	
	private static HashSet<Block> BLACKLIST = new HashSet<>();
	private static HashSet<Block> WHITELIST = new HashSet<>();
	static {
		BLACKLIST.add(Blocks.TORCH);
		BLACKLIST.add(Blocks.STANDING_BANNER);
		BLACKLIST.add(Blocks.WALL_BANNER);
		BLACKLIST.add(Blocks.STANDING_SIGN);
		BLACKLIST.add(Blocks.WALL_SIGN);
		BLACKLIST.add(Blocks.BEDROCK);
		
		WHITELIST.addAll(VANILLA_WHITELIST);
	}
	
	public ItemHammer(ToolMaterial materialIn, String materialName) {
		super(4, -2, materialIn, Sets.newHashSet());
		
		this.setRegistryName(new ResourceLocation("thermionics","hammer."+materialName));
		this.setUnlocalizedName("thermionics.hammer."+materialName);
		
        this.toolMaterial = materialIn;
        this.maxStackSize = 1;
        this.setMaxDamage(materialIn.getMaxUses() * 9);
        this.efficiencyOnProperMaterial = materialIn.getEfficiencyOnProperMaterial();
        this.damageVsEntity = 4 + materialIn.getDamageVsEntity();
        this.attackSpeed = -3.6f;
        this.setCreativeTab(Thermionics.TAB_THERMIONICS);
	}
	
	public ItemHammer(String id, String fakeToolMaterial, int level, int uses, float efficiency, float damage, int enchantability) {
		super(4, -2, ToolMaterial.IRON, Sets.newHashSet()); 
		this.fakeToolMaterial = fakeToolMaterial;
		
		this.setRegistryName(new ResourceLocation("thermionics","hammer."+id));
		this.setUnlocalizedName("thermionics.hammer."+id);
		
        this.toolMaterial = ToolMaterial.IRON;
        this.maxStackSize = 1;
        this.setMaxDamage(uses * 9);
        this.efficiencyOnProperMaterial = efficiency;
        this.damageVsEntity = 4 + damage;
        this.attackSpeed = -3.6f;
        this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
		String harvestTool = blockIn.getBlock().getHarvestTool(blockIn);
		if (harvestTool==null) return true;
		
		boolean isCorrectTool = harvestTool.equals("pickaxe") || harvestTool.equals("hammer");
		boolean isCorrectLevel = blockIn.getBlock().getHarvestLevel(blockIn) <= this.toolMaterial.getHarvestLevel();
		
		return isCorrectTool && isCorrectLevel;
	}
	
	@Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("pickaxe", "hammer");
    }
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer)) return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
		EntityPlayer player = (EntityPlayer)entityLiving;
		
        if (!world.isRemote && state.getBlockHardness(world, pos) != 0) {
            stack.damageItem(1, player);
            
            ArrayList<BlockPos> crushGroup = new ArrayList<>();
            
            //check and break anything nearby of equal or lower hardness
            for(int z=-1; z<=1; z++) {
            	for(int y=-1; y<=1; y++) {
            		for(int x=-1; x<=1; x++) {
            			if (x==0 && y==0 && z==0) continue;
            			BlockPos crushLocation = pos.add(x, y, z);
            			IBlockState toCrush = world.getBlockState(crushLocation);
            			
            			if (toCrush.getBlockHardness(world, crushLocation) <= state.getBlockHardness(world, pos)) {
            				if (!BLACKLIST.contains(toCrush.getBlock())) {
	            				crushGroup.add(crushLocation);
	            				stack.damageItem(1, entityLiving);
            				}
            			}
            		}
            	}
            }
            
            for(BlockPos cur : crushGroup) {
            	if (player instanceof EntityPlayerMP) { //Should be true any time we reach this part of the code
    				ToolHelper.auxHarvestBlock(world, cur, (EntityPlayerMP)player);
    			}
            }
        }
        
        return true;
    }
	
	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (fakeToolMaterial!=null) {
			return ToolHelper.matchesOreName(fakeToolMaterial, repair);
		} else {
			return super.getIsRepairable(toRepair, repair);
		}
    }
	
	@Override
	public void onBlockAuxDestroyed(World world, IBlockState state, BlockPos pos, EntityPlayer player) {
		// Thermionics hammers don't do anything special here. But you could.
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		//Certain materials are just effective with a pickaxe, and by extension, hammers.
        Material material = state.getMaterial();
        if (material == Material.IRON || material == Material.ANVIL || material == Material.ROCK) return this.efficiencyOnProperMaterial;
        
        if (WHITELIST.contains(state.getBlock())) return this.efficiencyOnProperMaterial;
        else return super.getStrVsBlock(stack, state);
    }
	
	/**
	 * Sets the toolMaterial for the passed-in stack and returns it. Doesn't make a defensive copy, do that yourself!
	 * @param stack the ItemStack to alter
	 * @return the passed-in stack
	 */
	public static ItemStack setToolMaterial(ItemStack stack, String toolMaterial) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound tag = stack.getTagCompound();
		tag.setString("toolMaterial", toolMaterial);
		
		return stack;
	}

	public static ItemStack createTool(String toolMaterial) {
		ItemStack result = new ItemStack(ThermionicsItems.HAMMER, 1);
		
		return setToolMaterial(result, toolMaterial);
	}
	
	public String getFakeToolMaterial(ItemStack stack) {
		if (!stack.hasTagCompound()) return this.fakeToolMaterial; //Support legacy hammers without comment.
		String material = stack.getTagCompound().getString("toolMaterial").trim();
		if (material==null || material.isEmpty()) return this.fakeToolMaterial; //Again, default to legacy hammers.
		
		return material;
	}
	
	@Override
	public String getOreRepairMaterial(ItemStack stack) {
		return getFakeToolMaterial(stack);
	}
	
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return false;
    }
	
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
		
        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.damageVsEntity, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
        }

        return multimap;
    }
	
	public float getEntityDamage() {
		return this.damageVsEntity;
	}

	private Random rnd = new Random();
	@Override
	public int activateSkill(IWeaponSkillInfo info, EntityLivingBase attacker, ItemStack item, DamageSource source, EntityLivingBase opponent) {
		//If you don't know about Earthbound, we can't be friends. Sorry, I don't make the rules.
		DamageSource smaaaaaaash = new EntityDamageSource("weaponskill.smash", attacker);
		opponent.attackEntityFrom(smaaaaaaash, 4f);
		
		opponent.addPotionEffect( new PotionEffect(Potion.getPotionFromResourceLocation("minecraft:blindness"), 20*3, 2 ));
		//Thermionics.LOG.info("SMAAAAAAASH WeaponSkill activated against entity {} at {},{},{}", opponent, opponent.posX, opponent.posY, opponent.posZ);
		
		if (!attacker.world.isRemote) {
			//Serverside, queue the effect
			SpawnParticleEmitterMessage fx = new SpawnParticleEmitterMessage(Thermionics.CONTEXT, EnumWeaponSkill.SMAAAAAAASH, opponent);
			fx.sendToAllWatching(opponent);
		}
		
		return 20*5;
	}
}
