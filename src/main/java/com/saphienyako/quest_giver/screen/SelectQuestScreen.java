package com.saphienyako.quest_giver.screen;

import com.google.common.collect.ImmutableList;
import com.saphienyako.quest_giver.network.DismissEntityMessage;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.widgets.EntityWidget;
import com.saphienyako.quest_giver.screen.widgets.SelectQuestWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.List;

public class SelectQuestScreen extends Screen {
    
    private final List<SelectableQuest> quests;
    private final int entityId;
    @SuppressWarnings("FieldCanBeLocal")
    private int left;
    private int top;
    private final String questLineId;
    private final String backgroundName;
    private final boolean dismiss;
    private final double scale;
    
    public SelectQuestScreen(List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        super(Component.empty());
        this.quests = ImmutableList.copyOf(quests);
        this.entityId = entityId;
        this.questLineId = questLineId;
        this.backgroundName = backgroundName;
        this.dismiss = dismiss;
        this.scale = scale;
    }

    @Override
    protected void init() {
        super.init();
        
        this.left = (this.width / 2) - ((EntityWidget.WIDTH + 25 + SelectQuestWidget.WIDTH) / 2);
        this.top = (this.height / 2) - (Math.max(EntityWidget.HEIGHT, (SelectQuestWidget.HEIGHT + 2) * this.quests.size()) / 2);
        
        for (int i = 0; i < this.quests.size(); i++) {
            this.addRenderableWidget(new SelectQuestWidget(left + EntityWidget.WIDTH + 25, this.top + ((SelectQuestWidget.HEIGHT + 2) * i), this.quests.get(i), questLineId, backgroundName, this.dismiss, this.scale));
        }

        if (this.entityId != -1) {
            Entity entity = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof LivingEntity living) {
                this.addRenderableWidget(new EntityWidget(left - 20, (this.height - EntityWidget.HEIGHT) / 2 - 30, living, scale));
            }
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.pose().pushPose();
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.pose().translate(0, 0, 20);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.pose().translate(0, 0, 20);
        this.drawTextLines(graphics, mouseX, mouseY);
        graphics.pose().popPose();
    }

    private void drawTextLines(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.minecraft != null) {
            graphics.drawString(this.minecraft.font, this.title, (this.width / 2 - (this.minecraft.font.width(this.title) / 2)) + 20, this.top - 2 - this.minecraft.font.lineHeight, 0xFFFFFF, false);
        }
    }

    @Override
    public void onClose() {
        if(this.dismiss) {
            PacketDistributor.sendToServer(new DismissEntityMessage(this.entityId));
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
