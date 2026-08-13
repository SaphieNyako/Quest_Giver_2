package com.saphienyako.quest_giver.quest;

import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.quest.reward.RewardType;
import com.saphienyako.quest_giver.quest.reward.RewardTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class QuestReward {

    private final RewardType<Object> reward;
    private final Object element;

    private QuestReward(RewardType<Object> reward, Object element) {
        this.reward = reward;
        this.element = element;
        if (!this.reward.element().isAssignableFrom(element.getClass())) {
            throw new IllegalStateException("Can't create quest task: element type mismatch");
        }
    }

    public static <T> QuestReward of(RewardType<T> type, T element) {
        //noinspection unchecked
        return new QuestReward((RewardType<Object>) type, element);
    }

    public void grantReward(ServerPlayer player) {
        this.reward.grantReward(player, this.element);
    }

    public JsonObject toJson() {
        JsonObject json = this.reward.toJson(this.element);
        json.addProperty("id", RewardTypes.getId(this.reward).toString());
        return json;
    }

    public static QuestReward fromJson(JsonObject json) {
        //noinspection unchecked
        RewardType<Object> reward = (RewardType<Object>) RewardTypes.getType(Identifier.tryParse(json.get("id").getAsString()));
        Object element = reward.fromJson(json);
        return new QuestReward(reward, element);
    }
}
