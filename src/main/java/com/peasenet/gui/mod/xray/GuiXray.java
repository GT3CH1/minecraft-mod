/*
 * Copyright (c) 2022. Gavin Pease and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.peasenet.gui.mod.xray;

import com.peasenet.gui.GuiElement;
import com.peasenet.gui.elements.Gui;
import com.peasenet.gui.elements.GuiClick;
import com.peasenet.gui.elements.GuiToggle;
import com.peasenet.main.GavinsMod;
import com.peasenet.main.GavinsModClient;
import com.peasenet.main.Mods;
import com.peasenet.main.Settings;
import com.peasenet.util.RenderUtils;
import com.peasenet.util.color.Colors;
import com.peasenet.util.math.PointD;
import net.minecraft.block.Block;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;

/**
 * @author gt3ch1
 * @version 7/4/2022
 * A gui that allows the player to search for blocks and add them to the xray list.
 */
public class GuiXray extends GuiElement {

    /**
     * The list of currently visible blocks.
     */
    private final LinkedHashSet<Block> visibleBlocks = blockList();
    /**
     * The background gui element.
     */
    private Gui box;
    /**
     * The button that moves to a page behind the current one.
     */
    private GuiClick prevButton;
    /**
     * The button that moves to a page ahead of the current one.
     */
    private GuiClick nextButton;
    /**
     * The width of the main gui.
     */
    private int width;
    /**
     * The height of the main gui.
     */
    private int height;
    /**
     * The x coordinate of the main gui.
     */
    private int x;
    /**
     * The y coordinate of the main gui.
     */
    private int y;
    /**
     * The current page of the gui.
     */
    private int page = 0;
    /**
     * The number of pages in the gui.
     */
    private int pageCount = 0;
    /**
     * The number of blocks per page.
     */
    private int blocksPerPage = 0;
    /**
     * The number of blocks per row.
     */
    private int blocksPerRow = 0;
    /**
     * The search field.
     */
    private TextFieldWidget search;
    /**
     * The toggle element to show all blocks or just enabled blocks.
     */
    private GuiToggle enabledOnly;

    /**
     * Creates a new GUI menu with the given title.
     */
    public GuiXray() {
        super(Text.translatable("gavinsmod.mod.render.xray"));
    }

    /**
     * Gets the list of all blocks that do not translate to "air".
     *
     * @return The list of all blocks.
     */
    private static LinkedHashSet<Block> blockList() {
        var list = new ArrayList<Block>();
        Registry.BLOCK.stream().sorted(Comparator.comparing(a -> I18n.translate(a.getTranslationKey()))).filter(b -> !b.asItem().getTranslationKey().contains("air")).forEach(list::add);
        return new LinkedHashSet<>(list);
    }

    @Override
    public void init() {
        int screenWidth = GavinsModClient.getMinecraftClient().getWindow().getScaledWidth();
        int screenHeight = GavinsModClient.getMinecraftClient().getWindow().getScaledHeight();
        width = (int) (screenWidth * 0.9f);
        height = (int) (screenHeight * 0.78f);
        x = screenWidth / 20;
        y = (screenHeight / 20) + 20;
        box = new Gui(new PointD(x, y), width, height, Text.literal(""));
        box.setBackground(Colors.INDIGO);
        int blocksPerColumn = (height / 18);
        blocksPerRow = width / 18;
        blocksPerPage = blocksPerRow * blocksPerColumn;
        pageCount = (int) Math.ceil((double) blockList().size() / blocksPerPage);
        parent = GavinsMod.guiSettings;
        search = new TextFieldWidget(textRenderer, x + width / 2 - 75, y - 15, 150, 12, Text.empty()) {
            @Override
            public boolean charTyped(char chr, int keyCode) {
                var pressed = super.charTyped(chr, keyCode);
                page = 0;
                updateBlockList();
                return pressed;
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                var pressed = super.keyPressed(keyCode, scanCode, modifiers);
                page = 0;
                updateBlockList();
                return pressed;
            }
        };
        prevButton = new GuiClick(new PointD(x + (width >> 1) - 89, y - 16), 13, 13, Text.empty());
        prevButton.setCallback(this::pageDown);
        nextButton = new GuiClick(new PointD(x + width / 2 + 76, y - 16), 13, 13, Text.empty());
        nextButton.setCallback(this::pageUp);
        enabledOnly = new GuiToggle(new PointD(x + width / 2 - 170, y - 15), 80, 10, Text.literal("Enabled Only"));
        enabledOnly.setCallback(() -> {
            page = 0;
            updateBlockList();
        });
        addSelectableChild(search);
        updateBlockList();
        super.init();
    }

    /**
     * Decrements the page by one.
     */
    private void pageDown() {
        if (page > 0) {
            page--;
            updateBlockList();
        }
    }

    /**
     * Increments the page by one.
     */
    private void pageUp() {
        if (page < pageCount - 1) {
            page++;
            updateBlockList();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        search.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        search.charTyped(chr, keyCode);
        return super.charTyped(chr, keyCode);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < blocksPerPage; i++) {
            if (i > visibleBlocks.size() - 1) break;
            Block block = visibleBlocks.toArray(new Block[0])[i];
            if (block == null) return;
            var stack = block.asItem().getDefaultStack();
            var blockX = (i % (blocksPerRow)) * 18 + x + 2;
            var blockY = (i / blocksPerRow) * 18 + y + 5;

            if (Settings.isXrayBlock(block)) {
                fill(matrixStack, blockX, blockY, blockX + 16, blockY + 16, Settings.getColor("gui.color.enabled").getAsInt(0.5f));
                RenderUtils.drawOutline(Colors.WHITE.getAsFloatArray(), blockX, blockY, blockX + 16, blockY + 16, matrixStack);
            }
            if (mouseX > blockX && mouseX < blockX + 16 && mouseY > blockY && mouseY < blockY + 16) {
                fill(matrixStack, blockX, blockY, blockX + 16, blockY + 16, Settings.getColor("gui.color.foreground").getAsInt(0.5f));
                RenderUtils.drawOutline(Colors.WHITE.getAsFloatArray(), blockX, blockY, blockX + 16, blockY + 16, matrixStack);
                renderTooltip(matrixStack, Text.translatable(stack.getTranslationKey()), mouseX, mouseY);
            }
            client.getItemRenderer().renderGuiItemIcon(stack, blockX, blockY);
        }

        box.render(matrixStack, textRenderer, mouseX, mouseY, delta);
        search.render(matrixStack, mouseX, mouseY, delta);
        prevButton.render(matrixStack, textRenderer, mouseX, mouseY, delta);
        nextButton.render(matrixStack, textRenderer, mouseX, mouseY, delta);
        enabledOnly.render(matrixStack, textRenderer, mouseX, mouseY, delta);
        textRenderer.draw(matrixStack, Text.literal(String.valueOf('\u25c0')), x + width / 2 - 86, y - 13, Colors.WHITE.getAsInt());
        textRenderer.draw(matrixStack, Text.literal(String.valueOf('\u25b6')), x + width / 2 + 80, y - 13, Colors.WHITE.getAsInt());
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // check if the mouse is over the search box
        if (search.isMouseOver(mouseX, mouseY)) {
            search.mouseClicked(mouseX, mouseY, button);
            search.setTextFieldFocused(true);
            return true;
        }
        if (prevButton.mouseWithinGui(mouseX, mouseY)) {
            prevButton.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (nextButton.mouseWithinGui(mouseX, mouseY)) {
            nextButton.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (enabledOnly.mouseWithinGui(mouseX, mouseY)) {
            enabledOnly.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (!(mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height)) return false;
        search.setTextFieldFocused(false);

        int blockIndex = (int) ((mouseY - y) / 19) * blocksPerRow + (int) ((mouseX - x) / 18);
        if (blockIndex > visibleBlocks.size() - 1) return false;

        Block block = visibleBlocks.toArray(new Block[0])[blockIndex];
        if (block == null || button != 0) return false;
        if (Settings.isXrayBlock(block)) Settings.removeXrayBlock(block);
        else Settings.addXrayBlock(block);
        Mods.getMod("xray").reload();

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Updates the list of currently visible blocks to reflect that of the search field, and the current page.
     */
    private void updateBlockList() {
        var searchText = search.getText().toLowerCase();
        visibleBlocks.clear();
        var tmpBlocks = new ArrayList<Block>();
        blockList().stream().filter(block -> block.getTranslationKey().toLowerCase().contains(searchText)).forEach(tmpBlocks::add);
        // get blocks in block list that are within the page.
        var enabled = enabledOnly.isOn();
        if (enabled) tmpBlocks = new ArrayList<>(tmpBlocks.stream().filter(Settings::isXrayBlock).toList());
        pageCount = (int) Math.ceil((double) tmpBlocks.size() / blocksPerPage);
        for (int i = page * blocksPerPage; i < page * blocksPerPage + blocksPerPage; i++) {
            if (tmpBlocks.isEmpty() || i > tmpBlocks.size() - 1) break;
            var block = tmpBlocks.get(i);
            if (block != null && block.getTranslationKey().toLowerCase().contains(searchText)) {
                if (enabled && !Settings.isXrayBlock(block)) continue;
                visibleBlocks.add(block);
            }
        }
    }
}
