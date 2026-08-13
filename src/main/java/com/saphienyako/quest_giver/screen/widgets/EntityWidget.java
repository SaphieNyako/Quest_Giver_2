package com.saphienyako.quest_giver.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class EntityWidget extends AbstractWidget {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    private final LivingEntity entity;
    private final double scale;

    public EntityWidget(int x, int y, LivingEntity entity, double scale) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.entity = entity;
        this.scale = scale;
    }


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        double scale = ((double) this.height / this.entity.getType().getHeight() * this.scale);

        int x1 = this.getX();
        int y1 = this.getY();
        int x2 = x1 + (this.width * 2);
        int y2 = y1 + this.height + 30 + (int) (scale);

        float yOffset = 0f;

        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                x1, y1,
                x2, y2,
                (int) scale,
                yOffset,
                mouseX,
                mouseY,
                this.entity
        );
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput output) {
        //
    }
}
