package com.saphienyako.quest_giver.quest.reward;

import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.quest.util.ItemStackHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemReward implements RewardType<ItemStack> {

    public static final ItemReward INSTANCE = new ItemReward();

    private ItemReward() {

    }

    @Override
    public Class<ItemStack> element() {
        return ItemStack.class;
    }

    @Override
    public void grantReward(ServerPlayer player, ItemStack element) {

        if (!player.getInventory().add(element.copy())) {
            player.drop(element.copy(), false);
        }
    }

    @Override
    public ItemStack fromJson(JsonObject json) {
        return ItemStackHelper.fromJson(json.get("item").getAsJsonObject());
    }

    @Override
    public JsonObject toJson(ItemStack element) {
        JsonObject json = new JsonObject();
        json.add("item", ItemStackHelper.toJson(element, true));
        return json;
    }
}
