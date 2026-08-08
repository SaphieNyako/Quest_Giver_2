package com.saphienyako.quest_giver.quest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLineData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;

public class QuestCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("quest_giver").requires(source -> source.hasPermission(2))
                .then(Commands.literal("complete")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("quest_line", StringArgumentType.word())
                                        .then(Commands.argument("quest", StringArgumentType.word())
                                                .executes(QuestCommands::completeQuest)))))

                .then(Commands.literal("next")
                                .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("quest_line", StringArgumentType.word())
                                                                .executes(QuestCommands::nextQuest))))

                .then(Commands.literal("restart")
                                .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("quest_line", StringArgumentType.word())
                                                                .executes(QuestCommands::restartQuestLine))))

                .then(Commands.literal("restart_all")
                                .then(Commands.argument("player", EntityArgument.player())
                                                .executes(QuestCommands::restartAll)))

             /*   .then(Commands.literal("end")
                                .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("quest_line", StringArgumentType.word())
                                                                .executes(QuestCommands::endQuestLine)))) */

                .then(Commands.literal("show")
                        .then(Commands.literal("active")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(QuestCommands::showActiveQuests)))
                )
        );
    }

    /**
     *  force complete
     *  /quest_giver complete <player> <quest_line> <quest>
     * force-completes one active quest
     * keeps its normal completion screen/rewards
     * unlocks following quests
     */

    private static int completeQuest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String questLineId = StringArgumentType.getString(context, "quest_line");
        String questInput = StringArgumentType.getString(context, "quest");

        ResourceLocation questId = questInput.contains(":") ? ResourceLocation.parse(questInput) : ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, questInput);

        QuestLineData line = QuestData.get(player).getQuestLine(questLineId);

        if (line == null) {
            context.getSource().sendFailure(Component.literal("Player does not have Quest Line: " + questLineId));
            return 0;
        }

        if (!line.forceCompleteQuest(questId)) {
            context.getSource().sendFailure(Component.literal("Quest is not currently active: " + questId));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Completed " + questId + " for " + player.getName().getString()), true);
        return 1;
    }

    /**
     * skips the currently active quest(s)
     * /quest_giver next <player> <quest_line>
     * no rewards/completion page
     * unlocks whatever comes next
     */

    private static int nextQuest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String questLineId = StringArgumentType.getString(context, "quest_line");

        QuestLineData line = QuestData.get(player).getQuestLine(questLineId);

        if (line == null || !line.skipToNextQuests()) {
            context.getSource().sendFailure(Component.literal("No active quests to skip."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Advanced " + player.getName().getString() + " to the next quest."), true);
        return 1;
    }

    /**
     * resets one questline to its beginning
     * /quest_giver restart <player> <quest_line>
     */

    private static int restartQuestLine(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String id = StringArgumentType.getString(context, "quest_line");

        if (!QuestData.get(player).restartQuestLine(id)) {

            context.getSource().sendFailure(Component.literal("Questline not found: " + id));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Restarted questline " + id + " for " + player.getName().getString()), true);
        return 1;
    }

    /**
     * resets all questlines to its beginning
     * /quest_giver restart_all <player>
     */

    private static int restartAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        QuestData.get(player).restartAllQuestLines();

        context.getSource().sendSuccess(() -> Component.literal("Restarted all questlines for " + player.getName().getString()), true);
        return 1;
    }

    /**
     * immediately ends one questline
     * next interaction shows end.json
     * /quest_giver end <player> <quest_line>
     */
    private static int endQuestLine(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String id = StringArgumentType.getString(context, "quest_line");

        if (!QuestData.get(player).endQuestLine(id)) {
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Ended questline " + id + " for " + player.getName().getString()), true);
        return 1;
    }

    private static int showActiveQuests(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        QuestData questData = QuestData.get(player);

        Map<String, QuestLineData> questLines = questData.getQuestLines();

        if (questLines.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal(player.getName().getString() + " has no quest lines."), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> Component.literal("Active quests for " + player.getName().getString() + ":"), false);

        int activeCount = 0;
        int completedCount = 0;


        for (Map.Entry<String, QuestLineData> entry : questLines.entrySet()) {

            String questLineId = entry.getKey();
            QuestLineData line = entry.getValue();

            Set<ResourceLocation> activeQuests = line.getActiveQuestIds();

            if (activeQuests.isEmpty()) {
                continue;
            }

            activeCount += activeQuests.size();
            context.getSource().sendSuccess(() -> Component.literal("Quest Line: " + questLineId), false);

            for (ResourceLocation questId : activeQuests) {
                context.getSource().sendSuccess(() -> Component.literal("   - Quest: " + questId.getPath()), false);
            }
        }

        if (activeCount == 0) {
            context.getSource().sendSuccess(() -> Component.literal("No active quests."), false);
        }

        // COMPLETED QUEST LINES
        context.getSource().sendSuccess(() -> Component.literal("Completed Quest Lines:"), false);

        for (Map.Entry<String, QuestLineData> entry : questLines.entrySet()) {

            String questLineId = entry.getKey();
            QuestLineData line = entry.getValue();

            if (!line.isFinished()) {
                continue;
            }

            completedCount++;

            context.getSource().sendSuccess(() -> Component.literal("  - " + questLineId), false);
        }

        if (completedCount == 0) {context.getSource().sendSuccess(() -> Component.literal("  None"), false);
        }

        return activeCount + completedCount;
    }

}
