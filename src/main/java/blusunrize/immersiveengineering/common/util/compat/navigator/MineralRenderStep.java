package blusunrize.immersiveengineering.common.util.compat.navigator;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

import com.gtnewhorizons.navigator.api.model.steps.UniversalInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import blusunrize.immersiveengineering.common.util.Utils;

public class MineralRenderStep extends UniversalInteractableStep<MineralLocation> {

    public MineralRenderStep(MineralLocation location) {
        super(location);
        setFontScale(1.2f);
        setMinScale(1);
    }

    @Override
    public void preRender(double topX, double topY, float drawScale, double zoom) {
        double iconSize = isXaero ? 10 * zoom : 10 * drawScale * Math.pow(2, zoom);
        setSize(iconSize);
        setOffset(-iconSize / 2);
    }

    @Override
    public void draw(double topX, double topY, float drawScale, double zoom) {
        boolean mineralPresent = location.mineralWorldInfo.mineral != null;

        if (mineralPresent && location.getIconFromPrimaryOre() != null) {
            DrawUtils.drawQuad(location.getIconFromPrimaryOre(), topX, topY, width, height, Integer.MAX_VALUE, 255);
        }

        if (mineralPresent && location.isDepleted()) {
            DrawUtils.drawRect(topX, topY, width, height, 0x000000, 150);
        }

        if (!mineralPresent) {
            IIcon icon = Blocks.stone.getIcon(0, 0);
            DrawUtils.drawQuad(icon, topX, topY, width, height, 0xFFFFFF, 255);
        }

        if (location.isActiveAsWaypoint()) {
            final double thickness = width / 8;
            DrawUtils.drawHollowRect(topX, topY, width, height, 0xFFD700, 204, thickness);
        }
    }

    @Override
    public void getTooltip(List<String> list) {
        if (location.mineralWorldInfo.mineral != null) {
            list.add(location.mineralWorldInfo.getMineral().name);
            float lastKnownIntegrity = location.mineralWorldInfo.getLastKnownIntegrity();
            list.add(Utils.formatDouble(lastKnownIntegrity * 100, "0.##") + "%");
        }
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height;
    }
}
