// Copyright (c) 2019 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools.items;

import com.alexanderstrada.practicaltools.ModConfig;
import com.alexanderstrada.practicaltools.ToolFunctions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Set;


public class GreataxeItem extends AxeItem {

    /** Copy-pasted from AxeItem because it is private and inaccessible to children. */
    public static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);

    public static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.WOOD, Material.GOURD, Material.CACTUS);

    public static final int LOG_BREAK_DELAY = 1;

    public final Ingredient customRepair;

    protected GreataxeItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder, Ingredient customRepair) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.customRepair = customRepair;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        stack.attemptDamageItem(ModConfig.COMMON.greataxeDuraLossMulti.get()-1, ToolFunctions.random, null);

        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;

            if (!attemptFellTree(world, pos, player)) {
                ToolFunctions.attemptBreakNeighbors(world, pos, player, EFFECTIVE_ON, EFFECTIVE_MATERIALS, false);
            }
        }

        return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return customRepair.test(repair);
    }

    /** Determines if the destroyed block was part of a tree and, if so, breaks all connected logs and returns true.*/
    private boolean attemptFellTree(World world, BlockPos pos, PlayerEntity player) {

        ArrayList<BlockPos> logs = new ArrayList<>();
        ArrayList<BlockPos> candidates = new ArrayList<>();
        candidates.add(pos);

        int leaves = 0;

        // Find all connected logs and count all connected leaves
        for (int i = 0; i < candidates.size(); i++) {
            if (logs.size() > 200) return false; // Whatever this is, it's too big! I don't want to know what happens if I let you use this in an all-log RFTDim.

            BlockPos candidate = candidates.get(i);
            Block block = world.getBlockState(candidate).getBlock();

            if (BlockTags.LEAVES.contains(block)) {
                leaves++;
            }
            else if (logs.size() == 0 || BlockTags.LOGS.contains(block)) {
                logs.add(candidate);

                // We found a log, check for neighboring logs
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) { // No good reason to check downwards, cuts 1/3 off this loop
                        for (int z = -1; z <= 1; z++) {
                            BlockPos neighbor = candidate.add(x, y, z);
                            if (candidates.contains(neighbor)) continue; // Don't check positions twice
                            candidates.add(neighbor);
                        }
                    }
                }
            }
        }

        if (logs.size() == 0) return false; // No logs? No tree.

        if (leaves >= logs.size()/6.0) { // Trees have leaves. Since we only count leaves adjacent to logs, we favor leaves a bit.

            // Break the tree. Spread across several ticks because doing all at once causes the game to stutter, even for small trees.
            MinecraftForge.EVENT_BUS.register(new Object() {

                int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
                int silkLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand());

                int delay = LOG_BREAK_DELAY;
                int i = 0;

                @SubscribeEvent
                public void onTick(TickEvent.WorldTickEvent event) {
                    if (delay-- > 0) return;
                    delay = LOG_BREAK_DELAY;
                    if (i < logs.size()) {
                        BlockPos log = logs.get(i);
                        ToolFunctions.attemptBreak(world, log, player, EFFECTIVE_ON, EFFECTIVE_MATERIALS, fortuneLevel, silkLevel, false);
                        i++;
                    }
                    else {
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }
            });

            return true;
        }

        return false;
    }
}
