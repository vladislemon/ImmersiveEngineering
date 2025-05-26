package blusunrize.immersiveengineering.common.util.compat;

import com.gtnewhorizons.navigator.api.NavigatorApi;

import blusunrize.immersiveengineering.common.util.compat.navigator.MineralsButtonManager;
import blusunrize.immersiveengineering.common.util.compat.navigator.MineralsLayerManager;

public class NavigatorHelper extends IECompatModule {

    @Override
    public void preInit() {}

    @Override
    public void init() {
        NavigatorApi.registerLayerManager(new MineralsLayerManager(new MineralsButtonManager()));
    }

    @Override
    public void postInit() {}
}
