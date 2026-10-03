package com.abkeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ABKeys implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("abkeys", "main"));

    private static KeyMapping start, cancel, resume;

    private static KeyMapping make(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.abkeys." + name, InputConstants.Type.KEYSYM, key, CATEGORY));
    }

    @Override
    public void onInitializeClient() {
        start = make("start", GLFW.GLFW_KEY_G);
        cancel = make("cancel", GLFW.GLFW_KEY_H);
        resume = make("resume", GLFW.GLFW_KEY_J);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;
            while (start.consumeClick()) run(mc, "buildfarm start");
            while (cancel.consumeClick()) run(mc, "buildfarm cancel");
            while (resume.consumeClick()) run(mc, "buildfarm resume");
        });
    }

    private static void run(Minecraft mc, String cmd) {
        mc.player.connection.sendCommand(cmd);
    }
}
