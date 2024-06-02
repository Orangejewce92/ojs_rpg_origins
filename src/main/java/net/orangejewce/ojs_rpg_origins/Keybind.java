package net.orangejewce.ojs_rpg_origins;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = OJs_OriginMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Keybind {
    public static KeyMapping TERTIARY_ACTIVE_KEY;
    public static KeyMapping QUATERNARY_ACTIVE_KEY;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        TERTIARY_ACTIVE_KEY = new KeyMapping("key.ojs_rpg_origins.tertiary_active", GLFW.GLFW_KEY_Y, "key.categories.ojs_rpg_origins");
        QUATERNARY_ACTIVE_KEY = new KeyMapping("key.ojs_rpg_origins.quaternary_active", GLFW.GLFW_KEY_U, "key.categories.ojs_rpg_origins");
        event.register(TERTIARY_ACTIVE_KEY);
        event.register(QUATERNARY_ACTIVE_KEY);
    }
}
