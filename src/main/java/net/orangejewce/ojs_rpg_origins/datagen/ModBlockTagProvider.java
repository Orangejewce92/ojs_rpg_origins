package net.orangejewce.ojs_rpg_origins.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.orangejewce.ojs_rpg_origins.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
    getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.RAW_SAPPHIRE_BLOCK)
                        .add(ModBlocks.SAPPHIRE_BLOCK)
                        .add(ModBlocks.SAPPHIRE_ORE)
                        .add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE)
                        .add(ModBlocks.NETHER_SAPPHIRE_ORE)
                        .add(ModBlocks.END_STONE_SAPPHIRE_ORE);


                        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                        .add(ModBlocks.SAPPHIRE_BLOCK);

                        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                        .add(ModBlocks.RAW_SAPPHIRE_BLOCK)
                        .add(ModBlocks.SAPPHIRE_ORE);

                        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                        .add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE);

                        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_4")))
                        .add(ModBlocks.END_STONE_SAPPHIRE_ORE);
    }
}
