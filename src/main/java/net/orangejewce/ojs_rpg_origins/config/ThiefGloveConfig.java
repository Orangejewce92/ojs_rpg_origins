package net.orangejewce.ojs_rpg_origins.config;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThiefGloveConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> stealableItems;
    public static ForgeConfigSpec.DoubleValue stealChance;
    public static ForgeConfigSpec.IntValue cooldownTicks;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        stealableItems = builder.comment("List of items that can be stolen (format: modid:itemid)")
                .defineList("stealableItems", new ArrayList<String>() {{
                    add("minecraft:diamond");
                    add("minecraft:gold_ingot");
                    add("minecraft:emerald");
                    add("minecraft:iron_ingot");
                    add("minecraft:book");
                    add("ojs_rpg_origins:sapphire");
                }}, o -> o instanceof String);

        stealChance = builder.comment("Chance to successfully steal an item")
                .defineInRange("stealChance", 0.1, 0.0, 1.0);

        cooldownTicks = builder.comment("Cooldown in ticks between steal attempts")
                .defineInRange("cooldownTicks", 100, 0, Integer.MAX_VALUE);

        COMMON_CONFIG = builder.build();
    }

    public static List<ItemStack> getStealableItems() {
        List<ItemStack> items = new ArrayList<>();
        for (String itemName : stealableItems.get()) {
            ItemStack itemStack = new ItemStack(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(itemName)
                    )
            );
            items.add(itemStack);
        }
        return items;
    }
}
