package com.saphienyako.quest_giver.screen.widgets;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class FancyButton extends Button {

    //TODO GUI changeable
    public static final ResourceLocation TEXTURE_LARGE = new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/quest_button.png");
    public static final ResourceLocation TEXTURE_SMALL = new ResourceLocation(QuestGiver.MOD_ID,"textures/gui/quest_button_small.png");

    private final ResourceLocation texture;
    private final BooleanSupplier enabled;
    private final int width;
    private final int height;

    protected FancyButton(ResourceLocation texture, int x, int y, int width, int height, Component message, BooleanSupplier enabled, OnPress onPress) {
        super(x, y, width, height, message, btn -> {
            if (enabled.getAsBoolean()) onPress.onPress(btn);
        }, l -> wrapDefaultNarrationMessage(message));
        this.enabled = enabled;
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.enabled.getAsBoolean()) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            graphics.blit(this.texture, this.getX(), this.getY(), 0, 0, this.width, this.height);
            graphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, getFGColor() | Mth.ceil(this.alpha * 255) << 24);
        }
    }
    
    public static FancyButton makeLarge(int x, int y, Component message, OnPress onPress) {
        return makeLarge(x, y, message, () -> true, onPress);
    }
    
    public static FancyButton makeLarge(int x, int y, Component message, BooleanSupplier enabled, OnPress onPress) {
        return new FancyButton(TEXTURE_LARGE, x, y, 90, 22, message, enabled, onPress);
    }

    public static FancyButton makeSmall(int x, int y, Component message, OnPress onPress) {
        return makeSmall(x, y, message, () -> true, onPress);
    }
    
    public static FancyButton makeSmall(int x, int y, Component message, BooleanSupplier enabled, OnPress onPress) {
        return new FancyButton(TEXTURE_SMALL, x, y, 22, 22, message, enabled, onPress);
    }
}
