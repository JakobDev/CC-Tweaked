/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.IComputer;

public interface IComputerTile
{
    int getComputerID();

    void setComputerID( int id );

    String getLabel();

    void setLabel( String label );

    ComputerFamily getFamily();

    @Deprecated
    IComputer getComputer();
}
