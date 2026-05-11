package com.saphienyako.quest_giver.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
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
        this.scale = scale; //TODO add Scaling
    }


    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        //Note; in 1.19 make all intermediate calculations double before casting to int to prevent flickering of the model.

        double scale = ((double) this.height / this.entity.getType().getHeight() * this.scale);
        double posX = this.x + this.width / 2.0;
        double posY = this.y + this.height + scale * 48.0 / 85.0;
        double centerX = this.x + this.width / 2.0;
        double centerY = this.y + this.height / 2.0;

        float deltaX = (float)(centerX - mouseX);
        float deltaY = (float)(centerY - mouseY);

        InventoryScreen.renderEntityInInventory(
                (int) posX,
                (int) posY,
                (int) scale,
                (float) deltaX,
                (float) deltaY,
                this.entity
        );
    }

    @Override
    public void updateNarration(@Nonnull  NarrationElementOutput output) {
        //
    }
}
