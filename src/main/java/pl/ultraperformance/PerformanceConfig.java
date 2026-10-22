
package pl.ultraperformance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceConfig {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    enum Preset {
        BALANCED,
        PERFORMANCE,
        POTATO,
        CUSTOM
    }

    Preset preset = Preset.CUSTOM;
    int viewDistance = 8;
    ParticlesMode particlesMode = ParticlesMode.DECREASED;

    private static Path getConfigPath(MinecraftClient client) {
        return client.runDirectory.toPath()
                .resolve("config")
                .resolve("ultra-performance.json");
    }

    static PerformanceConfig load(MinecraftClient client) {
        Path path = getConfigPath(client);

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                PerformanceConfig config =
                        GSON.fromJson(reader, PerformanceConfig.class);

                if (config != null) {
                    if (config.preset == null) {
                        config.preset = Preset.CUSTOM;
                    }

                    if (config.particlesMode == null) {
                        config.particlesMode =
                                client.options.getParticles().getValue();
                    }

                    config.viewDistance =
                            Math.max(2, Math.min(12, config.viewDistance));

                    config.applyToMinecraft(client);
                    return config;
                }
            } catch (IOException | RuntimeException exception) {
                System.err.println(
                        "[Ultra Performance] Nie udało się wczytać konfiguracji: "
                                + exception.getMessage()
                );
            }
        }

        PerformanceConfig config = new PerformanceConfig();
        config.viewDistance =
                client.options.getViewDistance().getValue();
        config.particlesMode =
                client.options.getParticles().getValue();

        return config;
    }

    void applyPreset(MinecraftClient client, Preset selectedPreset) {
        preset = selectedPreset;

        switch (selectedPreset) {
            case BALANCED:
                viewDistance = 8;
                particlesMode = ParticlesMode.DECREASED;
                break;

            case PERFORMANCE:
                viewDistance = 4;
                particlesMode = ParticlesMode.MINIMAL;
                break;

            case POTATO:
                viewDistance = 2;
                particlesMode = ParticlesMode.MINIMAL;
                break;

            case CUSTOM:
                // Zachowujemy własne ustawienia użytkownika.
                break;
        }

        applyToMinecraft(client);
        save(client);
    }

    void cycleViewDistance(MinecraftClient client) {
        viewDistance = viewDistance >= 12 ? 2 : viewDistance + 1;
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

    void applyToMinecraft(MinecraftClient client) {
        viewDistance = Math.max(2, Math.min(12, viewDistance));

        client.options.getViewDistance().setValue(viewDistance);
        client.options.getParticles().setValue(particlesMode);
        client.options.write();
    }

    void save(MinecraftClient client) {
        Path path = getConfigPath(client);

        try {
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException exception) {
            System.err.println(
                    "[Ultra Performance] Nie udało się zapisać konfiguracji: "
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
