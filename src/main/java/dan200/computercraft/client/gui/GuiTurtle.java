/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client.gui;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.client.gui.widgets.WidgetWrapper;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiTurtle extends GuiContainer
{
    private static final ResourceLocation BACKGROUND_NORMAL = new ResourceLocation( "computercraft", "textures/gui/turtle_normal.png" );
    private static final ResourceLocation BACKGROUND_ADVANCED = new ResourceLocation( "computercraft", "textures/gui/turtle_advanced.png" );

    private ContainerTurtle m_container;

    private final ComputerFamily m_family;
    private final ClientComputer m_computer;

    private WidgetTerminal terminal;
    private WidgetWrapper terminalWrapper;

    public GuiTurtle( TileTurtle turtle, ContainerTurtle container )
    {
        super( container );

        m_container = container;
        m_family = turtle.getFamily();
        m_computer = turtle.getClientComputer();

        xSize = 254;
        ySize = 217;
    }

    @Override
    protected void initGui()
    {
        super.initGui();
        mc.keyboardListener.enableRepeatEvents( true );

        int termPxWidth = ComputerCraft.terminalWidth_turtle * FixedWidthFontRenderer.FONT_WIDTH;
        int termPxHeight = ComputerCraft.terminalHeight_turtle * FixedWidthFontRenderer.FONT_HEIGHT;

        terminal = new WidgetTerminal(
            mc, () -> m_computer,
            ComputerCraft.terminalWidth_turtle,
            ComputerCraft.terminalHeight_turtle,
            2, 2, 2, 2
        );
        terminalWrapper = new WidgetWrapper( terminal, 2 + 8 + guiLeft, 2 + 8 + guiTop, termPxWidth, termPxHeight );

        children.add( terminalWrapper );
        setFocused( terminalWrapper );
    }

    @Override
    public void onGuiClosed()
    {
        children.remove( terminal );
        terminal = null;
        mc.keyboardListener.enableRepeatEvents( false );
    }

    @Override
    public void tick()
    {
        super.tick();
        terminal.update();
    }

    private void drawSelectionSlot( boolean advanced )
    {

        // Draw selection slot
        int slot = m_container.getSelectedSlot();
        if( slot >= 0 )
        {
            GlStateManager.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
            int slotX = (slot % 4);
            int slotY = (slot / 4);
            mc.getTextureManager().bindTexture( advanced ? BACKGROUND_ADVANCED : BACKGROUND_NORMAL );
            drawTexturedModalRect( guiLeft + m_container.m_turtleInvStartX - 2 + slotX * 18, guiTop + m_container.m_playerInvStartY - 2 + slotY * 18, 0, 217, 24, 24 );
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        // Draw term
        terminal.draw( terminalWrapper.getX(), terminalWrapper.getY() );

        // Draw border/inventory
        GlStateManager.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
        boolean advanced = m_family == ComputerFamily.Advanced;
        mc.getTextureManager().bindTexture( advanced ? BACKGROUND_ADVANCED : BACKGROUND_NORMAL );
        drawTexturedModalRect( guiLeft, guiTop, 0, 0, xSize, ySize );

        drawSelectionSlot( advanced );
    }

    @Override
    public void render( int mouseX, int mouseY, float partialTicks )
    {
        drawDefaultBackground();
        super.render( mouseX, mouseY, partialTicks );
        renderHoveredToolTip( mouseX, mouseY );
    }
}
