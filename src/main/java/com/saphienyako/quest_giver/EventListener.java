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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        if (event.player.tickCount % 20 == 0 && !event.player.level.isClientSide && event.player instanceof ServerPlayer player) {
            QuestData quests = QuestData.get(player);
            player.getInventory().items.forEach(stack -> quests.checkComplete(ItemStackTask.INSTANCE, stack));
            //Quest Check for Biome
            player.level.getBiome(player.blockPosition()).is(biome -> quests.checkComplete(BiomeTask.INSTANCE, biome.location()));
            //Quest Check for Structure
            if (player.getLevel().structureManager().hasAnyStructureAt(player.blockPosition())) {
                RegistryAccess access = player.level.registryAccess();
                Registry<Structure> structureRegistry = access.registryOrThrow(Registry.STRUCTURE_REGISTRY);
                player.getLevel().structureManager().getAllStructuresAt(player.blockPosition()).forEach((structure, set) -> {
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

                ItemStack stack = player.getItemInHand(event.getHand());

                if (!stack.isEmpty()) {

                    if (QuestGiverAPI.tryAcceptGift(player, event.getHand())) {
                        player.swing(event.getHand(), true);
                        return;
                    }
                }

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
