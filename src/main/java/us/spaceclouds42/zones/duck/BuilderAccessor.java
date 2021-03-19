package us.spaceclouds42.zones.duck;

import net.minecraft.entity.player.PlayerInventory;

/**
 * Duck accessor for builder properties
 */
public interface BuilderAccessor {
    /**
     * @return player's secondary inventory
     */
    PlayerInventory getSecondaryInventory();

    /**
     * @return whether or not player is currently in builder mode, regardless of status as a builder
     */
    boolean isInBuilderMode();

    /**
     * @return whether or not player is a builder
     */
    boolean isBuilder();

    /**
     * Swaps the builder's current inventory with their secondary inventory
     */
    void swapInventories();
}
