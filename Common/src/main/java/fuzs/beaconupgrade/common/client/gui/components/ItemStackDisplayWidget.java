package fuzs.beaconupgrade.common.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

public class ItemStackDisplayWidget extends AbstractWidget {
    private final Font font;
    private final ItemStack itemStack;

    public ItemStackDisplayWidget(int x, int y, Font font, ItemStack itemStack) {
        super(x, y, 16, 16, CommonComponents.EMPTY);
        this.font = font;
        this.itemStack = itemStack;
    }

    /**
     * @see GuiGraphics#renderItemDecorations(Font, ItemStack, int, int, String)
     */
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.renderFakeItem(this.itemStack, this.getX(), this.getY());
        int posX = this.getX() + 19 - 2 - this.font.width(this.getMessage());
        int posY = this.getY() + 6 + 3;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        guiGraphics.drawString(this.font, this.getMessage(), posX, posY, -1);
        if (this.hasHighlight()) {
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, this.getX(), this.getY(), 0);
        }

        guiGraphics.pose().popPose();
    }

    private boolean hasHighlight() {
        return this.isHoveredOrFocused() && this.tooltip.get() != null;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // NO-OP
    }
}
