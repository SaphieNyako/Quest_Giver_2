package com.saphienyako.quest_giver.quest.reward;

import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.quest.util.ItemStackHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class ItemReward implements RewardType<ItemStackTemplate> {

    public static final ItemReward INSTANCE = new ItemReward();

    private ItemReward() {
    }

    @Override
    public Class<ItemStackTemplate> element() {
        return ItemStackTemplate.class;
    }

    @Override
    public void grantReward(ServerPlayer player, ItemStackTemplate element) {
        ItemStack stack = element.create();

        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    public ItemStackTemplate fromJson(JsonObject json) {
        return ItemStackHelper.fromJson(json.get("item").getAsJsonObject());
    }

    @Override
    public JsonObject toJson(ItemStackTemplate element) {
        JsonObject json = new JsonObject();
        json.add("item", ItemStackHelper.toJson(element));
        return json;
    }
}
