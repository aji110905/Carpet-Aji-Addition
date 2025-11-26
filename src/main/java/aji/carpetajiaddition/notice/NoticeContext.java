package aji.carpetajiaddition.notice;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public record NoticeContext(ServerPlayerEntity player, MinecraftServer server) {

}
