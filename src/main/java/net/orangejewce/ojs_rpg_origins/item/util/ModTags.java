package net.orangejewce.ojs_rpg_origins.item.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.orangejewce.ojs_rpg_origins.OJs_OriginMod;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> VALUABLES = tag("valuables");
        public static final TagKey<Block> NEEDS_SAPPHIRE_TOOL = tag("needs_sapphire_tool");


        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(OJs_OriginMod.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(OJs_OriginMod.MOD_ID, name));
        }
    }
}
