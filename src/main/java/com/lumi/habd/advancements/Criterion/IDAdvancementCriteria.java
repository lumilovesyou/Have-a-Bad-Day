package com.lumi.habd.advancements.Criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

//Compressed advancement triggers to a single class since it's a waste to make a whole new criteria for each to only call once
public class IDAdvancementCriteria extends SimpleCriterionTrigger<IDAdvancementCriteria.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> playerPredicate, int advancementID) implements SimpleInstance {
        public static Codec<IDAdvancementCriteria.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Codec.INT.fieldOf("advancementID").forGetter(Conditions::advancementID)
        ).apply(instance, Conditions::new));

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        public boolean requirementsMet(int calledAdvancementID) {
            return (calledAdvancementID == advancementID || advancementID == 0 || (calledAdvancementID == 3 && advancementID == 2));
        }
    }

    public void trigger(ServerPlayer player, int advancementID) {
        trigger(player, conditions -> conditions.requirementsMet(advancementID));
    }
}