package blusunrize.aquatweaks.api;

import net.minecraft.world.IBlockAccess;

/**
 * @author BluSunrize - 14.06.2015
 *
 * Only blocks can implement this, not TileEntities
 */
public interface IAquaConnectable
{
    /**
     * @return whether the side of the adjacent fluid should be rendered. Coordinates passed are for this block, not the adjacent
     */
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, int side);

    /**
     * A general check to prevent unnecessary calculations of rendering.
     * @return false to prevent this block from being considered for rendering
     */
    public boolean shouldRenderFluid(IBlockAccess world, int x, int y, int z);
}