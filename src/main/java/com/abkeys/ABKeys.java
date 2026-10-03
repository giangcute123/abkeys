package com.abkeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ABKeys implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("abkeys", "main"));

    private record Bind(KeyMapping key, String command) {}

    private static final List<Bind> BINDS = new ArrayList<>();
    private static KeyMapping guiKey;

    private static KeyMapping make(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping("key.abkeys." + name, InputConstants.Type.KEYSYM, key, CATEGORY));
    }

    private static void bind(String name, int key, String command) {
        BINDS.add(new Bind(make(name, key), command));
    }

    @Override
    public void onInitializeClient() {
        bind("start", GLFW.GLFW_KEY_G, "buildfarm start");
        bind("cancel", GLFW.GLFW_KEY_H, "buildfarm cancel");
        bind("resume", GLFW.GLFW_KEY_J, "buildfarm resume");
        bind("status", GLFW.GLFW_KEY_K, "buildfarm status");
        bind("list", GLFW.GLFW_KEY_L, "buildfarm list");
        bind("autoeat", GLFW.GLFW_KEY_V, "buildfarm autoeat toggle");
        guiKey = make("gui", GLFW.GLFW_KEY_B);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;
            for (Bind b : BINDS) {
                while (b.key().consumeClick()) {
                    mc.player.connection.sendCommand(b.command());
                }
            }
            while (guiKey.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(new ControlScreen());
                }
            }
        });
    }
}
