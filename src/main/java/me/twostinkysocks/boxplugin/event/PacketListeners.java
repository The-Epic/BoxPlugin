package me.twostinkysocks.boxplugin.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import me.clip.placeholderapi.libs.kyori.adventure.platform.viaversion.ViaFacet;
import me.twostinkysocks.boxplugin.BoxPlugin;
import net.minecraft.server.bossevents.BossBattleCustom;
import net.minecraft.server.bossevents.BossBattleCustomData;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.world.BossBattle;

public class PacketListeners {
    public PacketListeners() {
        BoxPlugin.instance.getProtocolManager().addPacketListener(new PacketAdapter(
                BoxPlugin.instance,
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.BOSS
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
            }
        });
    }
}
