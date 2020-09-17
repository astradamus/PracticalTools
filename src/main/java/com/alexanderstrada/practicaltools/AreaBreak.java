// Copyright (c) 2020 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.Set;

public class AreaBreak {

    /** Attempt to break blocks around the given pos in a 3x3x1 square relative to the targeted face.*/
    public static void areaAttempt(World world,
                                   BlockPos pos,
                                   PlayerEntity player,
                                   Set<Block> effectiveOn,
                                   Set<Material> effectiveMaterials,
                                   boolean checkHarvestLevel) {

        RayTraceResult trace = calcRayTrace(world, player, RayTraceContext.FluidMode.ANY);

        if (trace.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockTrace = (BlockRayTraceResult) trace;
            Direction face = blockTrace.getFace();

            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (a == 0 && b == 0) continue;

                    BlockPos target = null;

                    if (face == Direction.UP    || face == Direction.DOWN)  target = pos.add(a, 0, b);
                    if (face == Direction.NORTH || face == Direction.SOUTH) target = pos.add(a, b, 0);
                    if (face == Direction.EAST  || face == Direction.WEST)  target = pos.add(0, a, b);

                    attemptBreak(world, target, pos, player, effectiveMaterials, effectiveOn, checkHarvestLevel);
                }
            }
        }
    }

    /** Attempt to break a block. Fails if the tool is not effective on the given block, or if the origin broken block
     * is significantly easier to break than the target block (i.e. don't let players use sandstone to quickly area
     * break obsidian).*/
    public static void attemptBreak(World world,
                                    BlockPos target,
                                    BlockPos origin,
                                    PlayerEntity player,
                                    Set<Material> effectiveMaterials,
                                    Set<Block> effectiveOn,
                                    boolean checkHarvestLevel) {

        BlockState state = world.getBlockState(target);

        if (checkHarvestLevel && !ForgeHooks.canHarvestBlock(state, player, world, target)) {
            return; // We are checking harvest level and this tool doesn't qualify.
        }

        if (!effectiveMaterials.contains(state.getMaterial()) && !effectiveOn.contains(state.getBlock())) {
            return; // This tool is not effective on this block.
        }

        // Prevent players from using low-hardness blocks to quickly break adjacent high-hardness blocks.
        if (origin != null) {
            BlockState originState = world.getBlockState(origin);
            float originHard = originState.getPlayerRelativeBlockHardness(player, world, origin);
            float targetHard = state.getPlayerRelativeBlockHardness(player, world, target);
            if (originHard / targetHard > 10f) {
                return;
            }
        }

        ServerPlayerEntity spe = (ServerPlayerEntity) player;
        int expDropped = ForgeHooks.onBlockBreakEvent(world, spe.interactionManager.getGameType(), spe, target);

        Block block = state.getBlock();
        if (block.removedByPlayer(state, world, target, player, true, world.getFluidState(target))) {
            block.onPlayerDestroy(world, target, state);
            block.harvestBlock(world, player, target, state, world.getTileEntity(target), player.getHeldItemMainhand());
            block.dropXpOnBlockBreak(world, target, expDropped);
        }
    }

    /** Attempt to break a block. Fails if the tool is not effective on the given block.*/
    public static void attemptBreak(World world,
                                    BlockPos target,
                                    PlayerEntity player,
                                    Set<Material> effectiveMaterials,
                                    Set<Block> effectiveOn,
                                    boolean checkHarvestLevel) {
        attemptBreak(world, target, null, player, effectiveMaterials, effectiveOn, checkHarvestLevel);
    }

    /** Copy-pasted from "Item.rayTrace" which is protected static, making it unusable in my own static helper methods.*/
    public static RayTraceResult calcRayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vec3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }
}
