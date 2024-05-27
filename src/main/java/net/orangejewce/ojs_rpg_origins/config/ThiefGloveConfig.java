package net.orangejewce.ojs_rpg_origins.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Config(name = "ojs_rpg_origins")
public class ThiefGloveConfig implements ConfigData {
    public double stealChance = 0.5; // 50% chance to steal
    public int cooldownTicks = 100; // 5 seconds cooldown (100 ticks)
    public List<String> stealableItems = Arrays.asList(
            "minecraft:diamond",
            "minecraft:emerald",
            "minecraft:gold_ingot"
    );

    public static void register() {
        AutoConfig.register(ThiefGloveConfig.class, GsonConfigSerializer::new);
    }

    public static ThiefGloveConfig getInstance() {
        return AutoConfig.getConfigHolder(ThiefGloveConfig.class).getConfig();
    }

    public static List<ItemStack> getStealableItems() {
        ThiefGloveConfig config = getInstance();
        return config.stealableItems.stream()
                .map(itemId -> Registries.ITEM.get(new Identifier(itemId)))
                .map(ItemStack::new)
                .collect(Collectors.toList());
    }
}
