package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockVegDesert extends BlockTerra implements IPlantable
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("veg", DesertVegType.class);
	public static final PropertyBool IS_ON_STONE = PropertyBool.create("isonstone");
	public BlockVegDesert()
	{
		super(Material.VINE, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.GROUND);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, DesertVegType.DeadBush).withProperty(IS_ON_STONE, false));
		float f = 0.35F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		checkAndDropBlock((World) worldIn, pos, worldIn.getBlockState(pos));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
	{
		if(!worldIn.isRemote && player.getHeldItemMainhand().getItem() == TFCItems.StoneKnife)
		{
			int count = 1;
			if(worldIn.getBlockState(pos.up()).getBlock() == this)
			{
				count = 2;
			}
			EntityItem ei = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TFCItems.Straw, count));
			worldIn.spawnEntity(ei);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!canBlockStay(worldIn, pos, state))
		{
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		BlockPos down = pos.down();
		IBlockState soil = worldIn.getBlockState(down);
		if (state.getBlock() != this) 
			return canPlaceBlockOn(state, soil);
		return soil.getBlock().canSustainPlant(soil, worldIn, down, EnumFacing.UP, this);
	}

	protected boolean canPlaceBlockOn(IBlockState state, IBlockState soil)
	{
		return Core.isSand(soil);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		DesertVegType veg = (DesertVegType)state.getValue(META_PROPERTY);
		if(plant.getBlock() == this)
		{
			if(veg == DesertVegType.DoubleGrassBottomSparse && plant.getValue(META_PROPERTY) == DesertVegType.DoubleGrassTopSparse)
				return true;
		}
		return false;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.withProperty(IS_ON_STONE, Core.isStone(world.getBlockState(pos.down())));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, DesertVegType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((DesertVegType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.75, 0.9);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { META_PROPERTY, IS_ON_STONE});
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 3;
	}

	public enum DesertVegType implements IStringSerializable
	{
		Tackweed("tackweed", 0),
		Ocatillo("ocatillo", 1),
		Yucca("yucca", 2),
		Primrose("primrose", 3),
		DeadBush("deadbush", 4),
		DoubleGrassBottomSparse("doublegrassbottomsparse", 5),
		DoubleGrassTopSparse("doublegrasstopsparse", 6),
		GrassSparse("grass_sparse", 7),
		ShortGrassSparse("shortgrass_sparse", 8),
		ShorterGrassSparse("shortergrass_sparse", 9);

		private String name;
		private int meta;

		DesertVegType(String s, int id)
		{
			name = s;
			meta = id;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta()
		{
			return meta;
		}

		public static DesertVegType getTypeFromMeta(int meta)
		{
			for(int i = 0; i < DesertVegType.values().length; i++)
			{
				if(DesertVegType.values()[i].meta == meta)
					return DesertVegType.values()[i];
			}
			return null;
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) 
	{
		return EnumPlantType.Desert;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) 
	{
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) 
			return getDefaultState();
		return state;
	}
}
