/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.client;

import dan200.computercraft.ComputerCraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber( modid = ComputerCraft.MOD_ID, value = Side.CLIENT )
public final class FrameInfo
{
    private static int tick;
    private static long renderFrame;

    private FrameInfo()
    {
    }

    public static boolean getGlobalCursorBlink()
    {
        return (tick / 8) % 2 == 0;
    }

    public static long getRenderFrame()
    {
        return renderFrame;
    }

    @SubscribeEvent
    public static void onTick( TickEvent.ClientTickEvent event )
    {
        if( event.phase == TickEvent.Phase.START ) tick++;
    }

    @SubscribeEvent
    public static void onRenderTick( TickEvent.RenderTickEvent event )
    {
        if( event.phase == TickEvent.Phase.START ) renderFrame++;
    }
}
