package us.spaceclouds42.zones.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    @Shadow public boolean dropItem;

    @Shadow private BlockState block;

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        method = "tick", 
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void onTick(CallbackInfo ci) {
        Zone zone = ZoneManager.INSTANCE.getZone(
            new PosD(
                world.getRegistryKey().getValue().toString(),
                getX(),
                getY(),
                getZ()
            )
        );

        if (zone != null) {
            if (dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                dropItem(block.getBlock());
            }
            
            discard();
            ci.cancel();
        }
    }
}
