
package pl.ultraperformance;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PerformanceScreen extends Screen {
    private PerformanceConfig config;

    public PerformanceScreen() {
        super(Text.literal("Ultra Performance 0.2.0"));
    }

    @Override
    protected void init() {
        MinecraftClient client = this.client;
        if (client == null) {
            return;
        }

        config = PerformanceConfig.load(client);

        int centerX = width / 2;
        int left = centerX - 101;
        int right = centerX + 3;
        int y = Math.max(55, height / 8);

        addDrawableChild(ButtonWidget.builder(
                Text.literal("BALANCED"),
                button -> selectPreset(PerformanceConfig.Preset.BALANCED)
        ).dimensions(left, y, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("PERFORMANCE"),
                button -> selectPreset(PerformanceConfig.Preset.PERFORMANCE)
        ).dimensions(right, y, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("POTATO"),
                button -> selectPreset(PerformanceConfig.Preset.POTATO)
        ).dimensions(left, y + 24, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("CUSTOM"),
                button -> selectPreset(PerformanceConfig.Preset.CUSTOM)
        ).dimensions(right, y + 24, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
                viewDistanceLabel(),
                button -> {
                    config.cycleViewDistance(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 52, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                particlesLabel(),
                button -> {
                    config.cycleParticles(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 76, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                cloudsLabel(client),
                button -> {
                    config.cycleClouds(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 100, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                graphicsLabel(client),
                button -> {
                    config.cycleGraphics(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 124, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                shadowsLabel(client),
                button -> {
                    config.toggleEntityShadows(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 148, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                fpsLabel(client),
                button -> {
                    config.cycleMaxFps(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 172, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                vsyncLabel(client),
                button -> {
                    config.toggleVsync(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 196, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                overlayLabel(),
                button -> {
                    config.toggleFpsOverlay(client);
                    refresh();
                }
        ).dimensions(centerX - 101, y + 220, 202, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Resetuj ustawienia"),
                button -> {
                    config.resetDefaults(client);
                    refresh();
                }
        ).dimensions(left, y + 248, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Zamknij"),
                button -> {
                    config.save(client);
                    client.options.write();
                    client.setScreen(null);
                }
        ).dimensions(right, y + 248, 98, 20).build());
    }

    private void selectPreset(PerformanceConfig.Preset preset) {
        if (client == null) {
            return;
        }

        config.applyPreset(client, preset);
        refresh();
    }

    private void refresh() {
        if (client != null) {
            client.setScreen(new PerformanceScreen());
        }
    }

    private Text viewDistanceLabel() {
        return Text.literal(
                "Render distance: " + config.viewDistance + " chunks"
        );
    }

    private Text particlesLabel() {
        return Text.literal("Cząsteczki: " + config.getParticlesName());
    }

    private Text cloudsLabel(MinecraftClient client) {
        CloudRenderMode mode =
                client.options.getCloudRenderMode().getValue();

        String value;
        if (mode == CloudRenderMode.OFF) {
            value = "Wyłączone";
        } else if (mode == CloudRenderMode.FAST) {
            value = "Szybkie";
        } else {
            value = "Dokładne";
        }

        return Text.literal("Chmury: " + value);
    }

    private Text graphicsLabel(MinecraftClient client) {
        GraphicsMode mode =
                client.options.getGraphicsMode().getValue();

        String value;
        if (mode == GraphicsMode.FAST) {
            value = "Szybka";
        } else if (mode == GraphicsMode.FANCY) {
            value = "Dokładna";
        } else {
            value = "Fabulous";
        }

        return Text.literal("Grafika: " + value);
    }

    private Text shadowsLabel(MinecraftClient client) {
        boolean enabled =
                client.options.getEntityShadows().getValue();

        return Text.literal(
                "Cienie encji: " + (enabled ? "Włączone" : "Wyłączone")
        );
    }

    private Text fpsLabel(MinecraftClient client) {
        int fps = client.options.getMaxFps().getValue();

        return Text.literal("Limit FPS: " + fps);
    }

    private Text vsyncLabel(MinecraftClient client) {
        boolean enabled =
                client.options.getEnableVsync().getValue();

        return Text.literal(
                "VSync: " + (enabled ? "Włączony" : "Wyłączony")
        );
    }

    private Text overlayLabel() {
        return Text.literal(
                "Licznik FPS: "
                        + (config.showFpsOverlay ? "Włączony" : "Wyłączony")
        );
    }

    @Override
    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY,
            float delta
    ) {
        renderBackground(matrices);

        String title = "Ultra Performance 0.2.0";
        String subtitle = "Preset: " + config.getPresetName();

        textRenderer.draw(
                matrices,
                title,
                width / 2.0F - textRenderer.getWidth(title) / 2.0F,
                20,
                0xFFFFFF
        );

        textRenderer.draw(
                matrices,
                subtitle,
                width / 2.0F - textRenderer.getWidth(subtitle) / 2.0F,
                36,
                0xAAAAAA
        );

        super.render(matrices, mouseX, mouseY, delta);
    }
}

