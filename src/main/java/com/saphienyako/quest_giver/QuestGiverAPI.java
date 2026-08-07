package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.network.OpenQuestDisplayMessage;
import com.saphienyako.quest_giver.network.OpenQuestSelectionMessage;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLineData;
import com.saphienyako.quest_giver.quest.task.GiftTask;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class QuestGiverAPI {


    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId) {
        interactQuest(player, entityId, displayName, hand, questLineId, false, 1.5);
    }

    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId, double scale) {
        interactQuest(player, entityId, displayName, hand, questLineId, false, scale);
    }

    /**
     * Interact with a quest-giving entity.
     * Opens quest completion screen, active quest screen, or initializes a new quest.
     *
     * @param player        The interacting player
     * @param entityId      The ID of the entity giving the quest
     * @param displayName   The name to show in the GUI (usually entity.getDisplayName())
     * @param hand          The hand used for interaction
     * @param dismiss       Should entity be dismissed when quest window closes
     * @param scale         Scale of the entity, based on; screen height / entity.height * scale
     */
    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId, boolean dismiss, double scale) {

        QuestData questData = QuestData.get(player);

        QuestLineData line = questData.getQuestLine(questLineId);

        // If the questline already exists
        if (line != null) {

            //Complete pending quests
            QuestDisplay completionDisplay = line.completePendingQuest();

            if (completionDisplay != null) {
                sendQuestDisplay(player, completionDisplay, false, entityId, questLineId, "quest_giver", dismiss, scale);
                player.swing(hand, true);
                return;
            }

            //Show active quests
            List<SelectableQuest> active = line.getActiveQuests();

            if (!active.isEmpty()) {

                if (active.size() == 1) {
                    sendQuestDisplay(player, active.get(0).display(), false, entityId, questLineId, "quest_giver", dismiss, scale);
                } else {
                    sendQuestSelection(player, displayName, active, entityId, questLineId, "quest_giver", dismiss, scale);
                }

                player.swing(hand, true);
                return;
            }
        }

        //Initialize the questline
        QuestDisplay initDisplay = questData.initialize(questLineId);

        QuestGiver.LOGGER.info("Initiating Quest: {}", initDisplay);

        if (initDisplay != null) {
            sendQuestDisplay(player, initDisplay, true, entityId, questLineId, "quest_giver", dismiss, scale);
            player.swing(hand, true);
        }
    }

    /**
     * Interact with a quest-giving entity.
     * Optional give in a backgroundName of the costum background gui image.
     * This should be placed in the quest_giver/textures/gui map.
     * backgroundName + "_background_01.png"
     * backgroundName + "_background_02.png"
     * backgroundName + "_background_03.png"
     * backgroundName + "_button.png"
     * backgroundName + "_button_small.png"
     * @param backgroundName          name of the background images
     */

    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId, String backgroundName){
        interactQuest(player, entityId, displayName, hand, questLineId, backgroundName, false, 1.5);
    }

    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId, String backgroundName, double scale){
        interactQuest(player, entityId, displayName, hand, questLineId, backgroundName, false, scale);
    }

    public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, String questLineId, String backgroundName, boolean dismiss, double scale) {

        QuestData questData = QuestData.get(player);

        QuestLineData line = questData.getQuestLine(questLineId);

        // If the questline already exists
        if (line != null) {

            //Complete pending quests
            QuestDisplay completionDisplay = line.completePendingQuest();

            if (completionDisplay != null) {
                sendQuestDisplay(player, completionDisplay, false, entityId, questLineId, backgroundName, dismiss, scale);
                player.swing(hand, true);
                return;
            }

            //Show active quests
            List<SelectableQuest> active = line.getActiveQuests();

            if (!active.isEmpty()) {

                if (active.size() == 1) {
                    sendQuestDisplay(player, active.get(0).display(), false, entityId, questLineId, backgroundName, dismiss, scale);
                } else {
                    sendQuestSelection(player, displayName, active, entityId, questLineId, backgroundName, dismiss, scale);
                }

                player.swing(hand, true);
                return;
            }
        }

        //Initialize the questline
        QuestDisplay initDisplay = questData.initialize(questLineId);

        QuestGiver.LOGGER.info("Initiating Quest: {}", initDisplay);

        if (initDisplay != null) {
            sendQuestDisplay(player, initDisplay, true, entityId, questLineId, backgroundName, dismiss, scale);
            player.swing(hand, true);
        }
    }




    private static void sendQuestDisplay(ServerPlayer player, QuestDisplay display, boolean isNew, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        QuestGiverNetwork.sendToPlayer(new OpenQuestDisplayMessage(display, isNew, entityId, questLineId, backgroundName, dismiss, scale), player);
    }

    private static void sendQuestSelection(ServerPlayer player, Component displayName, List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        QuestGiverNetwork.sendToPlayer(
                new OpenQuestSelectionMessage(displayName, quests, entityId, questLineId, backgroundName, dismiss, scale), player);
    }


    /**
     * Attempt to accept a quest-related gift or item from the player.
     * Returns true if the gift completed a quest task.
     *
     * @param player    The player giving the item
     * @param input     The item or input for the quest task
     * @param onConsume Runnable to execute if the item is consumed (like shrinking the stack)
     */
    public static boolean tryAcceptGift(ServerPlayer player, Entity target, ItemStack input, @Nullable Runnable onConsume) {
        if (player == null || target == null || input == null || input.isEmpty()) {
            return false;
        }

        GiftTask.GiftContext context = new GiftTask.GiftContext(input, target);

        boolean completed = QuestData.get(player).checkComplete(GiftTask.INSTANCE, context);

        if (completed && onConsume != null) {
            onConsume.run();
        }

        return completed;
    }

    /**
     * Attempt to accept a quest-related gift or item from the player.
     * Returns true if the gift completed a quest task.
     *
     * @param player    The player giving the item
     * @param hand      The item or input for the quest task
     * @param message   Send a component message to the player after receiving the item
     */

    public static boolean tryAcceptGift(ServerPlayer player, Entity target, InteractionHand hand, Component message) {
        ItemStack input = player.getItemInHand(hand);

        if (!input.isEmpty()) {

            GiftTask.GiftContext context =
                    new GiftTask.GiftContext(input, target);

            if (QuestData.get(player).checkComplete(GiftTask.INSTANCE, context)) {

                if (!player.isCreative()) {
                    input.shrink(1);
                }

                player.sendSystemMessage(message);
                return true;
            }
        }

        return false;
    }

    public static boolean tryAcceptGift(ServerPlayer player, Entity target, InteractionHand hand) {
        ItemStack input = player.getItemInHand(hand);

        if (!input.isEmpty()) {

            GiftTask.GiftContext context = new GiftTask.GiftContext(input, target);

            if (QuestData.get(player).checkComplete(GiftTask.INSTANCE, context)) {

                if (!player.isCreative()) {
                    input.shrink(1);
                }

                return true;
            }
        }

        return false;
    }


}
