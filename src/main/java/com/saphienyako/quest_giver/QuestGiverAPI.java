package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.network.OpenQuestDisplayMessage;
import com.saphienyako.quest_giver.network.OpenQuestSelectionMessage;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.QuestLineName;
import com.saphienyako.quest_giver.quest.player.QuestData;
import com.saphienyako.quest_giver.quest.task.GiftTask;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class QuestGiverAPI {

    /**
     * Interact with a quest-giving entity.
     * Opens quest completion screen, active quest screen, or initializes a new quest.
     *
     * @param player        The interacting player
     * @param entityId      The ID of the entity giving the quest
     * @param displayName   The name to show in the GUI (usually entity.getDisplayName())
     * @param hand          The hand used for interaction
     */
     public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, QuestLineName questLineName) {
         QuestData quests = QuestData.get(player);

         // Complete any pending quests first
         QuestDisplay completionDisplay = quests.completePendingQuest();
         if (completionDisplay != null) {
             sendQuestDisplay(player, completionDisplay, false, entityId);
             player.swing(hand, true);
             return;
         }

         // Show active quests
         List<SelectableQuest> active = quests.getActiveQuests();
         if (!active.isEmpty()) {
             if (active.size() == 1) {
                 sendQuestDisplay(player, active.get(0).display(), false, entityId);
             } else {
                 sendQuestSelection(player, displayName, active, entityId);
             }
             player.swing(hand, true);
             //return;
         }

         // Initialize a new quest if none active
         QuestDisplay initDisplay = quests.initialize(questLineName);
         QuestGiver.LOGGER.info("Initiating Quest:" + initDisplay);
         if (initDisplay != null) {
             sendQuestDisplay(player, initDisplay, true, entityId);
             player.swing(hand, true);
         }
     }

    private static void sendQuestDisplay(ServerPlayer player, QuestDisplay display, boolean isNew, int entityId) {
        QuestGiverNetwork.sendToPlayer(new OpenQuestDisplayMessage(display, isNew, entityId), player);
    }

    private static void sendQuestSelection(ServerPlayer player, Component displayName, List<SelectableQuest> quests, int entityId) {
        QuestGiverNetwork.sendToPlayer(
                new OpenQuestSelectionMessage(displayName, quests, entityId), player);
    }

    /**
     * Attempt to accept a quest-related gift or item from the player.
     * Returns true if the gift completed a quest task.
     *
     * @param player    The player giving the item
     * @param input     The item or input for the quest task
     * @param onConsume Runnable to execute if the item is consumed (like shrinking the stack)
     */
    public static boolean tryAcceptGift(ServerPlayer player, ItemStack input, @Nullable Runnable onConsume) {
        if (player == null || input == null) return false;

        boolean completed = QuestData.get(player).checkComplete(GiftTask.INSTANCE, input);
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

    private boolean tryAcceptGift(ServerPlayer player, InteractionHand hand, Component message) {
        ItemStack input = player.getItemInHand(hand);
        if (!input.isEmpty()) {

            if (QuestData.get(player).checkComplete(GiftTask.INSTANCE, input)) {
                if (!player.isCreative()) input.shrink(1);
                player.sendSystemMessage(message);
                return true;
            }
        }
        return false;
    }

}
