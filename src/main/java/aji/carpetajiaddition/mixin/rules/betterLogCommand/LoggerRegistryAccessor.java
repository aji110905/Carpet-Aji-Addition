package aji.carpetajiaddition.mixin.rules.betterLogCommand;

import carpet.logging.LoggerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LoggerRegistry.class)
public interface LoggerRegistryAccessor {
    @Accessor("playerSubscriptions")
    Map<String, Map<String, String>> getPlayerSubscriptions();
}
