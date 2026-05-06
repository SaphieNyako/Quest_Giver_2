package com.saphienyako.quest_giver.screen.widgets;

import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.network.SelectQuestMessage;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.util.TextProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SelectQuestWidget extends Button {

    public static final int WIDTH = 210;
    public static final int HEIGHT = 65;
    private final SelectableQuest quest;
    private final ItemStack iconStack;

    private String questLineId;

    private String backgroundName;

    private boolean dismiss;

    public SelectQuestWidget(int x, int y, SelectableQuest quest, String questLineId, String backgroundName, boolean dismiss) {
        super(x, y, WIDTH, HEIGHT, TextProcessor.INSTANCE.processLine(quest.display().title), b -> {}, l -> Component.empty());
        this.quest = quest;
        this.iconStack = new ItemStack(quest.icon());
        this.questLineId = questLineId;
        this.backgroundName = backgroundName;
        this.dismiss = dismiss;
    }

    @Override
    public void onPress() {
        super.onPress();
        QuestGiverNetwork.sendToServer(new SelectQuestMessage(this.questLineId, this.quest.id(), this.backgroundName, this.dismiss));
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        
        graphics.blit(new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/" + backgroundName + "_background_03.png"), this.getX(), this.getY(), 0, 0, WIDTH, HEIGHT);
        
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 10);
        graphics.renderFakeItem(this.iconStack, this.getX() + 20, this.getY() + (this.height - 16) / 2);
        
        graphics.drawString(Minecraft.getInstance().font, quest.display().title, this.getX() + 38, this.getY() + ((HEIGHT - font.lineHeight) / 2), 0xFFFFFF, true);
        graphics.pose().popPose();
    }
}
