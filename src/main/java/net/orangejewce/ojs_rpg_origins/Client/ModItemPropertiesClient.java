package net.orangejewce.ojs_rpg_origins.Client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import net.orangejewce.ojs_rpg_origins.item.ModItems;

@Environment(EnvType.CLIENT)
public class ModItemPropertiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        addCustomItemProperties();
    }

    public static void addCustomItemProperties() {
        makeBow();
    }

    private static void makeBow() {
        ModelPredicateProviderRegistry.register(ModItems.WHELM, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        ModelPredicateProviderRegistry.register(ModItems.WHELM, new Identifier("pulling"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
    }
}
