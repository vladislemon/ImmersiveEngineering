package blusunrize.immersiveengineering.common.util.compat;

import blusunrize.immersiveengineering.common.util.compat.navigator.MineralsButtonManager;
import blusunrize.immersiveengineering.common.util.compat.navigator.MineralsLayerManager;
import com.gtnewhorizons.navigator.api.NavigatorApi;
import cpw.mods.fml.common.FMLCommonHandler;

public class NavigatorHelper extends IECompatModule {

    @Override
    public void preInit() {
    }

    @Override
    public void init() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            NavigatorApi.registerLayerManager(new MineralsLayerManager(new MineralsButtonManager()));
        }
    }

    @Override
    public void postInit() {
    }
}
