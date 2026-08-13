package com.saphienyako.quest_giver;


import com.saphienyako.quest_giver.events.QuestCompletionEvent;
import com.saphienyako.quest_giver.quest.QuestLinkManager;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLinkData;
import com.saphienyako.quest_giver.quest.task.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


public class EventListener {

    @SubscribeEvent
    public void craftItem(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            QuestData.get((ServerPlayer) event.getEntity()).checkComplete(CraftTask.INSTANCE, event.getCrafting());
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            QuestData.get(player).attach(player);
        }
    }

    @SubscribeEvent
    public void playerKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            QuestData.get(player).checkComplete(KillTask.INSTANCE, event.getEntity());
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
    public void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.tickCount % 20 != 0 || player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        QuestData quests = QuestData.get(serverPlayer);

        // ITEM STACK TASK
        for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
            ItemStack stack = serverPlayer.getInventory().getItem(i);
            quests.checkComplete(ItemStackTask.INSTANCE, stack);
        }

        // BIOME TASK
        serverPlayer.level().getBiome(serverPlayer.blockPosition()).is(biome -> quests.checkComplete(BiomeTask.INSTANCE, biome.identifier()));

        // STRUCTURE TASK
        if (serverPlayer.level().structureManager().hasAnyStructureAt(serverPlayer.blockPosition())) {

            RegistryAccess access = serverPlayer.level().registryAccess();

            var structureRegistry = access.lookupOrThrow(Registries.STRUCTURE);

            serverPlayer.level()
                    .structureManager()
                    .getAllStructuresAt(serverPlayer.blockPosition())
                    .forEach((structure, set) -> {
                        //TODO We'll adapt this line to the new HolderLookup API?
                    });
        }
    }

    @SubscribeEvent
    public void entityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Entity target = event.getTarget();

        // GLOBAL GIFT CHECK FIRST
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {

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

        Identifier heldItemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (!link.interactionItem.equals(heldItemId)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
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

        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {

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
