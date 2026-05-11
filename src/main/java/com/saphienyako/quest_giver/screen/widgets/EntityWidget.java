package com.saphienyako.quest_giver.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
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

    //TODO
    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        double scale = ((this.height) / this.entity.getType().getHeight() * this.scale);
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                this.getX() + (this.width / 2),
                this.getY() + this.height,
                (int) scale,
                -(mouseX - this.getX() - (this.width / 2f)),
                -(mouseY - this.getY() - (this.height / 2f)),
                this.entity
        );
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput output) {
        //
    }
}
