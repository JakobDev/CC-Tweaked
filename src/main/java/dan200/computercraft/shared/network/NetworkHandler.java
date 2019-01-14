/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.network.client.*;
import dan200.computercraft.shared.network.server.ComputerActionServerMessage;
import dan200.computercraft.shared.network.server.QueueEventServerMessage;
import dan200.computercraft.shared.network.server.RequestComputerMessage;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.fabricmc.fabric.networking.PacketContext;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NetworkHandler
{
    private static final Int2ObjectMap<BiConsumer<PacketContext, PacketByteBuf>> packetReaders = new Int2ObjectOpenHashMap<>();
    private static final Object2IntMap<Class<?>> packetIds = new Object2IntOpenHashMap<>();

    private static final Identifier ID = new Identifier( ComputerCraft.MOD_ID, "main" );

    private NetworkHandler()
    {
    }

    public static void setup()
    {
        CustomPayloadPacketRegistry.CLIENT.register( ID, NetworkHandler::receive );
        CustomPayloadPacketRegistry.SERVER.register( ID, NetworkHandler::receive );

        // Server messages
        registerMainThread( 0, ComputerActionServerMessage::new );
        registerMainThread( 1, QueueEventServerMessage::new );
        registerMainThread( 2, RequestComputerMessage::new );

        // Client messages
        registerMainThread( 10, ChatTableClientMessage::new );
        registerMainThread( 11, ComputerDataClientMessage::new );
        registerMainThread( 12, ComputerDeletedClientMessage::new );
        registerMainThread( 13, ComputerTerminalClientMessage::new );
        registerMainThread( 14, PlayRecordClientMessage.class, PlayRecordClientMessage::new );
    }

    public static void sendToPlayer( PlayerEntity player, NetworkMessage packet )
    {
        ((ServerPlayerEntity) player).networkHandler.sendPacket(
            new CustomPayloadClientPacket( ID, encode( packet ) )
        );
    }

    public static void sendToAllPlayers( NetworkMessage packet )
    {
        FabricLoader.INSTANCE.getEnvironmentHandler().getServerInstance().getPlayerManager().sendToAll(
            new CustomPayloadClientPacket( ID, encode( packet ) )
        );
    }

    public static void sendToServer( NetworkMessage packet )
    {
        MinecraftClient.getInstance().player.networkHandler.sendPacket(
            new CustomPayloadServerPacket( ID, encode( packet ) )
        );
    }

    public static void sendToAllAround( NetworkMessage packet, World world, Vec3d pos, double range )
    {
        FabricLoader.INSTANCE.getEnvironmentHandler().getServerInstance().getPlayerManager().sendToAround(
            null, pos.x, pos.y, pos.z, range, world.getDimension().getType(),
            new CustomPayloadClientPacket( ID, encode( packet ) )
        );
    }

    private static void receive( PacketContext context, PacketByteBuf buffer )
    {
        int type = buffer.readByte();
        packetReaders.get( type ).accept( context, buffer );
    }

    private static PacketByteBuf encode( NetworkMessage message )
    {
        PacketByteBuf buf = new PacketByteBuf( Unpooled.buffer() );
        buf.writeByte( packetIds.getInt( message.getClass() ) );
        message.toBytes( buf );
        return buf;
    }

    /**
     * /**
     * Register packet, and a thread-unsafe handler for it.
     *
     * @param id      The identifier for this packet type
     * @param factory The factory for this type of packet.
     */
    private static <T extends NetworkMessage> void registerMainThread( int id, Supplier<T> factory )
    {
        registerMainThread( id, getType( factory ), buf -> {
            T instance = factory.get();
            instance.fromBytes( buf );
            return instance;
        } );
    }

    /**
     * /**
     * Register packet, and a thread-unsafe handler for it.
     *
     * @param id      The identifier for this packet type
     * @param decoder The factory for this type of packet.
     */
    private static <T extends NetworkMessage> void registerMainThread( int id, Class<T> type, Function<PacketByteBuf, T> decoder )
    {
        packetIds.put( type, id );
        packetReaders.put( id, ( context, buf ) -> {
            T result = decoder.apply( buf );
            context.getTaskQueue().execute( () -> result.handle( context ) );
        } );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> Class<T> getType( Supplier<T> supplier )
    {
        return (Class<T>) supplier.get().getClass();
    }
}
