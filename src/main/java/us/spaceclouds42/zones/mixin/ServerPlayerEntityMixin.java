package us.spaceclouds42.zones.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.access.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

/**
 * Manages inventory swapping
 *
 * Prevents builders from dropping items from builder mode
 */
@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin implements BuilderAccessor {
    @Unique private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
    @Unique private final PlayerInventory inventory = thisPlayer.getInventory();
    @Unique private final PlayerInventory secondaryInventory = new PlayerInventory(thisPlayer);
    @Unique private boolean isInBuilderMode = false;

    /**
     * This method isn't used anywhere yet because I can't seem
     * to find a place at which I can call it without either
     * causing an infinite loop crash or a null pointer crash.
     *
     * The toTag() method is the source of the crash. If used in
     * the mixin to writeCustomDataToTag(), infinite loop occurs
     * because toTag() calls writeCustomDataToTag(). If used too
     * early, a null pointer exception occurs due to the
     * ServerPlayerEntity not being fully initialized.
     *
     * And yes, this is in fact a cry for help.
     */
    @Unique
    private PlayerInventory deserializeSecondaryInventory() {
        CompoundTag playerTag = thisPlayer.toTag(new CompoundTag());
        PlayerInventory secondaryInventory = new PlayerInventory(thisPlayer);

        if (playerTag.contains("SecondaryInventory")) {
            ListTag secondaryInvTag = playerTag.getList("SecondaryInventory", NbtType.COMPOUND);
            secondaryInventory.deserialize(secondaryInvTag);
        }

        return secondaryInventory;
    }

    @Override
    public PlayerInventory getSecondaryInventory() {
        return this.secondaryInventory;
    }

    @Override
    public boolean isInBuilderMode() {
        return isInBuilderMode;
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

        isInBuilderMode = !isInBuilderMode;
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
            method = "dropItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void preventDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (isInBuilderMode) {
            cir.setReturnValue(null);
        }
    }
}
