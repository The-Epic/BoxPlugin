package me.twostinkysocks.boxplugin.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.twostinkysocks.boxplugin.BoxPlugin;

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
