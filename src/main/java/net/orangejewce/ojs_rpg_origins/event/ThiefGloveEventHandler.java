package net.orangejewce.ojs_rpg_origins.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import net.orangejewce.ojs_rpg_origins.item.ThiefGloveItem;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThiefGloveEventHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        LivingEntity target = (LivingEntity) event.getTarget();

        // Check if the player is crouching and the target is a Villager
        if (player.isCrouching() && target instanceof Villager) {
            // Check the Curios slots for the Thief Glove
            CuriosApi.getCuriosHelper().findFirstCurio(player, item -> item.getItem() instanceof ThiefGloveItem).ifPresent(triple -> {
                ItemStack thiefGlove = triple.stack();

                // Call the Thief Glove interaction method
                ThiefGloveItem thiefGloveItem = (ThiefGloveItem) thiefGlove.getItem();
                thiefGloveItem.interactWithVillager(thiefGlove, player, (Villager) target, event.getHand());

                // Cancel the event to prevent default interaction
                event.setCanceled(true);
            });
        }
    }

}
