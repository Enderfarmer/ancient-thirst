package com.thirst.systems.mutations.types;

import com.mojang.serialization.Codec;
import com.thirst.ModRegistries;
import com.thirst.ThirstId;
import com.thirst.common.entity.Unit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class MutationBase {
    protected int lastValue = -1;

    public abstract void onKill(EntityType entityType);

    public abstract void onBlockInfect(BlockState blockState);

    public abstract String getType();

    protected abstract int getCounter();

    public void processUnit(Unit mob) {
        if (getCounter() != lastValue)
            processUnitInternal(mob);
        lastValue = getCounter();
    };

    protected abstract void processUnitInternal(Unit mob);

    public Identifier getId() {
        return ThirstId.id(this.getType());
    };

    public static Codec<MutationBase> DISPATCH_CODEC = Identifier.CODEC.dispatch(bb -> bb.getId(),
            type -> ModRegistries.UPGRADE_REGISTRY.get(type).codec());

    /**
     * <h3>The basic upgrader function</h3>
     * <p>
     * Most upgrades are based on a simple counter and some stages
     * </p>
     * 
     * @param counter    The counter the upgrades are based on
     * @param tresholds  The tresholds for the stages (ascending order)
     * @param values     The values that should be added to the attribute per stage
     *                   (ascending order, same index as the corresponding stage)
     * @param mob        The unit being upgraded
     * @param attribute  The entity attribute being upgraded
     * @param upgrade_id The upgrade id
     */
    protected void upgradeByCounterTresholdsValues(int counter, List<Integer> tresholds, List<Double> values, Unit mob,
            RegistryEntry<EntityAttribute> attribute, Identifier upgrade_id) {
        if (tresholds.size() != values.size())
            throw new UnsupportedOperationException("Too many tresholds or values passed");
        if (counter < tresholds.get(0))
            return;
        List<Integer> checkTresholds = tresholds.reversed();
        List<Double> applyValues = values.reversed();
        EntityAttributeInstance instance = mob.getAttributeInstance(attribute);
        instance.removeModifier(upgrade_id);
        for (int i = 0; i < values.size(); i++) {
            if (counter >= checkTresholds.get(i)) {
                instance.addPersistentModifier(new EntityAttributeModifier(upgrade_id, applyValues.get(i),
                        EntityAttributeModifier.Operation.ADD_VALUE));
                return;
            }
        }
    }
}
