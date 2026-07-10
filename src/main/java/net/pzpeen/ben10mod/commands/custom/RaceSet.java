package net.pzpeen.ben10mod.commands.custom;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.RaceCapS2CPacket;
import net.pzpeen.ben10mod.races.RacesRegistries;


public class RaceSet {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RACE = (context, builder) -> {
        Iterable<String> RACES_IDS = RacesRegistries.getAllIDs();
        return SharedSuggestionProvider.suggest(RACES_IDS, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("raceset")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("raceId", StringArgumentType.greedyString())
                                .suggests(SUGGEST_RACE)
                                .executes(RaceSet::execute))));

    }

    public static int execute(CommandContext<CommandSourceStack> commandContext){
        try{
            ServerPlayer targetPlayer = EntityArgument.getPlayer(commandContext, "target");
            ResourceLocation raceID = ResourceLocation.parse(StringArgumentType.getString(commandContext, "raceId"));
            //ResourceLocation raceID = new ResourceLocation(StringArgumentType.getString(commandContext, "raceId"));

            RaceCap raceCap = ((IBen10ModCapCache)targetPlayer).ben10Mod$getCachedRaceCap();
            if(raceCap != null){
                raceCap.setRace(raceID, targetPlayer, true);
            }
            /*
            targetPlayer.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                raceCap.setRace(raceID, targetPlayer, true);
            });

             */

            ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceID, targetPlayer.getUUID()), commandContext.getSource().getPlayer());

            return 1;
        } catch (Exception e) {
            commandContext.getSource().sendFailure(Component.literal("ERROR ON SET RACE"));
            return 0;
        }

    }

}
