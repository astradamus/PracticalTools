// Copyright (c) 2020 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools.items;

import com.alexanderstrada.practicaltools.AreaBreak;
import com.alexanderstrada.practicaltools.ModConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;


public class ExcavatorItem extends ShovelItem {

    /** Copy-pasted from ShovelItem because it is private and inaccessible to children. */
    public static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
    public static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.CLAY, Material.ORGANIC, Material.EARTH, Material.SAND, Material.SNOW, Material.SNOW_BLOCK);

    public final Ingredient customRepair;

    protected ExcavatorItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder, Ingredient customRepair) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.customRepair = customRepair;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        stack.attemptDamageItem(ModConfig.COMMON.excavatorDuraLossMulti.get()-1, ModConfig.random, null);

        if (entityLiving instanceof PlayerEntity)
            AreaBreak.areaAttempt(world, pos, (PlayerEntity) entityLiving, EFFECTIVE_ON, EFFECTIVE_MATERIALS, true);

        return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return customRepair.test(repair);
    }
}
