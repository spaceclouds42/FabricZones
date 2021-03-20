package us.spaceclouds42.zones.mixin.griefing;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.spaceclouds42.zones.data.ZoneManager;
import us.spaceclouds42.zones.data.spec.PosD;
import us.spaceclouds42.zones.data.spec.Zone;
import us.spaceclouds42.zones.duck.BuilderAccessor;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At(value = "NEW", target = "net/minecraft/item/ItemUsageContext"), cancellable = true)
    private void disablePlacingBlocksInZone(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (player.world.isClient())
            return;


        Zone blockZone = ZoneManager.INSTANCE.getZone(world, hitResult.getBlockPos());
        Zone playerZone = null;
        if (((BuilderAccessor) player).isInBuilderMode()) {
            playerZone = ZoneManager.INSTANCE.getZone(new PosD(
                world.getRegistryKey().getValue().toString(),
                player.getX(),
                player.getY(),
                player.getZ()
            ));
        }

        if (blockZone != playerZone) {
            cir.setReturnValue(ActionResult.FAIL);

            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
                0,
                player.getInventory().selectedSlot + 36,
                player.getMainHandStack()
            ));
        }
    }
}
