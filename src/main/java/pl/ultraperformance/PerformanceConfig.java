
package pl.ultraperformance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceConfig {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static volatile int requestedViewDistance = 2;
    private static volatile boolean fpsOverlayEnabled = true;

    public enum Preset {
        BALANCED, PERFORMANCE, POTATO, CUSTOM
    }

    Preset preset = Preset.CUSTOM;
    int viewDistance = 8;
    ParticlesMode particlesMode = ParticlesMode.DECREASED;
    boolean showFpsOverlay = true;

    public static int getRequestedViewDistance() {
        return requestedViewDistance;
    }

    public static boolean isFpsOverlayEnabled() {
        return fpsOverlayEnabled;
    }

    private static Path getPath(MinecraftClient client) {
        return client.runDirectory.toPath()
                .resolve("config")
                .resolve("ultra-performance.json");
    }

    public static PerformanceConfig load(MinecraftClient client) {
        Path path = getPath(client);
        PerformanceConfig config = null;

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                config = GSON.fromJson(reader, PerformanceConfig.class);
            } catch (IOException | RuntimeException exception) {
                System.err.println(
                        "[Ultra Performance] Config load failed: "
                                + exception.getMessage()
                );
            }
        }

        if (config == null) {
            config = new PerformanceConfig();
            config.viewDistance =
                    client.options.getViewDistance().getValue();
            config.particlesMode =
                    client.options.getParticles().getValue();
        }

        if (config.preset == null) {
            config.preset = Preset.CUSTOM;
        }

        if (config.particlesMode == null) {
            config.particlesMode =
                    client.options.getParticles().getValue();
        }

        if (config.viewDistance < 1 || config.viewDistance > 12) {
            config.viewDistance = 8;
        }

        config.showFpsOverlay = config.showFpsOverlay;
        config.applyToMinecraft(client);

        return config;
    }

    void applyPreset(MinecraftClient client, Preset selected) {
        preset = selected;

        switch (selected) {
            case BALANCED:
                viewDistance = 8;
                particlesMode = ParticlesMode.DECREASED;
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getCloudRenderMode().setValue(
                        CloudRenderMode.FAST
                );
                client.options.getEntityShadows().setValue(false);
                client.options.getMaxFps().setValue(60);
                client.options.getEnableVsync().setValue(false);
                break;

            case PERFORMANCE:
                viewDistance = 4;
                particlesMode = ParticlesMode.MINIMAL;
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getCloudRenderMode().setValue(
                        CloudRenderMode.OFF
                );
                client.options.getEntityShadows().setValue(false);
                client.options.getMaxFps().setValue(60);
                client.options.getEnableVsync().setValue(false);
                break;

            case POTATO:
                viewDistance = 2;
                particlesMode = ParticlesMode.MINIMAL;
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getCloudRenderMode().setValue(
                        CloudRenderMode.OFF
                );
                client.options.getEntityShadows().setValue(false);
                client.options.getMaxFps().setValue(30);
                client.options.getEnableVsync().setValue(false);
                break;

            case CUSTOM:
                // Keep the user's current settings.
                break;
        }

        applyToMinecraft(client);
        save(client);
    }

    void cycleViewDistance(MinecraftClient client) {
        viewDistance = viewDistance >= 12 ? 1 : viewDistance + 1;
        preset = Preset.CUSTOM;
        applyToMinecraft(client);
        save(client);
    }

    void cycleParticles(MinecraftClient client) {
        switch (particlesMode) {
            case ALL:
                particlesMode = ParticlesMode.DECREASED;
                break;
            case DECREASED:
                particlesMode = ParticlesMode.MINIMAL;
                break;
            case MINIMAL:
                particlesMode = ParticlesMode.ALL;
                break;
        }

        preset = Preset.CUSTOM;
        applyToMinecraft(client);
        save(client);
    }

    void cycleClouds(MinecraftClient client) {
        CloudRenderMode current =
                client.options.getCloudRenderMode().getValue();

        if (current == CloudRenderMode.OFF) {
            client.options.getCloudRenderMode().setValue(
                    CloudRenderMode.FAST
            );
        } else if (current == CloudRenderMode.FAST) {
            client.options.getCloudRenderMode().setValue(
                    CloudRenderMode.FANCY
            );
        } else {
            client.options.getCloudRenderMode().setValue(
                    CloudRenderMode.OFF
            );
        }

        markCustom(client);
    }

    void cycleGraphics(MinecraftClient client) {
        GraphicsMode current =
                client.options.getGraphicsMode().getValue();

        if (current == GraphicsMode.FAST) {
            client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        } else if (current == GraphicsMode.FANCY) {
            client.options.getGraphicsMode().setValue(
                    GraphicsMode.FABULOUS
            );
        } else {
            client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
        }

        markCustom(client);
    }

    void toggleEntityShadows(MinecraftClient client) {
        boolean enabled =
                client.options.getEntityShadows().getValue();

        client.options.getEntityShadows().setValue(!enabled);
        markCustom(client);
    }

    void cycleMaxFps(MinecraftClient client) {
        int current = client.options.getMaxFps().getValue();

        int next;
        if (current <= 30) {
            next = 60;
        } else if (current <= 60) {
            next = 120;
        } else {
            next = 30;
        }

        client.options.getMaxFps().setValue(next);
        markCustom(client);
    }

    void toggleVsync(MinecraftClient client) {
        boolean enabled =
                client.options.getEnableVsync().getValue();

        client.options.getEnableVsync().setValue(!enabled);
        markCustom(client);
    }

    void toggleFpsOverlay(MinecraftClient client) {
        showFpsOverlay = !showFpsOverlay;
        fpsOverlayEnabled = showFpsOverlay;
        preset = Preset.CUSTOM;
        save(client);
    }

    void resetDefaults(MinecraftClient client) {
        showFpsOverlay = true;
        fpsOverlayEnabled = true;
        applyPreset(client, Preset.BALANCED);
    }

    private void markCustom(MinecraftClient client) {
        preset = Preset.CUSTOM;
        client.options.write();
        save(client);
    }

    void applyToMinecraft(MinecraftClient client) {
        viewDistance = Math.max(1, Math.min(12, viewDistance));
        requestedViewDistance = viewDistance;
        fpsOverlayEnabled = showFpsOverlay;

        // Vanilla clamps its standard option to at least 2.
        // The mixin below handles the special 1-chunk case.
        client.options.getViewDistance().setValue(
                Math.max(2, viewDistance)
        );

        client.options.getParticles().setValue(particlesMode);
        client.options.write();
    }

    void save(MinecraftClient client) {
        Path path = getPath(client);

        try {
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException exception) {
            System.err.println(
                    "[Ultra Performance] Config save failed: "
                            + exception.getMessage()
            );
        }
    }

    String getPresetName() {
        return preset.name();
    }

    String getParticlesName() {
        switch (particlesMode) {
            case ALL:
                return "Wszystkie";
            case DECREASED:
                return "Zmniejszone";
            case MINIMAL:
                return "Minimalne";
            default:
                return "Nieznane";
        }
    }
}
