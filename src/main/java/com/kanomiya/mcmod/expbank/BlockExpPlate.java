package com.kanomiya.mcmod.expbank;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by kanomiya on 2017/06/25.
 */
public class BlockExpPlate extends Block implements ITileEntityProvider {
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockExpPlate() {
        super(Material.ROCK, MapColor.GRAY);

        setHardness(1.5f);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public int tickRate(World worldIn) {
        return 20;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        TileEntityExpPlate tile = (TileEntityExpPlate) worldIn.getTileEntity(pos);
        worldIn.setBlockState(pos, state.withProperty(POWERED, false));
        ((TileEntityExpPlate) worldIn.getTileEntity(pos)).setData(tile.experience, tile.tick);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        TileEntityExpPlate tile = (TileEntityExpPlate) world.getTileEntity(pos);
        return (int) (tile.experience *.6d);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (! worldIn.isRemote) {
            TileEntityExpPlate tile = (TileEntityExpPlate) worldIn.getTileEntity(pos);
            playerIn.sendMessage(new TextComponentString("Exp: " + tile.experience));
        }
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityPlayer) {
            TileEntityExpPlate tile = (TileEntityExpPlate) worldIn.getTileEntity(pos);
            EntityPlayer player = (EntityPlayer) entityIn;

            tile.tick ++;

            if (tile.tick > 20) {
                boolean flag = false;

                if (worldIn.isBlockPowered(pos)) {
                    if (tile.experience > 0) {
                        int exp = Math.min(player.xpBarCap(), tile.experience);
                        if (exp > 0) {
                            player.addExperience(exp);
                            tile.experience -= exp;

                            flag = true;
                        }
                    }
                }
                else if (player.experienceLevel > 0){
                    int exp = ExpBank.getLevelCap(player.experienceLevel);
                    int rest = tile.limit -tile.experience;
                    if (rest < exp) {
                        exp = rest;
                    }

                    if (exp > 0) {
                        tile.experience += exp;
                        player.addExperienceLevel(-1);
                        flag = true;
                    }
                }

                if (flag) {
                    int d = (int) (30 * 20 * Math.abs(Math.sin((double) player.experienceLevel /10d *Math.PI /2d)));
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, d));
                    player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, d));
                    player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, d));
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, d));
                }

                tile.tick = 0;
            }

            if (((worldIn.isBlockPowered(pos) && tile.experience > 0) || (! worldIn.isBlockPowered(pos) && player.experienceLevel > 0)) && ! state.getValue(POWERED)) {
                worldIn.setBlockState(pos, state.withProperty(POWERED, true));
                ((TileEntityExpPlate) worldIn.getTileEntity(pos)).setData(tile.experience, tile.tick);
                worldIn.scheduleUpdate(new BlockPos(pos), this, tickRate(worldIn));
            }
        }

    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(POWERED, false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityExpPlate();
    }


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0d, 0d, 0d, 1d, .25d, 1d);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return getBoundingBox(blockState, worldIn, pos).grow(.1d);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(POWERED, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

}
