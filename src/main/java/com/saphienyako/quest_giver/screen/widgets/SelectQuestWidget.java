package com.saphienyako.quest_giver.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.network.SelectQuestMessage;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.util.TextProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
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

    public SelectQuestWidget(int x, int y, SelectableQuest quest, String questLineId, String backgroundName) {
        super(x, y, WIDTH, HEIGHT, TextProcessor.INSTANCE.processLine(quest.display().title), b -> {});
        this.quest = quest;
        this.iconStack = new ItemStack(quest.icon());
        this.questLineId = questLineId;
        this.backgroundName = backgroundName;
    }

    @Override
    public void onPress() {
        super.onPress();
        QuestGiverNetwork.sendToServer(new SelectQuestMessage(this.questLineId, this.quest.id(), this.backgroundName));
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation(QuestGiver.MOD_ID, "textures/gui/" + backgroundName + "_background_03.png"));

        blit(poseStack, this.x, this.y, 0, 0, WIDTH, HEIGHT);

        poseStack.pushPose();
        poseStack.translate(0, 0, 10);

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        int itemX = this.x + 20;
        int itemY = this.y + (this.height - 16) / 2;

        itemRenderer.renderAndDecorateItem(this.iconStack, itemX, itemY);
        itemRenderer.renderGuiItemDecorations(font, this.iconStack, itemX, itemY);

        font.draw(poseStack, quest.display().title, this.x + 38, this.y + ((HEIGHT - font.lineHeight) / 2), 0xFFFFFF);

        poseStack.popPose();
    }
}
