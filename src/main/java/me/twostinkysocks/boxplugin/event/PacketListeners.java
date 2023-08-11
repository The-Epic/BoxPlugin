package me.twostinkysocks.boxplugin.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.lumine.mythic.bukkit.utils.lib.http.util.EntityUtils;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import me.clip.placeholderapi.libs.kyori.adventure.platform.viaversion.ViaFacet;
import me.twostinkysocks.boxplugin.BoxPlugin;
import net.minecraft.server.bossevents.BossBattleCustom;
import net.minecraft.server.bossevents.BossBattleCustomData;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.world.BossBattle;
import org.bukkit.Bukkit;

public class PacketListeners {
    public PacketListeners() {
        System.out.println("Enabling Sound Listener...");
        BoxPlugin.instance.getProtocolManager().addPacketListener(new PacketAdapter(
                BoxPlugin.instance,
                PacketType.Play.Server.NAMED_SOUND_EFFECT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

            }
        });
    }
}
