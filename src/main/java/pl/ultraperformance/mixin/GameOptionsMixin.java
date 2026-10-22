
package pl.ultraperformance.mixin;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.ultraperformance.PerformanceConfig;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Inject(
            method = "getClampedViewDistance",
            at = @At("RETURN"),
            cancellable = true
    )
    private void ultraPerformance$allowOneChunk(
            CallbackInfoReturnable<Integer> cir
    ) {
        int requested =
                PerformanceConfig.getRequestedViewDistance();

        if (requested == 1 && cir.getReturnValue() > 1) {
            cir.setReturnValue(1);
        }
    }
}
