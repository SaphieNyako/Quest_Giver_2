package com.saphienyako.quest_giver.screen;

import com.saphienyako.quest_giver.network.ConfirmQuestMessage;
import com.saphienyako.quest_giver.network.DismissEntityMessage;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.screen.util.AnimatedText;
import com.saphienyako.quest_giver.screen.util.TextProcessor;
import com.saphienyako.quest_giver.screen.widgets.BackgroundWidget;
import com.saphienyako.quest_giver.screen.widgets.EntityWidget;
import com.saphienyako.quest_giver.screen.widgets.FancyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class DisplayQuestScreen extends Screen {

    @SuppressWarnings("FieldCanBeLocal")
    private final QuestDisplay display;
    private final boolean hasConfirmationButtons;
    private final int entityId;
    private final Component title;
    private final AnimatedText text;
    private int left;
    private int top;
    private final String questLineId;
    private final String backgroundName;
    private final boolean dismiss;
    private final double scale;

    public DisplayQuestScreen(QuestDisplay display, boolean hasConfirmationButtons, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        super(display.title);
        this.backgroundName = backgroundName;
        this.display = display;
        this.hasConfirmationButtons = hasConfirmationButtons;
        this.entityId = entityId;
        this.questLineId = questLineId;
        this.dismiss = dismiss;
        this.scale = scale;

        this.title = TextProcessor.INSTANCE.processLine(this.display.title);
        //TODO
        //This determines how many chars should appear each tick when displaying the quest text, 1 being very slow - 5 being very fast.
        int quest_text_speed = 3;
        this.text = new AnimatedText(BackgroundWidget.WIDTH - (2 * BackgroundWidget.HORIZONTAL_PADDING), BackgroundWidget.HEIGHT - (2 * BackgroundWidget.VERTICAL_PADDING), quest_text_speed, TextProcessor.INSTANCE.process(this.display.description));
    }

    @Override
    protected void init() {
        super.init();

        this.left = (this.width / 2) - ((EntityWidget.WIDTH + BackgroundWidget.WIDTH) / 2);
        this.top = (this.height / 2) - (BackgroundWidget.HEIGHT / 2);

        this.addRenderableOnly(new BackgroundWidget(this.left + EntityWidget.WIDTH, this.top, this.backgroundName));

        if (this.entityId != -1) {
            Entity entity = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof LivingEntity living) {
                this.addRenderableWidget(new EntityWidget(this.left - 20, this.top + (BackgroundWidget.HEIGHT - EntityWidget.HEIGHT) / 2 - 30, living, scale));
            }
        }

        this.addRenderableWidget(FancyButton.makeSmall(this.backgroundName, this.left + EntityWidget.WIDTH + 320, this.top + 58, Component.literal(this.text.isOnLastPage() ? "x" : ">>"), this.text::canContinue, button -> {
            if (this.text.isOnLastPage()) {
                this.onClose();
            } else {
                this.text.nextPage();
                button.setMessage(Component.literal(this.text.isOnLastPage() ? "x" : ">>"));
            }
        }));

        if (this.hasConfirmationButtons) {
            this.addRenderableWidget(FancyButton.makeLarge(this.backgroundName,this.left + EntityWidget.WIDTH + 80, this.top + 123, Component.translatable("message.quest_giver.quest_accept"), button -> {
                ClientPacketDistributor.sendToServer(new ConfirmQuestMessage(this.questLineId,  true));
                this.onClose();
            }));
            this.addRenderableWidget(FancyButton.makeLarge(this.backgroundName,this.left + EntityWidget.WIDTH + 180, this.top + 123, Component.translatable("message.quest_giver.quest_decline"), button -> {
                ClientPacketDistributor.sendToServer(new ConfirmQuestMessage(this.questLineId,false));
                this.onClose();
            }));
        }
    }

    @Override
    public void onClose() {
        if(this.dismiss) {
            ClientPacketDistributor.sendToServer(new DismissEntityMessage(this.entityId));
        }
        super.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      //  graphics.pose().pushPose();
      //  this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);


     //   graphics.pose().translate(0, 0, 20);
     //   super.render(graphics, mouseX, mouseY, partialTicks);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

      //  graphics.pose().translate(0, 0, 20);
     //   this.drawTextLines(graphics, mouseX, mouseY);
     //   graphics.pose().popPose();
        this.drawTextLines(graphics);
    }

    private void drawTextLines(GuiGraphicsExtractor graphics) {
        if (this.minecraft != null) {
            graphics.text(this.minecraft.font, this.title, (this.width / 2) - (this.minecraft.font.width(this.title) / 2) + 20, this.top - this.minecraft.font.lineHeight + 10, 0xFFFFFF, false);
            this.text.render(graphics, this.left + EntityWidget.WIDTH + BackgroundWidget.HORIZONTAL_PADDING, this.top + BackgroundWidget.VERTICAL_PADDING);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.text != null) {
            this.text.tick();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !hasConfirmationButtons;
    }
}
