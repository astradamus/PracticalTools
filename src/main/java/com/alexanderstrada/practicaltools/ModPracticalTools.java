// Copyright (c) 2020 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools;

import com.alexanderstrada.practicaltools.items.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("practicaltools")
public class ModPracticalTools {

    public static final String MODID = "practicaltools";

    public ModPracticalTools() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.init(new ForgeConfigSpec.Builder()));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            ModItems.init(event);
        }
    }
}
