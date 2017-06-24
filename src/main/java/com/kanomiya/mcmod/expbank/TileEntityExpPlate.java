package com.kanomiya.mcmod.expbank;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * Created by kanomiya on 2017/06/25.
 */
public class TileEntityExpPlate extends TileEntity {
    public int limit;
    public int experience;
    public int tick;

    public TileEntityExpPlate(int limit) {
        this.limit = limit;
    }

    public void setData(int experience, int tick) {
        this.experience = experience;
        this.tick = tick;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.experience = compound.getInteger("experience");
        this.limit = compound.getInteger("limit");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("experience", experience);
        compound.setInteger("limit", limit);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.getUpdateTag();
        compound.setInteger("experience", experience);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
    /*
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
    */
}
