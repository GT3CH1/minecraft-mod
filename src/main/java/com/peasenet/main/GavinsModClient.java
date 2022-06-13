package com.peasenet.main;

import com.peasenet.mixinterface.IMinecraftClient;
import com.peasenet.mods.Mod;
import com.peasenet.util.RenderUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * @author gt3ch1
 * @version 5/15/2022
 */
public class GavinsModClient implements ClientModInitializer {
    public static double LAST_GAMMA = 0.0;
    @Override
    public void onInitializeClient() {
        GavinsMod.LOGGER.info("GavinsMod keybinding initialized");
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            for (Mod m : GavinsMod.mods) {
                m.onTick();
            }
        });
        WorldRenderEvents.AFTER_ENTITIES.register(RenderUtils::afterEntities);
    }

    public static IMinecraftClient getMinecraftClient() {
        return (IMinecraftClient) (Object) MinecraftClient.getInstance();
    }

    public static ClientPlayerEntity getPlayer() {
        return getMinecraftClient().getPlayer();
    }
}
