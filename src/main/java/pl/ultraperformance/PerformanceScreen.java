
package pl.ultraperformance;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PerformanceScreen extends Screen {
    private int selectedViewDistance;
    private boolean minimalParticles;

    protected PerformanceScreen() {
        super(Text.literal("Ultra Performance"));
    }

    @Override
    protected void init() {
        selectedViewDistance =
            this.client.options.getViewDistance().getValue();

        minimalParticles =
            this.client.options.getParticles().getValue()
                == ParticlesMode.MINIMAL;

        int centerX = this.width / 2;
        int y = this.height / 4;

        this.addDrawableChild(
            ButtonWidget.builder(viewDistanceLabel(), button -> {
                selectedViewDistance =
                    selectedViewDistance >= 12
                        ? 2
                        : selectedViewDistance + 2;

                this.client.options.getViewDistance()
                    .setValue(selectedViewDistance);

                button.setMessage(viewDistanceLabel());
            }).dimensions(centerX - 100, y, 200, 20).build()
        );

        this.addDrawableChild(
            ButtonWidget.builder(particlesLabel(), button -> {
                minimalParticles = !minimalParticles;

                this.client.options.getParticles().setValue(
                    minimalParticles
                        ? ParticlesMode.MINIMAL
                        : ParticlesMode.ALL
                );

                button.setMessage(particlesLabel());
            }).dimensions(centerX - 100, y + 26, 200, 20).build()
        );

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Zamknij"), button -> {
                this.client.setScreen(null);
            }).dimensions(centerX - 100, y + 60, 200, 20).build()
        );
    }

    private Text viewDistanceLabel() {
        return Text.literal(
            "Render distance: " + selectedViewDistance + " chunks"
        );
    }

    private Text particlesLabel() {
        return Text.literal(
            "Cząsteczki: " +
                (minimalParticles ? "Minimalne" : "Wszystkie")
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

        this.textRenderer.draw(
            matrices,
            this.title,
            this.width / 2.0F
                - this.textRenderer.getWidth(this.title) / 2.0F,
            this.height / 4.0F - 30,
            0xFFFFFF
        );

        super.render(matrices, mouseX, mouseY, delta);
    }
}
