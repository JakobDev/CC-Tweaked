/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * A stub mod for CC: Tweaked. This doesn't have any functionality (everything of note is done in
 * {@link ComputerCraft}), but people may depend on this if they require CC: Tweaked functionality.
 */
@Mod(
    modid = "cctweaked", name = ComputerCraft.NAME, version = ComputerCraft.VERSION,
    acceptableRemoteVersions = "*"
)
public class CCTweaked
{
    @NetworkCheckHandler
    public boolean onNetworkConnect( Map<String, String> mods, Side side )
    {
        return true;
    }
}
