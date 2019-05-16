/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.client.gui;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.media.inventory.ContainerHeldItem;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;

public class GuiPocketComputer extends GuiComputer
{
    public GuiPocketComputer( ContainerHeldItem container )
    {
        super(
            container,
            ComputerCraft.Items.pocketComputer.getFamily( container.getStack() ),
            ItemPocketComputer.createClientComputer( container.getStack() ),
            ComputerCraft.terminalWidth_pocketComputer,
            ComputerCraft.terminalHeight_pocketComputer
        );
    }
}
