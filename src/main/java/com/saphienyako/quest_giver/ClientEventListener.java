package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = QuestGiver.MOD_ID, value = Dist.CLIENT)
public class ClientEventListener {
    @SubscribeEvent
    public static void showGui(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof DisplayQuestScreen
                || Minecraft.getInstance().screen instanceof SelectQuestScreen) {
            event.setCanceled(true);
        }
    }
}
