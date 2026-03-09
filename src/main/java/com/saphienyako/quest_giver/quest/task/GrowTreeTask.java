package com.saphienyako.quest_giver.quest.task;


import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;


import javax.annotation.Nullable;

public class GrowTreeTask implements TaskType<Block, BlockState> {

    public static final GrowTreeTask INSTANCE = new GrowTreeTask();

    private GrowTreeTask() {}

    /**
     * What data is stored in the quest.
     * Here we store the sapling block.
     */
    @Override
    public Class<Block> element() {
        return Block.class;
    }

    /**
     * What object we test when the event fires.
     * We pass the grown block state.
     */
    @Override
    public Class<BlockState> testType() {
        return BlockState.class;
    }

    /**
     * Called when a tree grows.
     */
    @Override
    public boolean checkCompleted(ServerPlayer player, Block sapling, BlockState newState) {
        // If the sapling grew into something else (logs/leaves),
        // we consider the task complete.
        return newState.getBlock() != sapling;
    }

    @Override
    public Block fromJson(JsonObject json) {
        ResourceLocation id = new ResourceLocation(json.get("sapling").getAsString());
        Block block = ForgeRegistries.BLOCKS.getValue(id);

        if (block == null) {
            throw new IllegalStateException("Unknown sapling: " + id);
        }

        return block;
    }

    @Override
    public JsonObject toJson(Block element) {
        JsonObject json = new JsonObject();

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(element);
        if (id == null) {
            throw new IllegalStateException("Block not registered: " + element);
        }

        json.addProperty("sapling", id.toString());
        return json;
    }

    @Nullable
    @Override
    public Item icon(Block element) {
        return element.asItem();
    }
}
