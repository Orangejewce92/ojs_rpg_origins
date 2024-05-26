package net.orangejewce.ojs_rpg_origins;

import net.fabricmc.api.ModInitializer;

import net.orangejewce.ojs_rpg_origins.block.ModBlocks;
import net.orangejewce.ojs_rpg_origins.item.ModItemGroups;
import net.orangejewce.ojs_rpg_origins.item.ModItems;
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
	}
}