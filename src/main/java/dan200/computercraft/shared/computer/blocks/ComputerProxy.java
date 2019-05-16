/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;

/**
 * A proxy object for computer objects, delegating to {@link IComputer} or {@link TileComputer} where appropriate.
 */
public abstract class ComputerProxy
{
    protected abstract TileComputerBase getTile();

    public void turnOn()
    {
        TileComputerBase tile = getTile();
        ServerComputer computer = tile.getServerComputer();
        if( computer == null )
        {
            tile.m_startOn = true;
        }
        else
        {
            computer.turnOn();
        }
    }

    public void shutdown()
    {
        TileComputerBase tile = getTile();
        ServerComputer computer = tile.getServerComputer();
        if( computer == null )
        {
            tile.m_startOn = false;
        }
        else
        {
            computer.shutdown();
        }
    }

    public void reboot()
    {
        TileComputerBase tile = getTile();
        ServerComputer computer = tile.getServerComputer();
        if( computer == null )
        {
            tile.m_startOn = true;
        }
        else
        {
            computer.reboot();
        }
    }

    public int assignID()
    {
        TileComputerBase tile = getTile();
        ServerComputer computer = tile.getServerComputer();
        return computer == null ? tile.getComputerID() : computer.getID();
    }

    public boolean isOn()
    {
        ServerComputer computer = getTile().getServerComputer();
        return computer != null && computer.isOn();
    }

    public String getLabel()
    {
        TileComputerBase tile = getTile();
        ServerComputer computer = tile.getServerComputer();
        return computer == null ? tile.getLabel() : computer.getLabel();
    }
}
