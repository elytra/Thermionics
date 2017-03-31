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
package com.elytradev.thermionics.block;

import com.elytradev.thermionics.Thermionics;
import com.elytradev.thermionics.data.IPreferredRenderState;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockMachineBase extends BlockBase implements IPreferredRenderState {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING; //Yes, I'm stealing it.
	private static final EnumFacing[] VALID_FACING = { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST };
	
	public BlockMachineBase(String id) {
		super(Material.IRON);
		this.setRegistryName("machine."+id);
		this.setUnlocalizedName("thermionics.machine."+id);
		this.setCreativeTab(Thermionics.TAB_THERMIONICS);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.4f);
		this.setResistance(8f); //actually quite susceptible to explosions
		
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ACTIVE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 0x03));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        
        if (tileentity!=null && tileentity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
        	IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        	for (int i=0; i<inventory.getSlots(); i++) {
                ItemStack itemstack = inventory.getStackInSlot(i);

                if (!itemstack.isEmpty()) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                }
            }
        	worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity!=null && tileentity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
        	IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        	
        	int capacity = inventory.getSlots();
        	int filled = 0;
        	for(int i=0; i<capacity; i++) {
        		if (!inventory.getStackInSlot(i).isEmpty()) filled++;
        	}
        	
        	float fraction = filled / (float)capacity;
        	
        	return (int)(fraction*15);
		} else {
			return 0;
		}
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos) {
		return VALID_FACING;
	}
	
	/**
	 * A straightforward (vanilla-conforming) implementation for planar machines would be to discard rotation hits from
	 * the sides, but this might be hard for players to interpret. Instead, when asked to rotate, we proactively rotate
	 * around the Y axis. This should make things like charset and yotta wrench Just Work, even though machines don't
	 * know that they exist.
	 * 
	 * This may even cause rotations to work properly in charset block-carry. More research is needed.
	 */
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState cur = world.getBlockState(pos);
		
		world.setBlockState(pos, cur.withProperty(FACING, cur.getValue(FACING).rotateY()));
		return true;
	}

	@Override
	public String getPreferredRenderState() {
		return "active=false,facing=north";
	}
}
