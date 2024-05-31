package net.orangejewce.ojs_rpg_origins;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.orangejewce.ojs_rpg_origins.Client.ModItemPropertiesClient;
import net.orangejewce.ojs_rpg_origins.block.ModBlocks;
import net.orangejewce.ojs_rpg_origins.item.ModItemGroups;
import net.orangejewce.ojs_rpg_origins.item.ModItems;
import net.orangejewce.ojs_rpg_origins.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OjsMod implements ModInitializer {
	public static final String MOD_ID = "ojs_rpg_origins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModWorldGeneration.generateModWorldGen();

	}

	public static void registerClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			ModItemPropertiesClient.addCustomItemProperties();
		});
	}
}
