package com.thirst.systems.documentation.gathering;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.thirst.AncientThirst;
import com.thirst.common.entity.Unit;
import com.thirst.mass.MassState;
import com.thirst.mass.Stage;
import com.thirst.systems.documentation.DocuStorage;
import com.thirst.systems.documentation.attributes.AdvancedAttrs;
import com.thirst.systems.documentation.attributes.BasicAttrs;
import com.thirst.systems.documentation.entry.DocuEntry;
import com.thirst.systems.documentation.entry.ProcessedDocuEntry;
import com.thirst.systems.documentation.entry.UnitKill;
import com.thirst.systems.documentation.weapon.HandWeapon;
import com.thirst.systems.documentation.weapon.KineticWeapon;
import com.thirst.systems.documentation.weapon.MagicWeapon;
import com.thirst.systems.documentation.weapon.MeleeWeapon;
import com.thirst.systems.documentation.weapon.RangedWeapon;
import com.thirst.systems.documentation.weapon.Weapon;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class Events {
    public static void onDeath(Unit killed, DamageSource source, float amount) {
        LivingEntity killer = (LivingEntity) source.getAttacker();
        RegistryKey<DamageType> damageType = source.getTypeRegistryEntry().getKey().get();
        BasicAttrs basicAttrs = BasicAttrs.scan(killer);
        DocuEntry docuEntry = DocuStorage.loadFromEntity(killer);
        docuEntry.rawEntry.setBasicAttributes(basicAttrs);
        // AdvancedAttrs advancedAttrs =
        // docuEntry.getRawDocuEntry().getAdvancedAttributes();
        ItemStack weaponItem = killer.getMainHandStack();
        List<RegistryKey<DamageType>> meleeDamageTypes = List.of(
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK,
                DamageTypes.STING);
        List<RegistryKey<DamageType>> magic = List.of(
                DamageTypes.MAGIC,
                DamageTypes.INDIRECT_MAGIC);
        List<RegistryKey<DamageType>> ranged = List.of(
                DamageTypes.MOB_PROJECTILE,
                DamageTypes.ARROW,
                DamageTypes.TRIDENT,
                DamageTypes.FIREBALL,
                DamageTypes.SPIT,
                DamageTypes.SONIC_BOOM,
                DamageTypes.DRAGON_BREATH,
                DamageTypes.WITHER_SKULL);
        List<RegistryKey<DamageType>> kinetic = List.of(
                DamageTypes.SPEAR,
                DamageTypes.MACE_SMASH);
        Weapon weapon = new MagicWeapon(amount);
        KineticWeaponComponent kineticWeaponComponent = weaponItem.getItem().getComponents()
                .get(DataComponentTypes.KINETIC_WEAPON);
        if (weaponItem.isEmpty()) {
            weapon = new HandWeapon((float) killer.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
        } else if (meleeDamageTypes.contains(damageType)) {
            double attackSpeed = killer.getAttributeValue(EntityAttributes.ATTACK_SPEED);
            List<AttributeModifiersComponent.Entry> modifiers = weaponItem.getComponents()
                    .get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
            for (AttributeModifiersComponent.Entry element : modifiers) {
                if (element.attribute() == EntityAttributes.ATTACK_SPEED) {
                    attackSpeed += element.modifier().value();
                    break;
                }
            }
            AncientThirst.LOGGER.info("Creating a MeleeWeapon(" + amount + ", " + attackSpeed + ")");
            weapon = new MeleeWeapon(amount,
                    (float) attackSpeed);
        } else if (ranged.contains(damageType)) {
            weapon = new RangedWeapon(amount);
        } else if (magic.contains(damageType)) {
            weapon = new MagicWeapon(amount);
        } else if (kinetic.contains(damageType) || kineticWeaponComponent != null) {
            if (weaponItem.getItem() == Items.MACE) {
                float damage = 0;
                List<AttributeModifiersComponent.Entry> modifiers = weaponItem.getComponents()
                        .get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers();
                for (AttributeModifiersComponent.Entry element : modifiers) {
                    if (element.attribute() == EntityAttributes.ATTACK_SPEED) {
                        damage = (float) element.modifier().value();
                        break;
                    }
                }
                weapon = new KineticWeapon(1.25f, damage);
            } else {
                float minSpeed = kineticWeaponComponent.damageConditions().get().minSpeed();
                weapon = new KineticWeapon(minSpeed, weaponItem.getItem().getBonusAttackDamage(killed,
                        (float) killer.getAttributeValue(EntityAttributes.ATTACK_DAMAGE), source));
            }
        }
        docuEntry.rawEntry.addKill(new UnitKill(amount, weaponItem, weapon, killed.getMaxHealth()));
        if (MassState.getServerState(killer.getEntityWorld().getServer()).getStage() != Stage.AWAKENING) {
            float meleeLethality = ProcessedDocuEntry.analyzeRaw(docuEntry.rawEntry).getMeleeLethality();
            AncientThirst.LOGGER.info(Float.toString(meleeLethality));
            docuEntry.processedEntry = ProcessedDocuEntry.analyzeRaw(docuEntry.rawEntry);
        }
        DocuStorage.saveToKey(DocuStorage.assignKey(killer), docuEntry, killer.getEntityWorld().getServer());
    }
}
