package com.saphienyako.quest_giver.screen.widgets;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class FancyButton extends Button {
    private final Identifier texture;
    private final BooleanSupplier enabled;
    private final int width;
    private final int height;

    protected FancyButton(Identifier texture, int x, int y, int width, int height, Component message, BooleanSupplier enabled, OnPress onPress) {
        super(x, y, width, height, message, btn -> {
            if (enabled.getAsBoolean()) onPress.onPress(btn);
        }, l -> wrapDefaultNarrationMessage(message));
        this.enabled = enabled;
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    public static FancyButton makeLarge(String backgroundName,int x, int y, Component message, OnPress onPress) {
        return makeLarge(backgroundName, x, y, message, () -> true, onPress);
    }
    
    public static FancyButton makeLarge(String backgroundName, int x, int y, Component message, BooleanSupplier enabled, OnPress onPress) {
        return new FancyButton(Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID,"textures/gui/" + backgroundName +  "_button.png"), x, y, 90, 22, message, enabled, onPress);
    }

    public static FancyButton makeSmall(String backgroundName,int x, int y, Component message, OnPress onPress) {
        return makeSmall(backgroundName,x, y, message, () -> true, onPress);
    }
    
    public static FancyButton makeSmall(String backgroundName, int x, int y, Component message, BooleanSupplier enabled, OnPress onPress) {
        return new FancyButton(Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID,"textures/gui/" + backgroundName +  "_button_small.png"), x, y, 22, 22, message, enabled, onPress);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.enabled.getAsBoolean()) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
        //    graphics.blit(this.texture, this.getX(), this.getY(), 0, 0, this.width, this.height);
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
            graphics.text(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, getFGColor() | Mth.ceil(this.alpha * 255) << 24);
        }
    }
}
