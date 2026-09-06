package fuzs.beaconupgrade.common.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

/**
 * A string widget that always renders text centered and handles overflowing text via scrolling.
 */
public class ScrollingStringWidget extends AbstractStringWidget {

    public ScrollingStringWidget(int posX, int posY, int width, int height, Component component, Font font) {
        super(posX, posY, width, height, component, font);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderScrollingString(guiGraphics, this.getFont(), 2, this.getColor());
    }
}
