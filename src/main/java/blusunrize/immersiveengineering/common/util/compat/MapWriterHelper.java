package blusunrize.immersiveengineering.common.util.compat;

import blusunrize.immersiveengineering.common.util.compat.mapwriter.IEMineralsMwDataProvider;
import mapwriter.api.MwAPI;

public class MapWriterHelper extends IECompatModule {
    @Override
    public void preInit() {
    }

    @Override
    public void init() {
        MwAPI.registerDataProvider("Minerals", new IEMineralsMwDataProvider());
    }

    @Override
    public void postInit() {
    }
}
