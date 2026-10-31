package top.ajitech.carpetajiaddition.command;

//#if MC < 12109
import com.mojang.authlib.GameProfile;
//#endif
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
//#if MC < 12109
import net.minecraft.server.players.GameProfileCache;
//#else
//$$ import net.minecraft.server.players.NameAndId;
//#endif
import net.minecraft.world.level.storage.LevelResource;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.util.FileUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static top.ajitech.carpetajiaddition.translate.TranslationsKey.CMD_PLAYERDATA;
import static top.ajitech.carpetajiaddition.util.TranslateUtil.trc;

public class PlayerdataCommand extends Command {
    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> getCommand(CommandBuildContext buildContext) {
        return literal("playerdata")
                .requires(CarpetAjiAdditionRules.COMMAND_PLAYERDATA::canUseCommand)
                .then(literal("remove")
                        .then(argument("player", StringArgumentType.greedyString())
                                .suggests(PlayerdataCommand::player)
                                .executes(PlayerdataCommand::remove)));
    }

    private static CompletableFuture<Suggestions> player(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        MinecraftServer server = context.getSource().getServer();
        HashSet<String> names = new HashSet<>();
        //#if MC < 12109
        server.getPlayerList().getPlayers().forEach(player -> names.add(player.getGameProfile().getName()));
        GameProfileCache profileCache = server.getProfileCache();
        if (profileCache != null) {
            ArrayList<Path> paths = new ArrayList<>();
            paths.add(server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR));
            paths.add(server.getWorldPath(LevelResource.PLAYER_STATS_DIR));
            paths.add(server.getWorldPath(LevelResource.PLAYER_DATA_DIR));
            HashSet<String> uuids = new HashSet<>();
            for (Path path : paths) {
                try (Stream<Path> stream = Files.list(path)) {
                    stream.filter(Files::isRegularFile).map(FileUtil::getNameWithoutExtension).forEach(uuids::add);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            uuids.forEach(uuid -> profileCache.get(UUID.fromString(uuid)).ifPresent(profile -> names.add(profile.getName())));
        }
        //#else
        //$$ server.getPlayerList().getPlayers().forEach(player -> names.add(player.getGameProfile().name()));
        //$$ ArrayList<Path> paths = new ArrayList<>();
        //$$ paths.add(server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR));
        //$$ paths.add(server.getWorldPath(LevelResource.PLAYER_STATS_DIR));
        //$$ paths.add(server.getWorldPath(LevelResource.PLAYER_DATA_DIR));
        //$$ HashSet<String> uuids = new HashSet<>();
        //$$ for (Path path : paths) {
        //$$     try (Stream<Path> stream = Files.list(path)) {
        //$$         stream.filter(Files::isRegularFile).map(FileUtil::getNameWithoutExtension).forEach(uuids::add);
        //$$     } catch (IOException e) {
        //$$         throw new UncheckedIOException(e);
        //$$     }
        //$$ }
        //$$ uuids.forEach(uuid ->  server.services().nameToIdCache().get(UUID.fromString(uuid)).ifPresent(profile -> names.add(profile.name())));
        //#endif
        names.forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        //#if MC < 12109
        GameProfileCache profileCache = server.getProfileCache();
        if (profileCache == null) {
            return 0;
        }
        String name = StringArgumentType.getString(context, "player");
        GameProfile profile = profileCache.get(name).orElse(null);
        if (profile == null) {
            source.sendFailure(trc(CMD_PLAYERDATA + "remove.error.not_exist", name));
            return 0;
        }
        UUID uuid = profile.getId();
        //#else
        //$$ String name = StringArgumentType.getString(context, "player");
        //$$ NameAndId nameAndId = server.services().nameToIdCache().get(name).orElse(null);
        //$$ if (nameAndId == null) {
        //$$     source.sendFailure(trc(CMD_PLAYERDATA + "remove.error.not_exist", name));
        //$$     return 0;
        //$$ }
        //$$ UUID uuid = nameAndId.id();
        //#endif
        if (server.getPlayerList().getPlayer(uuid) != null) {
            source.sendFailure(trc(CMD_PLAYERDATA + "remove.error.online"));
            return 0;
        }
        try {
            Files.deleteIfExists(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(uuid + ".dat"));
            Files.deleteIfExists(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(uuid + ".dat_old"));
            Files.deleteIfExists(server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(uuid + ".json"));
            Files.deleteIfExists(server.getWorldPath(LevelResource.PLAYER_STATS_DIR).resolve(uuid + ".json"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        source.sendSuccess(() -> trc(CMD_PLAYERDATA + "remove.feedback", name), true);
        return 1;
    }
}
