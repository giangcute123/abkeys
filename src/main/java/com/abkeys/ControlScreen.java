package com.abkeys;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class ControlScreen extends Screen {
    private static final int BW = 100, BH = 18, GAP = 3, STEP_W = 20;
    private static final int W = BW * 3 + GAP * 2;

    private int speed = 5, leaf = 8, eat = 16;
    private float reach = 3.8F;
    private boolean autoEat = true;

    private String last = "";
    private int left, titleY, lastY;

    public ControlScreen() {
        super(Component.literal("AutoBuilder"));
        loadConfig();
    }

    private void loadConfig() {
        try {
            Path p = FabricLoader.getInstance().getConfigDir().resolve("autobuildfarm.json");
            if (!Files.exists(p)) return;
            try (BufferedReader r = Files.newBufferedReader(p)) {
                JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
                if (o.has("placeSpeed")) speed = o.get("placeSpeed").getAsInt();
                if (o.has("leafPlaceSpeed")) leaf = o.get("leafPlaceSpeed").getAsInt();
                if (o.has("placeReach")) reach = o.get("placeReach").getAsFloat();
                if (o.has("autoEatEnabled")) autoEat = o.get("autoEatEnabled").getAsBoolean();
                if (o.has("autoEatThreshold")) eat = o.get("autoEatThreshold").getAsInt();
            }
        } catch (Throwable ignored) {
        }
    }

    private void send(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.connection.sendCommand(command);
        }
        last = "Đã gửi: /" + command;
    }

    private void cmd(String label, String command, int col, int y) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> {
            send(command);
            onClose();
        }).bounds(left + col * (BW + GAP), y, BW, BH).build());
    }

    private void row(int y, Supplier<String> label, IntConsumer step) {
        Button center = Button.builder(Component.literal(label.get()), b -> {})
                .bounds(left + STEP_W + GAP, y, W - 2 * (STEP_W + GAP), BH).build();
        center.active = false;
        addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            step.accept(-1);
            center.setMessage(Component.literal(label.get()));
        }).bounds(left, y, STEP_W, BH).build());
        addRenderableWidget(center);
        addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            step.accept(1);
            center.setMessage(Component.literal(label.get()));
        }).bounds(left + W - STEP_W, y, STEP_W, BH).build());
    }

    @Override
    protected void init() {
        left = width / 2 - W / 2;
        int rowH = BH + GAP;
        int total = 14 + 3 * rowH + rowH + 4 * rowH + 12;
        int y = Math.max(2, (height - total) / 2);
        titleY = y;
        y += 14;

        cmd("Bắt đầu", "buildfarm start", 0, y);
        cmd("Tiếp tục", "buildfarm resume", 1, y);
        cmd("Hủy", "buildfarm cancel", 2, y);
        y += rowH;
        cmd("Trạng thái", "buildfarm status", 0, y);
        cmd("Danh sách", "buildfarm list", 1, y);
        cmd("Quét", "buildfarm scan", 2, y);
        y += rowH;
        cmd("Lấy đồ", "buildfarm restock", 0, y);
        cmd("Vào lại", "buildfarm rejoin", 1, y);
        cmd("Log", "buildfarm log", 2, y);
        y += rowH;

        Button eatToggle = Button.builder(Component.literal(autoEatText()), b -> {
            send("buildfarm autoeat toggle");
            autoEat = !autoEat;
            b.setMessage(Component.literal(autoEatText()));
        }).bounds(left, y, BW * 2 + GAP, BH).build();
        addRenderableWidget(eatToggle);
        addRenderableWidget(Button.builder(Component.literal("Đóng"), b -> onClose())
                .bounds(left + 2 * (BW + GAP), y, BW, BH).build());
        y += rowH;

        row(y, () -> "Tốc độ đặt: " + speed + " tick", d -> {
            speed = Math.max(0, Math.min(40, speed + d));
            send("buildfarm speed " + speed);
        });
        y += rowH;
        row(y, () -> "Tốc độ lá: " + leaf + " tick", d -> {
            leaf = Math.max(1, Math.min(40, leaf + d));
            send("buildfarm leafspeed " + leaf);
        });
        y += rowH;
        row(y, () -> "Tầm đặt: " + fmt(reach), d -> {
            reach = Math.max(1.5F, Math.min(5.0F, Math.round((reach + d * 0.1F) * 10F) / 10F));
            send("buildfarm reach " + fmt(reach));
        });
        y += rowH;
        row(y, () -> "Ngưỡng ăn: " + eat, d -> {
            eat = Math.max(1, Math.min(19, eat + d));
            send("buildfarm autoeat " + eat);
        });
        y += rowH;
        lastY = y + 2;
    }

    private String autoEatText() {
        return "Tự ăn: " + (autoEat ? "BẬT" : "TẮT");
    }

    private static String fmt(float v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, width / 2, titleY, 0xFFFFFFFF);
        if (!last.isEmpty()) {
            g.drawCenteredString(this.font, Component.literal(last), width / 2, lastY, 0xFFAAAAAA);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
