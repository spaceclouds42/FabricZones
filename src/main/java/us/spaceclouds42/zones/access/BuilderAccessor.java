package us.spaceclouds42.zones.access;

import net.minecraft.entity.player.PlayerInventory;

public interface BuilderAccessor {
    PlayerInventory getSecondaryInventory();

    boolean isInBuilderMode();

    boolean isBuilder();

    void swapInventories();
}
