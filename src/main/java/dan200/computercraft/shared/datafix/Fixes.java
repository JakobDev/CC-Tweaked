/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.datafix;

import dan200.computercraft.ComputerCraft;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;

public final class Fixes
{
    public static final int VERSION = 1;

    private Fixes() {}

    public static void register( CompoundDataFixer fixer )
    {
        ModFixs fixes = fixer.init( ComputerCraft.MOD_ID, VERSION );
        fixes.registerFix( FixTypes.BLOCK_ENTITY, new TileEntityDataFixer() );
    }
}
