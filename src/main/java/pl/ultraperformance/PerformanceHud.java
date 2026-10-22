
package pl.ultraperformance;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public final class PerformanceHud {
    private static long lastFrameNanos;
    private static double frameTimeMs;

    private PerformanceHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(PerformanceHud::render);
    }

    private static void render(MatrixStack matrices, float tickDelta) {
        if (!PerformanceConfig.isFpsOverlayEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options.hudHidden) {
            return;
        }

        long now = System.nanoTime();

        if (lastFrameNanos != 0L) {
            frameTimeMs = (now - lastFrameNanos) / 1_000_000.0;
        }

        lastFrameNanos = now;

        String text = "FPS: " + MinecraftClient.getInstance().getCurrentFps()
                + " | Frame: " + String.format("%.2f", frameTimeMs) + " ms";

        client.textRenderer.draw(
                matrices,
                text,
                6,
                6,
                0xFFFFFF
        );
    }
}
