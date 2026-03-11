package com.saphienyako.quest_giver.entity;

import com.saphienyako.quest_giver.QuestGiverAPI;
import com.saphienyako.quest_giver.quest.player.QuestData;
import com.saphienyako.quest_giver.quest.task.SpecialTask;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuestVillagerEntity extends Villager {

    public QuestVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean canSpawn(EntityType<? extends QuestVillagerEntity> entityType, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(BlockTags.DIRT).contains(level.getBlockState(pos.below()).getBlock()) || Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(BlockTags.SAND).contains(level.getBlockState(pos.below()).getBlock());
    }

    @Nonnull
    @Override
    public InteractionResult interactAt(@Nonnull Player player, @Nonnull Vec3 vec, @Nonnull InteractionHand hand) {
        InteractionResult superResult = super.interactAt(player, vec, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (superResult == InteractionResult.PASS) {
            if (stack.isEmpty() && player instanceof ServerPlayer) {
                QuestGiverAPI.interactQuest((ServerPlayer)player, this.getId(), Component.literal("Example Quest"), hand, "example_quest");
            }

             else if (!stack.isEmpty() && player instanceof ServerPlayer && QuestGiverAPI.tryAcceptGift((ServerPlayer) player, hand)) {
                player.swing(hand, true);
            }

            else if (player.getItemInHand(hand).getItem() == Items.NAME_TAG) {
                setCustomName(player.getItemInHand(hand).getHoverName().copy());
                setCustomNameVisible(true);
                    if (player instanceof ServerPlayer) {
                        QuestData.get((ServerPlayer) player).checkComplete(SpecialTask.INSTANCE, "special_task_example");
                    }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return superResult;
        }
    }
}
