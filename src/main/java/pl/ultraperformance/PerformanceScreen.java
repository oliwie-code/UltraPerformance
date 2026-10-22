
package pl.ultraperformance;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.text.Text;

public class PerformanceScreen extends Screen {
    private int selectedViewDistance;
    private boolean minimalParticles;

    protected PerformanceScreen() {
        super(Text.literal("Ultra Performance"));
    }

    @Override
    protected void init() {
        selectedViewDistance = this.client.options.getViewDistance();
        minimalParticles =
            this.client.options.getParticles() == ParticlesMode.MINIMAL;

        int centerX = this.width / 2;
        int y = this.height / 4;

        this.addDrawableChild(
            ButtonWidget.builder(viewDistanceLabel(), button -> {
                selectedViewDistance =
                    selectedViewDistance >= 12
                    ? 2
                    : selectedViewDistance + 2;

                this.client.options.setViewDistance(selectedViewDistance);
                button.setMessage(viewDistanceLabel());
            }).dimensions(centerX - 100, y, 200, 20).build()
        );

        this.addDrawableChild(
            ButtonWidget.builder(particlesLabel(), button -> {
                minimalParticles = !minimalParticles;

                this.client.options.setParticles(
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
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            this.height / 4 - 30,
            0xFFFFFF
        );
        super.render(context, mouseX, mouseY, delta);
    }
}
