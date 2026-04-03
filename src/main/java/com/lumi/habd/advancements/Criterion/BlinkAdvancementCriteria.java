package com.lumi.habd.advancements.Criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BlinkAdvancementCriteria extends SimpleCriterionTrigger<BlinkAdvancementCriteria.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static Codec<BlinkAdvancementCriteria.Conditions> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
            .xmap(Conditions::new, Conditions::player).codec();

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        public boolean requirementsMet() {
            return true;
        }
    }

    public void trigger(ServerPlayer player) {
        trigger(player, Conditions::requirementsMet);
    }
}