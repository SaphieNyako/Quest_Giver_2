package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.events.QuestCompletionEvent;
import com.saphienyako.quest_giver.quest.QuestLinkManager;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLinkData;
import com.saphienyako.quest_giver.quest.task.*;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class EventListener {
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void showGui(RenderGuiOverlayEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof DisplayQuestScreen || Minecraft.getInstance().screen instanceof SelectQuestScreen) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void craftItem(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            QuestData.get((ServerPlayer) event.getEntity()).checkComplete(CraftTask.INSTANCE, event.getCrafting());
        }
    }

    @SubscribeEvent
    public void playerKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            QuestData quests = QuestData.get(player);
            quests.checkComplete(KillTask.INSTANCE, event.getEntity());
        }
    }

    @SubscribeEvent
    public void tameAnimal(AnimalTameEvent event) {
        Player player = event.getTamer();
        if (player instanceof ServerPlayer) {
            QuestData.get((ServerPlayer) player).checkComplete(AnimalTameTask.INSTANCE, event.getAnimal());
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        // Only check one / second
        if (event.player.tickCount % 20 == 0 && !event.player.level().isClientSide && event.player instanceof ServerPlayer player) {
            QuestData quests = QuestData.get(player);
            player.getInventory().items.forEach(stack -> quests.checkComplete(ItemStackTask.INSTANCE, stack));
            //Quest Check for Biome
            player.level().getBiome(player.blockPosition()).is(biome -> quests.checkComplete(BiomeTask.INSTANCE, biome.location()));
            //Quest Check for Structure
            if (player.serverLevel().structureManager().hasAnyStructureAt(player.blockPosition())) {
                RegistryAccess access = player.level().registryAccess();
                Registry<Structure> structureRegistry = access.registryOrThrow(Registries.STRUCTURE);
                player.serverLevel().structureManager().getAllStructuresAt(player.blockPosition()).forEach((structure, set) -> {
                    ResourceLocation structureId = structureRegistry.getKey(structure);
                    if (structureId != null) {
                        quests.checkComplete(StructureTask.INSTANCE, structureId);
                    }
                });
            }
        }
    }


    @SubscribeEvent
    public void entityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Entity target = event.getTarget();

        // GLOBAL GIFT CHECK FIRST
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {

            ItemStack stack = player.getItemInHand(event.getHand());

            if (!stack.isEmpty() && QuestGiverAPI.tryAcceptGift(player, target, event.getHand())) {

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);

                player.swing(event.getHand(), true);
                return;
            }
        }

        // ONLY AFTER GIFTING do we care about QuestLinks
        QuestLinkData link = QuestLinkManager.getMatchingLink(target);

        if (link == null || link.interactionItem == null) {
            return;
        }

        ItemStack stack = event.getEntity().getItemInHand(event.getHand());

        ResourceLocation heldItemId = ForgeRegistries.ITEMS.getKey(stack.getItem());

        if (!link.interactionItem.equals(heldItemId)) {
            return;
        }

        if (event.getLevel().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        if (link.backgroundName != null) {
            QuestGiverAPI.interactQuest(
                    player,
                    target.getId(),
                    target.getDisplayName(),
                    event.getHand(),
                    link.questLineId,
                    link.backgroundName,
                    link.scale
            );
        } else {
            QuestGiverAPI.interactQuest(
                    player,
                    target.getId(),
                    target.getDisplayName(),
                    event.getHand(),
                    link.questLineId,
                    link.scale
            );
        }

        player.swing(event.getHand(), true);
    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) {

        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {

            if (event.getHand() != InteractionHand.MAIN_HAND) {
                return;
            }

            Entity target = event.getTarget();

            // PET TASK
            if (player.getMainHandItem().isEmpty()) {
                QuestData.get(player).checkComplete(AnimalPetTask.INSTANCE, target);
            }

            QuestLinkData link = QuestLinkManager.getMatchingLink(target);

            if (link == null) {
                return;
            }

            // This NPC requires a special interaction item.
            // EntityInteractSpecific handles it instead.
            if (link.interactionItem != null) {
                return;
            }

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);

            if (link.backgroundName != null) {
                QuestGiverAPI.interactQuest(
                        player,
                        target.getId(),
                        target.getDisplayName(),
                        event.getHand(),
                        link.questLineId,
                        link.backgroundName,
                        link.scale
                );
            } else {
                QuestGiverAPI.interactQuest(
                        player,
                        target.getId(),
                        target.getDisplayName(),
                        event.getHand(),
                        link.questLineId,
                        link.scale
                );
            }

            player.swing(event.getHand(), true);
        }
    }

    @SubscribeEvent
    public void questCompleted(QuestCompletionEvent event) {
        ServerPlayer player = event.getEntity();
        CompleteQuestTask.Context context = new CompleteQuestTask.Context(event.getQuestLineId(), event.getQuest().id);
        QuestData.get(player).checkComplete(CompleteQuestTask.INSTANCE, context);
    }

    //TODO Tree Grow

    /*
    @SubscribeEvent
    public void treeGrow(SaplingGrowTreeEvent event) {
        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);

        List<Pixie> pixies = event.getLevel().getEntitiesOfClass(Pixie.class, new AABB(pos).inflate(20));
        for (Pixie pixie : pixies) {
            if (pixie.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 20 * 20) continue;
            if (pixie.getOwningPlayer() instanceof ServerPlayer serverPlayer) {
                QuestData.get(serverPlayer).checkComplete(GrowTreeTask.INSTANCE, state);
            }
        }
    } */
}
