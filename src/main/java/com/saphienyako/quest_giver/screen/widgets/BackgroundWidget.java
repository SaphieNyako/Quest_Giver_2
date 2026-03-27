package com.saphienyako.quest_giver.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class BackgroundWidget extends AbstractWidget {

    public static final int WIDTH = 350;
    public static final int HEIGHT = 145;
    public static final int HORIZONTAL_PADDING = 35;
    public static final int VERTICAL_PADDING = 25;


    //TODO make this one Background, replaceable
    public static final ResourceLocation BACKGROUND_01 = new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/quest_giver_background_01.png");
    public static final ResourceLocation BACKGROUND_02 = new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/quest_giver_background_02.png");

    public final String backgroundName;

    public BackgroundWidget(int x, int y, String backgroundName) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.backgroundName = backgroundName;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, new ResourceLocation(QuestGiver.MOD_ID, "textures/gui/" + backgroundName + "_background_01.png"));
        blit(poseStack, this.x, this.y, 0, 0, 240, HEIGHT);

        RenderSystem.setShaderTexture(0, new ResourceLocation(QuestGiver.MOD_ID, "textures/gui/" + backgroundName + "_background_02.png"));
        blit(poseStack, this.x + 240, this.y, 0, 0, WIDTH - 240, HEIGHT);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput output) {
        //
    }
}
