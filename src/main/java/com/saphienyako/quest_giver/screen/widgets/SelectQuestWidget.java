package com.saphienyako.quest_giver.screen.widgets;

import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.SelectQuestMessage;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.util.TextProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
public class SelectQuestWidget extends Button {

    public static final int WIDTH = 210;
    public static final int HEIGHT = 65;
    private final SelectableQuest quest;
    private final ItemStack iconStack;
    private final String questLineId;
    private final String backgroundName;
    private final boolean dismiss;
    private final double scale;

    public SelectQuestWidget(int x, int y, SelectableQuest quest, String questLineId, String backgroundName, boolean dismiss, double scale) {
        super(x, y, WIDTH, HEIGHT, TextProcessor.INSTANCE.processLine(quest.display().title), b -> {}, l -> Component.empty());
        this.quest = quest;
        this.iconStack = new ItemStack(quest.icon());
        this.questLineId = questLineId;
        this.backgroundName = backgroundName;
        this.dismiss = dismiss;
        this.scale = scale;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        super.onPress(input);
        ClientPacketDistributor.sendToServer(new SelectQuestMessage(this.questLineId, this.quest.id(), this.backgroundName, this.dismiss, this.scale));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

     //   graphics.blit(Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID,"textures/gui/" + backgroundName + "_background_03.png"), this.getX(), this.getY(), 0, 0, WIDTH, HEIGHT);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "textures/gui/" + backgroundName + "_background_03.png"),
                this.getX(), this.getY(), 0, 0, WIDTH, HEIGHT, 256, 256);

      //  graphics.pose().pushPose();
     //   graphics.pose().translate(0, 0, 10);
      //  graphics.renderFakeItem(this.iconStack, this.getX() + 20, this.getY() + (this.height - 16) / 2);
        graphics.fakeItem(this.iconStack, this.getX() + 20, this.getY() + (this.height - 16) / 2);

    //    graphics.text(Minecraft.getInstance().font, quest.display().title, this.getX() + 38, this.getY() + ((HEIGHT - font.lineHeight) / 2), 0xFFFFFF, true);
    //    graphics.pose().popPose();
        graphics.text(font, quest.display().title, this.getX() + 38, this.getY() + ((HEIGHT - font.lineHeight) / 2), 0xFFFFFF, true);
    }
}
