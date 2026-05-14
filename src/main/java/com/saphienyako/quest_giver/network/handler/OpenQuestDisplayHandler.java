package com.saphienyako.quest_giver.network.handler;

import com.saphienyako.quest_giver.network.OpenQuestDisplayMessage;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class OpenQuestDisplayHandler {

    public static void openMenu(QuestDisplay display, boolean confirmationButtons, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        if (display.sound != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null && display.sound != null) {
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(display.sound, SoundSource.MASTER, 1, 1, player.getRandom(), player.getX(), player.getY(), player.getZ()));
            }
        }
        if (entityId != -1) ClientQuests.lastTalkedEntityId = entityId;
        Minecraft.getInstance().setScreen(new DisplayQuestScreen(display, confirmationButtons, ClientQuests.lastTalkedEntityId, questLineId, backgroundName, dismiss, scale));

    }

}
