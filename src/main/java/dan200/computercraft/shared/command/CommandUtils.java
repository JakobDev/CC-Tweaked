/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;

public final class CommandUtils
{
    private CommandUtils() {}

    public static boolean isPlayer( ICommandSender sender )
    {
        return sender instanceof EntityPlayerMP
            && !(sender instanceof FakePlayer)
            && ((EntityPlayerMP) sender).connection != null;
    }
}
