package com.saphienyako.quest_giver;

import com.mojang.logging.LogUtils;
import com.saphienyako.quest_giver.data.QuestLineNameLoader;
import com.saphienyako.quest_giver.data.QuestLinkDataLoader;
import com.saphienyako.quest_giver.data.SpecialTaskActionLoader;
import com.saphienyako.quest_giver.entity.ModEntities;
import com.saphienyako.quest_giver.entity.QuestVillagerEntity;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestCommands;
import com.saphienyako.quest_giver.quest.QuestManager;
import com.saphienyako.quest_giver.quest.reward.CommandReward;
import com.saphienyako.quest_giver.quest.reward.ItemReward;
import com.saphienyako.quest_giver.quest.reward.RewardTypes;
import com.saphienyako.quest_giver.quest.task.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(QuestGiver.MOD_ID)
public class QuestGiver
{
    public static final String MOD_ID = "quest_giver";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public QuestGiver(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::entityAttributes);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        ModEntities.register(modEventBus);
        modEventBus.addListener(QuestGiverNetwork::register);
        ModItems.register(modEventBus);

        //Datapack for Quests
        NeoForge.EVENT_BUS.addListener(this::reloadData);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        //Player Capabilities
        ModAttachments.register(modEventBus);

        NeoForge.EVENT_BUS.register(new EventListener());

        // Quest task & reward types. Not in setup as they are required for datagen.
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"craft"), CraftTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"gift"), GiftTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"item_stack"), ItemStackTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"kill"), KillTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"pet"), AnimalPetTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"tame"), AnimalTameTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"biome"), BiomeTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"structure"), StructureTask.INSTANCE);
        //  TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"tree"), GrowTreeTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "quest_complete"), QuestCompleteTask.INSTANCE);
        TaskTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID,"special_task"), SpecialTask.INSTANCE);

        RewardTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item"), ItemReward.INSTANCE);
        RewardTypes.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "command"), CommandReward.INSTANCE);


    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //Added ModCreativeModeTab for the mod itself
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.QUEST_VILLAGER.get(), QuestVillagerEntity.createAttributes().build());
    }

    public void reloadData(AddReloadListenerEvent event) {
        event.addListener(new QuestLineNameLoader());
        event.addListener(new SpecialTaskActionLoader());
        event.addListener(QuestManager.createReloadListener());
        event.addListener(new QuestLinkDataLoader());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        QuestCommands.register(event.getDispatcher());
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.QUEST_VILLAGER.get(), VillagerRenderer::new);
        }

        @SubscribeEvent
        private static void spawnPlacement(RegisterSpawnPlacementsEvent event) {
            event.register(ModEntities.QUEST_VILLAGER.get(),  SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, QuestVillagerEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }
}
