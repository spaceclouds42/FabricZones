package us.spaceclouds42.zones.mixin;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.access.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

/**
 *  - Manages inventory swapping
 *  - Prevents builders from dropping items from builder mode
 */
@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin implements BuilderAccessor {
    @Unique private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
    @Unique private final PlayerInventory inventory = thisPlayer.getInventory();
    @Unique private final PlayerInventory secondaryInventory = new PlayerInventory(thisPlayer);
    @Unique private boolean isInBuilderMode = false;

    @Override
    public PlayerInventory getSecondaryInventory() {
        return this.secondaryInventory;
    }

    @Override
    public boolean isInBuilderMode() {
        return this.isInBuilderMode;
    }

    @Override
    public boolean isBuilder() {
        return BuilderManager.INSTANCE.isBuilder(((PlayerEntity) (Object) this).getUuid());
    }

    @Override
    public void swapInventories() {
        PlayerInventory tempInventory = new PlayerInventory((PlayerEntity) (Object) this);
        tempInventory.clone(this.secondaryInventory);
        this.secondaryInventory.clone(this.inventory);
        this.inventory.clone(tempInventory);

        this.isInBuilderMode = !this.isInBuilderMode;
    }

    @Inject(
            method = "readCustomDataFromTag",
            at = @At(
                    value = "TAIL"
            )
    )
    private void readDataFromTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("InBuilderMode")) {
            this.isInBuilderMode = tag.getBoolean("InBuilderMode");
        }

        if (tag.contains("SecondaryInventory")) {
            ListTag secondaryInvTag = tag.getList("SecondaryInventory", NbtType.COMPOUND);
            this.secondaryInventory.deserialize(secondaryInvTag);
        }
    }

    @Inject(
            method = "writeCustomDataToTag",
            at = @At(
                    value = "TAIL"
            )
    )
    private void writeDataToTag(CompoundTag tag, CallbackInfo ci) {
        ByteTag inBuilderModeTag = ByteTag.of(this.isInBuilderMode);
        tag.put("InBuilderMode", inBuilderModeTag);

        tag.put("SecondaryInventory", this.secondaryInventory.serialize(new ListTag()));
    }

    @Inject(
            method = "copyFrom",
            at = @At(
                    value = "TAIL"
            )
    )
    private void copySecondaryInventory(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.secondaryInventory.clone(((BuilderAccessor) oldPlayer).getSecondaryInventory());
    }

    @Inject(
            method = "dropItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (this.isInBuilderMode) {
            cir.setReturnValue(null);
        }
    }
}
