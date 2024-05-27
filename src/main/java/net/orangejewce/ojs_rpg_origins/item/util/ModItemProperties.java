package net.orangejewce.ojs_rpg_origins.item.util;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;
import net.orangejewce.ojs_rpg_origins.item.ModItems;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        makeBow(ModItems.WHELM);
    }

    private static void makeBow(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        ModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
    }
}
