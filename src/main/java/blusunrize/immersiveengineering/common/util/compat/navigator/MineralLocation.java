package blusunrize.immersiveengineering.common.util.compat.navigator;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.gtnewhorizons.navigator.api.model.locations.IWaypointAndLocationProvider;
import com.gtnewhorizons.navigator.api.model.waypoints.Waypoint;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;

public class MineralLocation implements IWaypointAndLocationProvider {

    public final int dimensionId;
    public final int chunkX;
    public final int chunkZ;
    public final ExcavatorHandler.MineralWorldInfo mineralWorldInfo;
    private boolean isActiveAsWaypoint;

    public MineralLocation(int dimensionId, int chunkX, int chunkZ,
        ExcavatorHandler.MineralWorldInfo mineralWorldInfo) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.mineralWorldInfo = mineralWorldInfo;
    }

    @Override
    public Waypoint toWaypoint() {
        ExcavatorHandler.MineralMix mineral = mineralWorldInfo.getMineral();
        String label = mineral != null ? mineral.name : "No minerals";
        // TODO color based on mineral type
        return new Waypoint((chunkX << 4) + 8, 65, (chunkZ << 4) + 8, dimensionId, label, Integer.MAX_VALUE);
    }

    @Override
    public boolean isActiveAsWaypoint() {
        return isActiveAsWaypoint;
    }

    @Override
    public void onWaypointCleared() {
        isActiveAsWaypoint = false;
    }

    @Override
    public void onWaypointUpdated(Waypoint waypoint) {
        isActiveAsWaypoint = waypoint.dimensionId == dimensionId && waypoint.blockX == (chunkX << 4) + 8
            && waypoint.blockZ == (chunkZ << 4) + 8;
    }

    @Override
    public int getDimensionId() {
        return dimensionId;
    }

    @Override
    public double getBlockX() {
        return ((chunkX << 4) + 8) + 0.5;
    }

    @Override
    public double getBlockZ() {
        return ((chunkZ << 4) + 8) + 0.5;
    }

    public boolean isDepleted() {
        return mineralWorldInfo.getLastKnownIntegrity() == 0f;
    }

    public IIcon getIconFromPrimaryOre() {
        ItemStack primaryOre = mineralWorldInfo.mineral.getPrimaryOre();
        if (primaryOre == null) {
            return null;
        }
        if (primaryOre.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.field_150939_a.getIcon(1, primaryOre.getItemDamage());
        }
        return primaryOre.getIconIndex();
    }
}
