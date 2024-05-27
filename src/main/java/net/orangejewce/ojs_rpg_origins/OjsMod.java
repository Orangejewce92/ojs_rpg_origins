package net.orangejewce.ojs_rpg_origins;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.orangejewce.ojs_rpg_origins.block.ModBlocks;
import net.orangejewce.ojs_rpg_origins.config.ThiefGloveConfig;
import net.orangejewce.ojs_rpg_origins.item.ModItemGroups;
import net.orangejewce.ojs_rpg_origins.item.ModItems;
import net.orangejewce.ojs_rpg_origins.item.custom.SapphireMiseryItem;
import net.orangejewce.ojs_rpg_origins.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OjsMod implements ModInitializer {
	public static final String MOD_ID = "ojs_rpg_origins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize(){
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ThiefGloveConfig.register();
		ModWorldGeneration.generateModWorldGen();


		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				SapphireMiseryItem.tickPlayer(player);
			}
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof PlayerEntity) {
				SapphireMiseryItem.onLivingHurt((LivingEntity) entity, null, 0);
			}
		});
	}
}
