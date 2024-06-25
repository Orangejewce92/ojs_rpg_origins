package net.orangejewce.ojs_rpg_origins.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.orangejewce.ojs_rpg_origins.block.ModBlocks;
import net.orangejewce.ojs_rpg_origins.item.ModItems;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SAPPHIRE_BLOCK);
        addDrop(ModBlocks.RAW_SAPPHIRE_BLOCK);

        addDrop(ModBlocks.SAPPHIRE_ORE, copperLikeOreDrops(ModBlocks.SAPPHIRE_ORE, ModItems.RAW_SAPPHIRE));
        addDrop(ModBlocks.DEEPSLATE_SAPPHIRE_ORE, copperLikeOreDrops(ModBlocks.DEEPSLATE_SAPPHIRE_ORE, ModItems.RAW_SAPPHIRE));
        addDrop(ModBlocks.NETHER_SAPPHIRE_ORE, copperLikeOreDrops(ModBlocks.NETHER_SAPPHIRE_ORE, ModItems.RAW_SAPPHIRE));
        addDrop(ModBlocks.END_STONE_SAPPHIRE_ORE, copperLikeOreDrops(ModBlocks.END_STONE_SAPPHIRE_ORE, ModItems.RAW_SAPPHIRE));

    }

    public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop,
                ((LeafEntry.Builder<?>)
                        ItemEntry.builder(item)
                                .apply(SetCountLootFunction
                                        .builder(UniformLootNumberProvider
                                                .create(2.0f, 5.0f))))
                        .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }
}

