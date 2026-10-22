
package pl.ultraperformance;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class UltraPerformanceClient implements ClientModInitializer {
    private KeyBinding openMenuKey;
    private boolean configLoaded;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.ultra_performance.open_menu",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_O,
                        "category.ultra_performance"
                )
        );

        PerformanceHud.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!configLoaded) {
                PerformanceConfig.load(client);
                configLoaded = true;
            }

            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new PerformanceScreen());
                }
            }
        });
    }
}
