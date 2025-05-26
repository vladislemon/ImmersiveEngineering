package blusunrize.immersiveengineering.common.util.compat.navigator;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.buttons.ButtonManager;

import blusunrize.immersiveengineering.ImmersiveEngineering;

public class MineralsButtonManager extends ButtonManager {

    @Override
    public ResourceLocation getIcon(SupportedMods mod, String theme) {
        return new ResourceLocation(ImmersiveEngineering.MODID.toLowerCase(), "textures/icons/oreveins.png");
    }

    @Override
    public String getButtonText() {
        return StatCollector.translateToLocal("immersiveengineering.button.orevein");
    }
}
