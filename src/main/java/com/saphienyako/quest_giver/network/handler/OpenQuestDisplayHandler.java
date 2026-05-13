package com.saphienyako.quest_giver.network.handler;

import com.saphienyako.quest_giver.network.OpenQuestDisplayMessage;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class OpenQuestDisplayHandler {

    public static void openMenu(OpenQuestDisplayMessage msg) {
        if (msg.display().sound != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null && msg.display().sound != null) {
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(msg.display().sound, SoundSource.MASTER, 1, 1, player.getRandom(), player.getX(), player.getY(), player.getZ()));
            }
        }
        if (msg.entityId() != -1) ClientQuests.lastTalkedEntityId = msg.entityId();
        Minecraft.getInstance().setScreen(new DisplayQuestScreen(msg.display(), msg.confirmationButtons(), ClientQuests.lastTalkedEntityId, msg.questLineId(), msg.backgroundName(), msg.dismiss(), msg.scale()));

    }
}
