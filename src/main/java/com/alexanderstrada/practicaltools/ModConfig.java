// Copyright (c) 2020 Alexander Strada - MIT License (This header, with links, must not be removed)
//     https://github.com/astradamus/PracticalTools
//     https://curseforge.com/minecraft/mc-mods/practical-tools
//     https://twitch.tv/neurodr0me

package com.alexanderstrada.practicaltools;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Random;

public class ModConfig {

    public static final Random random = new Random();
    public static Common COMMON;

    public static ForgeConfigSpec init(ForgeConfigSpec.Builder builder) {
        COMMON = new Common(builder);
        return builder.build();
    }

    public static class Common {

        public final ForgeConfigSpec.IntValue hammerDuraLossMulti;
        public final ForgeConfigSpec.IntValue greataxeDuraLossMulti;
        public final ForgeConfigSpec.IntValue excavatorDuraLossMulti;

        public Common(ForgeConfigSpec.Builder builder) {

            builder.comment(" Practical Tools config \n These multipliers affect the amount of damage tools take per use.").push("common");
            hammerDuraLossMulti = builder.defineInRange("HammerDuraLossMulti", 2, 1, Integer.MAX_VALUE);
            greataxeDuraLossMulti = builder.defineInRange("GreataxeDuraLossMulti", 3, 1, Integer.MAX_VALUE);
            excavatorDuraLossMulti = builder.defineInRange("ExcavatorDuraLossMulti", 2, 1, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}
