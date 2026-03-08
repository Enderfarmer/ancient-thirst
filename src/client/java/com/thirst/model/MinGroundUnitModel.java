package com.thirst.model;

import com.thirst.entity.MinGroundUnitEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MinGroundUnitModel extends DefaultedEntityGeoModel<MinGroundUnitEntity> {
    public MinGroundUnitModel() {
        super(Identifier.of("ancient_thirst", "min_ground_unit"));
    }
}