package com.lumi.habd.custom.Advancements.Criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import static com.lumi.habd.HaveABadDay.MODID;

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
            return true; // AbstractCriterion#trigger helpfully checks the playerPredicate for us.
        }
    }

    public void trigger(ServerPlayer player) {
        trigger(player, Conditions::requirementsMet);
    }

    public class ModCriteria {
        public static final BlinkAdvancementCriteria USE_TOOL = CriteriaTriggers.register(MODID + ":blink", new BlinkAdvancementCriteria());
    }
}