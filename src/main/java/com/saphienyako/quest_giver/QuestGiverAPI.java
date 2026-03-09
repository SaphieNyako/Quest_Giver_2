package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.QuestName;
import com.saphienyako.quest_giver.quest.player.QuestData;
import com.saphienyako.quest_giver.quest.task.GiftTask;
import com.saphienyako.quest_giver.quest.task.TaskType;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class QuestGiverAPI {

     /**
     * Show the player's active quest selection or completion screen.
     */
     public static void interactQuest(ServerPlayer player, int entityId, Component displayName, InteractionHand hand, QuestName questName) {
         QuestData quests = QuestData.get(player);


         // Complete any pending quests first
         QuestDisplay completionDisplay = quests.completePendingQuest();
         if (completionDisplay != null) {
             sendQuestDisplay(player, completionDisplay, false, entityId, questName);
             player.swing(hand, true);
             return;
         }

         // Show active quests
         List<SelectableQuest> active = quests.getActiveQuests();
         if (!active.isEmpty()) {
             if (active.size() == 1) {
                 sendQuestDisplay(player, active.get(0).display(), false, entityId, questName);
             } else {
                 sendQuestSelection(player, displayName, active, entityId);
             }
             player.swing(hand, true);
             return;
         }

         // Initialize a new quest if none active
         QuestDisplay initDisplay = quests.initialize(questName);
         if (initDisplay != null) {
             sendQuestDisplay(player, initDisplay, true, entityId, questName);
             player.swing(hand, true);
         }
     }

    private static void sendQuestDisplay(ServerPlayer player, QuestDisplay display, boolean isNew, int entityId, QuestName questName) {
        QuestGiverNetwork.sendToPlayer(player,
                new OpenQuestDisplayMessage(display, isNew, entityId, questName));
    }

    private static void sendQuestSelection(ServerPlayer player, Component displayName, List<SelectableQuest> quests, int entityId) {
        QuestGiverNetwork.sendToPlayer(player,
                new OpenQuestSelectionMessage(displayName, quests, entityId));
    }

    /**
     * Attempt to accept a quest-related gift or item from the player.
     * Returns true if the gift completed a quest task.
     */
    public static boolean tryAcceptGift(ServerPlayer player, ItemStack input, @Nullable Runnable onConsume) {
        if (player == null || input == null) return false;

        boolean completed = QuestData.get(player).checkComplete(GiftTask.INSTANCE, input);
        if (completed && onConsume != null) {
            onConsume.run();
        }
        return completed;
    }

}
