package com.saphienyako.quest_giver;


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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


public class EventListener {
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)

    public void showGui( RenderGuiLayerEvent.Pre event) {
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
        if (player.tickCount % 20 == 0 && !player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {

            QuestData quests = QuestData.get(serverPlayer);

            serverPlayer.getInventory().items.forEach(stack ->
                    quests.checkComplete(ItemStackTask.INSTANCE, stack));

            serverPlayer.level().getBiome(serverPlayer.blockPosition())
                    .is(biome -> quests.checkComplete(BiomeTask.INSTANCE, biome.location()));

            if (serverPlayer.serverLevel().structureManager().hasAnyStructureAt(serverPlayer.blockPosition())) {

                RegistryAccess access = serverPlayer.level().registryAccess();
                Registry<Structure> structureRegistry = access.registryOrThrow(Registries.STRUCTURE);

                serverPlayer.serverLevel()
                        .structureManager()
                        .getAllStructuresAt(serverPlayer.blockPosition())
                        .forEach((structure, set) -> {

                            ResourceLocation structureId = structureRegistry.getKey(structure);

                            if (structureId != null) {
                                quests.checkComplete(StructureTask.INSTANCE, structureId);
                            }
                        });
            }
        }
    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.getMainHandItem() == ItemStack.EMPTY) {
                QuestData.get(player).checkComplete(AnimalPetTask.INSTANCE, event.getTarget());
            }

            Entity questNPC = event.getTarget();

            // Try to see if this entity starts a quest line
            QuestLinkData link = QuestLinkManager.getMatchingLink(questNPC);
            if (link != null) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);

                if(link.backgroundName != null) {
                    QuestGiverAPI.interactQuest(
                            player,
                            questNPC.getId(),
                            questNPC.getDisplayName(),
                            event.getHand(),
                            link.questLineId,
                            link.backgroundName
                    );
                } else {
                    QuestGiverAPI.interactQuest(
                            player,
                            questNPC.getId(),
                            questNPC.getDisplayName(),
                            event.getHand(),
                            link.questLineId
                    );
                }
                player.swing(event.getHand(), true);
            }
        }
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
