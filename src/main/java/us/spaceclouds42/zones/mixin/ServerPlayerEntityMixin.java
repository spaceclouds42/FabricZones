package us.spaceclouds42.zones.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.spaceclouds42.zones.duck.BuilderAccessor;
import us.spaceclouds42.zones.data.BuilderManager;

/**
 *  Manages inventory swapping.
 */
@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity implements BuilderAccessor {
    @Shadow public ServerPlayNetworkHandler networkHandler;
    /**
     * The player that the mixin is mixed-in to
     */
    @Unique private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;

    /**
     * Player's current inventory
     */
    @Unique private final PlayerInventory currentInventory = thisPlayer.inventory;

    /**
     * Player's secondary inventory
     */
    @Unique private final PlayerInventory secondaryInventory = new PlayerInventory(thisPlayer);

    /**
     * Whether or not player is using builder mode
     */
    @Unique private boolean isInBuilderMode = false;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

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
        return BuilderManager.INSTANCE.isBuilder(this.getUuid());
    }

    @Override
    public void swapInventories() {
        PlayerInventory tempInventory = new PlayerInventory(this);
        tempInventory.clone(this.secondaryInventory);
        tempInventory.selectedSlot = this.currentInventory.selectedSlot;
        this.secondaryInventory.clone(this.currentInventory);
        this.currentInventory.clone(tempInventory);

        this.isInBuilderMode = !this.isInBuilderMode;


        for (StatusEffectInstance effect : this.getStatusEffects()) {
            networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), effect));
        }
    }

    /**
     * Reads secondary inventory from player data
     *
     * @param tag player's data tag
     * @param ci callback info
     */
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

    /**
     * Save secondary inventory to player data
     *
     * @param tag player's data tag
     * @param ci callback info
     */
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

    /**
     * Prevents loss of secondary inventory
     *
     * @param oldPlayer previous player object
     * @param alive whether or not player is alive
     * @param ci callback info
     */
    @Inject(
            method = "copyFrom",
            at = @At(
                    value = "TAIL"
            )
    )
    private void copySecondaryInventory(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.secondaryInventory.clone(((BuilderAccessor) oldPlayer).getSecondaryInventory());
        this.isInBuilderMode = ((BuilderAccessor) oldPlayer).isInBuilderMode();
    }
}
