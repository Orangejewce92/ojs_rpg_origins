package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.orangejewce.ojs_rpg_origins.OJs_OriginMod;
import net.orangejewce.ojs_rpg_origins.block.ModBlocks;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OJs_OriginMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NEW_TAB = CREATIVE_MODE_TABS.register("rpg_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHELM.get()))
                    .title(Component.translatable("creativetab.rpg_tab"))
                    .displayItems((pParameters, pOutput) -> {
                      pOutput.accept(ModItems.RAW_SAPPHIRE.get());
                      pOutput.accept(ModBlocks.SAPPHIRE_ORE.get());
                      pOutput.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
                      pOutput.accept(ModBlocks.NETHER_SAPPHIRE_ORE.get());
                      pOutput.accept(ModBlocks.END_STONE_SAPPHIRE_ORE.get());
                      pOutput.accept(ModItems.SAPPHIRE.get());
                      pOutput.accept(ModBlocks.RAW_SAPPHIRE_BLOCK.get());
                      pOutput.accept(ModBlocks.SAPPHIRE_BLOCK.get());
                      pOutput.accept(ModItems.WHELM.get());
                      pOutput.accept(ModItems.SAPPHIRE_STAFF.get());
                      pOutput.accept(ModItems.SCYTHE.get());
                      pOutput.accept(ModItems.LUTE.get());
                      pOutput.accept(ModItems.THIEF_GLOVE.get());

                      pOutput.accept(ModItems.SAPPHIRE_SWORD.get());
                      pOutput.accept(ModItems.SAPPHIRE_PICKAXE.get());
                      pOutput.accept(ModItems.SAPPHIRE_SHOVEL.get());
                      pOutput.accept(ModItems.SAPPHIRE_AXE.get());
                      pOutput.accept(ModItems.SAPPHIRE_MISERY.get());
                      pOutput.accept(ModItems.SAPPHIRE_HOE.get());
                      pOutput.accept(ModItems.SAPPHIRE_HELMET.get());
                      pOutput.accept(ModItems.SAPPHIRE_CHESTPLATE.get());
                      pOutput.accept(ModItems.SAPPHIRE_LEGGINGS.get());
                      pOutput.accept(ModItems.SAPPHIRE_BOOTS.get());




                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
