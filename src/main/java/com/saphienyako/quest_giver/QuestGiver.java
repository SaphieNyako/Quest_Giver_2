package com.saphienyako.quest_giver;

import com.mojang.logging.LogUtils;
import com.saphienyako.quest_giver.data.QuestLineNameLoader;
import com.saphienyako.quest_giver.data.SpecialTaskActionLoader;
import com.saphienyako.quest_giver.entity.ModEntities;
import com.saphienyako.quest_giver.entity.QuestVillagerEntity;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestManager;
import com.saphienyako.quest_giver.quest.player.CapabilityQuests;
import com.saphienyako.quest_giver.quest.reward.CommandReward;
import com.saphienyako.quest_giver.quest.reward.ItemReward;
import com.saphienyako.quest_giver.quest.reward.RewardTypes;
import com.saphienyako.quest_giver.quest.task.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(QuestGiver.MOD_ID)
public class QuestGiver
{
    public static final String MOD_ID = "quest_giver";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public QuestGiver() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityAttributes);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        ModEntities.register(modEventBus);


        //Datapack for Quests
        MinecraftForge.EVENT_BUS.addListener(this::reloadData);
        //Player Capabilities
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityQuests::attachPlayerCaps);
        MinecraftForge.EVENT_BUS.addListener(CapabilityQuests::playerCopy);

        MinecraftForge.EVENT_BUS.register(new EventListener());

        // Quest task & reward types. Not in setup as they are required for datagen.
        TaskTypes.register(new ResourceLocation(MOD_ID,"craft"), CraftTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"gift"), GiftTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"item_stack"), ItemStackTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"kill"), KillTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"pet"), AnimalPetTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"tame"), AnimalTameTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"biome"), BiomeTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"structure"), StructureTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"tree"), GrowTreeTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"special_task"), SpecialTask.INSTANCE);

        RewardTypes.register(new ResourceLocation(MOD_ID, "item"), ItemReward.INSTANCE);
        RewardTypes.register(new ResourceLocation(MOD_ID, "command"), CommandReward.INSTANCE);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(QuestGiverNetwork::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //Added ModCreativeModeTab for the mod itself
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.QUEST_VILLAGER.get(), VillagerRenderer::new);
        }
    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.QUEST_VILLAGER.get(), QuestVillagerEntity.createAttributes().build());

    }

    private void spawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.QUEST_VILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, QuestVillagerEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    public void reloadData(AddReloadListenerEvent event) {
        event.addListener(new QuestLineNameLoader());
        event.addListener(new SpecialTaskActionLoader());
        event.addListener(QuestManager.createReloadListener());
    }
}
