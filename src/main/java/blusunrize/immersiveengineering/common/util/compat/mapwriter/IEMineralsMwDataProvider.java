package blusunrize.immersiveengineering.common.util.compat.mapwriter;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.world.ChunkCoordIntPair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IEMineralsMwDataProvider implements IMwDataProvider {
    private final Map<ChunkCoordIntPair, ExcavatorHandler.MineralWorldInfo> cache = new HashMap<>();
    private int zoom;

    @Override
    public ArrayList<IMwChunkOverlay> getChunksOverlay(int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
        ArrayList<IMwChunkOverlay> overlays = new ArrayList<>(cache.size());
        for (Map.Entry<ChunkCoordIntPair, ExcavatorHandler.MineralWorldInfo> entry : cache.entrySet()) {
            overlays.add(new MineralOverlay(entry.getKey(), zoom, entry.getValue()));
        }
        return overlays;
    }

    @Override
    public String getStatusString(int dim, int bX, int bY, int bZ) {
        return "";
    }

    @Override
    public void onMiddleClick(int dim, int bX, int bZ, MapView mapView) {
    }

    @Override
    public void onDimensionChanged(int dimension, MapView mapView) {
        rebuildCache(mapView);
    }

    @Override
    public void onMapCenterChanged(double vX, double vZ, MapView mapView) {
        rebuildCache(mapView);
    }

    @Override
    public void onZoomChanged(int level, MapView mapView) {
        rebuildCache(mapView);
    }

    @Override
    public void onOverlayActivated(MapView mapView) {
        rebuildCache(mapView);
    }

    @Override
    public void onOverlayDeactivated(MapView mapView) {
    }

    @Override
    public void onDraw(MapView mapView, MapMode mapMode) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        for (Map.Entry<ChunkCoordIntPair, ExcavatorHandler.MineralWorldInfo> entry : cache.entrySet()) {
            ChunkCoordIntPair coords = entry.getKey();
            ExcavatorHandler.MineralWorldInfo mineralWorldInfo = entry.getValue();
            ExcavatorHandler.MineralMix mineral = mineralWorldInfo.getMineral();
            if (mineral == null) {
                continue;
            }
            Point2D.Double pos = mapMode.blockXZtoScreenXY(mapView, coords.getCenterXPos(), coords.getCenterZPosition());
            int zoom = mapView.getZoomLevel();
            if (zoom > 0) {
                continue;
            }
            int maxLength = zoom < -3 ? 16 : zoom < -2 ? 10 : zoom < -1 ? 4 : zoom < 0 ? 2 : 1;
            String name = mineral.name.substring(0, Math.min(mineral.name.length(), maxLength));
            drawCenteredStringWithShadow(fontRenderer, name, (int) pos.x, (int) pos.y - 2, 0xFFFFFFFF);
            if (zoom < -2) {
                String integrity = Utils.formatDouble(mineralWorldInfo.getLastKnownIntegrity() * 100, "0.##") + "%";
                drawCenteredStringWithShadow(fontRenderer, integrity, (int) pos.x, (int) pos.y + 10, 0xFFD0D0D0);
            }
        }
    }

    @Override
    public boolean onMouseInput(MapView mapview, MapMode mapmode) {
        return false;
    }

    private void rebuildCache(MapView mapView) {
        cache.clear();
        int minChunkX = ((int) Math.floor(mapView.getMinX())) >> 4;
        int minChunkZ = ((int) Math.floor(mapView.getMinZ())) >> 4;
        int maxChunkX = ((int) Math.ceil(mapView.getMaxX())) >> 4;
        int maxChunkZ = ((int) Math.ceil(mapView.getMaxZ())) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                DimensionChunkCoords coords = new DimensionChunkCoords(mapView.getDimension(), chunkX, chunkZ);
                ExcavatorHandler.MineralWorldInfo mineralWorldInfo = ExcavatorHandler.mineralCache.get(coords);
                if (mineralWorldInfo != null) {
                    cache.put(coords, mineralWorldInfo);
                }
            }
        }
        zoom = mapView.getZoomLevel();
    }

    private static void drawCenteredStringWithShadow(FontRenderer fontRenderer, String s, int x, int y, int color) {
        int width = fontRenderer.getStringWidth(s);
        fontRenderer.drawStringWithShadow(s, x - width / 2 + 1, y - 2, color);
    }

    private static int lerp(int i1, int i2, float k) {
        return i1 + Math.round((i2 - i1) * k);
    }

    private static int colorLerp(int c1, int c2, float k) {
        return lerp((c1 >> 24) & 0xFF, (c2 >> 24) & 0xFF, k) << 24
                | lerp((c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF, k) << 16
                | lerp((c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF, k) << 8
                | lerp((c1) & 0xFF, (c2) & 0xFF, k);
    }

    private static class MineralOverlay implements IMwChunkOverlay {
        private static final int COLOR_NONE = 0x44FFFFFF;
        private static final int COLOR_FULL = 0x5500FF00;
        private static final int COLOR_EMPTY = 0x55FF0000;
        private static final int COLOR_INFINITE = 0x7700AAFF;
        private static final int COLOR_BORDER_OR_MASK = 0xFF000000;
        private final Point pos;
        private final int zoom;
        private final ExcavatorHandler.MineralWorldInfo mineralWorldInfo;

        private MineralOverlay(ChunkCoordIntPair chunkPos, int zoom, ExcavatorHandler.MineralWorldInfo mineralWorldInfo) {
            this.pos = new Point(chunkPos.chunkXPos, chunkPos.chunkZPos);
            this.zoom = zoom;
            this.mineralWorldInfo = mineralWorldInfo;
        }

        @Override
        public Point getCoordinates() {
            return pos;
        }

        @Override
        public int getColor() {
            ExcavatorHandler.MineralMix mineral = mineralWorldInfo.getMineral();
            if (mineral == null) {
                return COLOR_NONE;
            }
            float integrity = mineralWorldInfo.getLastKnownIntegrity();
            if (Float.isInfinite(integrity)) {
                return COLOR_INFINITE;
            }
            return colorLerp(COLOR_EMPTY, COLOR_FULL, integrity);
        }

        @Override
        public float getFilling() {
            return 1.0f;
        }

        @Override
        public boolean hasBorder() {
            return zoom > 0;
        }

        @Override
        public float getBorderWidth() {
            return 1;
        }

        @Override
        public int getBorderColor() {
            return getColor() | COLOR_BORDER_OR_MASK;
        }
    }
}
