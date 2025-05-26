package blusunrize.immersiveengineering.common.util.compat.navigator;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.navigator.api.journeymap.waypoints.JMWaypointManager;
import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.buttons.ButtonManager;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.UniversalInteractableRenderer;
import com.gtnewhorizons.navigator.api.model.locations.ILocationProvider;
import com.gtnewhorizons.navigator.api.model.waypoints.WaypointManager;
import com.gtnewhorizons.navigator.api.xaero.waypoints.XaeroWaypointManager;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;

public class MineralsLayerManager extends InteractableLayerManager {

    public MineralsLayerManager(ButtonManager buttonManager) {
        super(buttonManager);
    }

    @Override
    protected @Nullable LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        return new UniversalInteractableRenderer(manager)
            .withRenderStep(location -> new MineralRenderStep((MineralLocation) location));
    }

    @Nullable
    @Override
    protected WaypointManager addWaypointManager(InteractableLayerManager manager, SupportedMods mod) {
        return switch (mod) {
            case JourneyMap -> new JMWaypointManager(manager);
            case XaeroWorldMap -> new XaeroWaypointManager(manager, "!");
            default -> null;
        };
    }

    @Override
    protected @Nullable ILocationProvider generateLocation(int chunkX, int chunkZ, int dim) {
        DimensionChunkCoords coords = new DimensionChunkCoords(dim, chunkX, chunkZ);
        ExcavatorHandler.MineralWorldInfo mineralWorldInfo = ExcavatorHandler.mineralCache.get(coords);
        if (mineralWorldInfo == null) {
            return null;
        }
        return new MineralLocation(dim, chunkX, chunkZ, mineralWorldInfo);
    }
}
