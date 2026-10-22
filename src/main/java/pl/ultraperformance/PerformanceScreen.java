
package pl.ultraperformance;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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

        int centerX = this.width / 2;
        int leftX = centerX - 100;
        int rightX = centerX + 2;
        int topY = this.height / 4;

        // Presety: dwa przyciski w każdym wierszu.
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("BALANCED"), button ->
                        selectPreset(PerformanceConfig.Preset.BALANCED)
                ).dimensions(leftX, topY, 98, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("PERFORMANCE"), button ->
                        selectPreset(PerformanceConfig.Preset.PERFORMANCE)
                ).dimensions(rightX, topY, 98, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("POTATO"), button ->
                        selectPreset(PerformanceConfig.Preset.POTATO)
                ).dimensions(leftX, topY + 24, 98, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("CUSTOM"), button ->
                        selectPreset(PerformanceConfig.Preset.CUSTOM)
                ).dimensions(rightX, topY + 24, 98, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(viewDistanceLabel(), button -> {
                    config.cycleViewDistance(client);
                    refreshScreen();
                }).dimensions(leftX, topY + 52, 200, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(particlesLabel(), button -> {
                    config.cycleParticles(client);
                    refreshScreen();
                }).dimensions(leftX, topY + 78, 200, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Zapisz konfigurację"),
                        button -> {
                            config.save(client);
                            button.setMessage(Text.literal("Zapisano!"));
                        }
                ).dimensions(leftX, topY + 104, 200, 20).build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Zamknij"), button -> {
                    config.save(client);
                    client.options.write();
                    client.setScreen(null);
                }).dimensions(leftX, topY + 130, 200, 20).build()
        );
    }

    private void selectPreset(PerformanceConfig.Preset preset) {
        if (this.client == null) {
            return;
        }

        config.applyPreset(this.client, preset);
        refreshScreen();
    }

    private void refreshScreen() {
        if (this.client != null) {
            this.client.setScreen(new PerformanceScreen());
        }
    }

    private Text viewDistanceLabel() {
        return Text.literal(
                "Render distance: " + config.viewDistance + " chunks"
        );
    }

    private Text particlesLabel() {
        return Text.literal(
                "Cząsteczki: " + config.getParticlesName()
        );
    }

    @Override
    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY,
            float delta
    ) {
        this.renderBackground(matrices);

        String title = "Ultra Performance 0.2.0";
        String subtitle = "Preset: " + config.getPresetName();

        this.textRenderer.draw(
                matrices,
                title,
                this.width / 2.0F
                        - this.textRenderer.getWidth(title) / 2.0F,
                this.height / 4.0F - 30,
                0xFFFFFF
        );

        this.textRenderer.draw(
                matrices,
                subtitle,
                this.width / 2.0F
                        - this.textRenderer.getWidth(subtitle) / 2.0F,
                this.height / 4.0F - 16,
                0xAAAAAA
        );

        super.render(matrices, mouseX, mouseY, delta);
    }
}
