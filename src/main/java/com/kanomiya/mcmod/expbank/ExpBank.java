package com.kanomiya.mcmod.expbank;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.UUID;

/**
 * Created by kanomiya on 2017/05/21.
 */
@Mod(modid = ExpBank.MOD_ID, name = ExpBank.MOD_NAME, version = ExpBank.VERSION)
public class ExpBank {
    public static final String MOD_ID = "com.kanomiya.mcmod.expbank";
    public static final String MOD_NAME = "Exp Bank Mod";
    public static final String VERSION = "0.1.1";

    private static DummyPlayer dummyPlayer;

    public static class EBlocks {
        public static final Block exp_plate = new BlockExpPlate()
                .setRegistryName(MOD_ID, "exp_plate")
                .setUnlocalizedName(MOD_ID + ".exp_plate");
    }
    public static class EItems {
        public static final Item exp_plate = new ItemBlock(EBlocks.exp_plate)
                .setRegistryName(MOD_ID, "exp_plate");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EItems.exp_plate), new Object[] {
                "III",
                "QEQ",
                'I', Items.IRON_INGOT,
                'Q', Items.QUARTZ,
                'E', Items.EMERALD
        }));
        GameRegistry.registerTileEntity(TileEntityExpPlate.class, MOD_ID + ":exp_plate");
        ModelLoader.setCustomModelResourceLocation(EItems.exp_plate, 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, "exp_plate"), "inventory"));
    }

    public static synchronized int getTotalExp(int level) {
        int total = 0;
        while (level > 0) {
            total += getLevelCap(level);
            level --;
        }
        return total;
    }

    public static synchronized int getLevelCap(int level) {
        dummyPlayer.experienceLevel = level -1;
        return dummyPlayer.xpBarCap();
    }

    public static synchronized int getLevel(int expTotal) {
        dummyPlayer.experienceLevel = 0;

        int cap;
        while ((cap = dummyPlayer.xpBarCap()) <= expTotal) {
            expTotal -= cap;
            dummyPlayer.experienceLevel ++;
        }

        return dummyPlayer.experienceLevel;
    }

    @Mod.EventBusSubscriber
    public static class Register {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                EBlocks.exp_plate
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                EItems.exp_plate
            );
        }

    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void x(WorldEvent.Load event) {
            dummyPlayer = new DummyPlayer(event.getWorld(), new GameProfile(new UUID(0L, 0L), "dummy"));
        }
    }

}
