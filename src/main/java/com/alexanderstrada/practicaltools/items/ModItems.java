// Copyright (c) 2020 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools.items;

import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {

    public static Item IRON_EXCAVATOR;
    public static Item GOLDEN_EXCAVATOR;
    public static Item DIAMOND_EXCAVATOR;

    public static Item IRON_HAMMER;
    public static Item GOLDEN_HAMMER;
    public static Item DIAMOND_HAMMER;

    public static Item IRON_GREATAXE;
    public static Item GOLDEN_GREATAXE;
    public static Item DIAMOND_GREATAXE;

    public static void init(RegistryEvent.Register<Item> event) {
        Item ironBlock = Item.BLOCK_TO_ITEM.get(Blocks.IRON_BLOCK);
        Item goldBlock = Item.BLOCK_TO_ITEM.get(Blocks.GOLD_BLOCK);
        Item diamondBlock = Item.BLOCK_TO_ITEM.get(Blocks.DIAMOND_BLOCK);

        IRON_EXCAVATOR = mkExcavator(ItemTier.IRON, ironBlock, "iron_excavator");
        GOLDEN_EXCAVATOR = mkExcavator(ItemTier.GOLD, goldBlock, "golden_excavator");
        DIAMOND_EXCAVATOR = mkExcavator(ItemTier.DIAMOND, diamondBlock, "diamond_excavator");

        IRON_HAMMER = mkHammer(ItemTier.IRON, ironBlock, "iron_hammer");
        GOLDEN_HAMMER = mkHammer(ItemTier.GOLD, goldBlock, "golden_hammer");
        DIAMOND_HAMMER = mkHammer(ItemTier.DIAMOND, diamondBlock, "diamond_hammer");

        IRON_GREATAXE = mkGreataxe(ItemTier.IRON, ironBlock, "iron_greataxe");
        GOLDEN_GREATAXE = mkGreataxe(ItemTier.GOLD, goldBlock, "golden_greataxe");
        DIAMOND_GREATAXE = mkGreataxe(ItemTier.DIAMOND, diamondBlock, "diamond_greataxe");

        event.getRegistry().register(IRON_EXCAVATOR);
        event.getRegistry().register(GOLDEN_EXCAVATOR);
        event.getRegistry().register(DIAMOND_EXCAVATOR);

        event.getRegistry().register(IRON_HAMMER);
        event.getRegistry().register(GOLDEN_HAMMER);
        event.getRegistry().register(DIAMOND_HAMMER);

        event.getRegistry().register(IRON_GREATAXE);
        event.getRegistry().register(GOLDEN_GREATAXE);
        event.getRegistry().register(DIAMOND_GREATAXE);
    }

    private static Item mkExcavator(ItemTier tier, Item customRepair, String registryName) {
        Ingredient ingredient = Ingredient.fromItems(customRepair);
        return new ExcavatorItem(tier, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS), ingredient).setRegistryName(registryName);
    }

    private static Item mkHammer(ItemTier tier, Item customRepair, String registryName) {
        Ingredient ingredient = Ingredient.fromItems(customRepair);
        return new HammerItem(tier, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS), ingredient).setRegistryName(registryName);
    }

    private static Item mkGreataxe(ItemTier tier, Item customRepair, String registryName) {
        Ingredient ingredient = Ingredient.fromItems(customRepair);
        return new GreataxeItem(tier, 6F, -3.4F, (new Item.Properties()).group(ItemGroup.TOOLS), ingredient).setRegistryName(registryName);
    }
}
