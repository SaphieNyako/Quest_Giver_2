package com.saphienyako.quest_giver.screen.widgets;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class BackgroundWidget extends AbstractWidget {

    public static final int WIDTH = 350;
    public static final int HEIGHT = 145;
    public static final int HORIZONTAL_PADDING = 35;
    public static final int VERTICAL_PADDING = 25;

    public final String backgroundName;

    public BackgroundWidget(int x, int y, String backgroundName) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.backgroundName = backgroundName;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID,"textures/gui/" + backgroundName+  "_background_01.png"), this.getX(), this.getY(), 0, 0, 240, HEIGHT);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID,"textures/gui/" + backgroundName+  "_background_02.png"), this.getX() + 240, this.getY(), 0, 0, WIDTH - 240, HEIGHT);
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput output) {
        //
    }
}
