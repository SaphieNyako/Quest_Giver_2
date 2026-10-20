package com.saphienyako.quest_giver.entity;

import com.saphienyako.quest_giver.QuestGiverAPI;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.task.SpecialTask;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class QuestVillagerEntity extends Villager {

    public QuestVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean canSpawn(EntityType<@NotNull QuestVillagerEntity> entityType, LevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND);
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Vec3 location) {

        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && stack.isEmpty()) {

            if (player instanceof ServerPlayer serverPlayer) {
                QuestGiverAPI.interactQuest(serverPlayer, this.getId(), Component.literal("Example Quest"), hand, "example_quest");
            }

            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand, location);
    }

}
