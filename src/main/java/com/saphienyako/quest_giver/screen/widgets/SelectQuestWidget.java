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

    public static final ResourceLocation BACKGROUND_03 = new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/quest_giver_background_03.png");

    private final SelectableQuest quest;
    private final ItemStack iconStack;

    public SelectQuestWidget(int x, int y, SelectableQuest quest) {
        super(x, y, WIDTH, HEIGHT, TextProcessor.INSTANCE.processLine(quest.display().title), b -> {}, l -> Component.empty());
        this.quest = quest;
        this.iconStack = new ItemStack(quest.icon());
    }

    @Override
    public void onPress() {
        super.onPress();
        QuestGiverNetwork.sendToServer(new SelectQuestMessage(this.quest.id()));
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        
        graphics.blit(BACKGROUND_03, this.getX(), this.getY(), 0, 0, WIDTH, HEIGHT);
        
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 10);
        graphics.renderFakeItem(this.iconStack, this.getX() + 20, this.getY() + (this.height - 16) / 2);
        
        graphics.drawString(Minecraft.getInstance().font, quest.display().title, this.getX() + 38, this.getY() + ((HEIGHT - font.lineHeight) / 2), 0xFFFFFF, true);
        graphics.pose().popPose();
    }
}
