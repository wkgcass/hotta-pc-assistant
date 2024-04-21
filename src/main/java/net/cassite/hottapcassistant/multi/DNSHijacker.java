package net.cassite.hottapcassistant.multi;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.coll.Tuple;
import io.vproxy.pni.Allocator;
import io.vproxy.vfd.IPv4;
import io.vproxy.vfd.IPv6;
import io.vproxy.vpacket.AbstractIpPacket;
import io.vproxy.windivert.WinDivert;
import io.vproxy.windivert.WinDivertException;
import io.vproxy.windivert.WinDivertPacketRecv;
import io.vproxy.windivert.WinDivertRcvSndCtx;
import io.vproxy.windivert.hostsmanager.DNSResponder;
import io.vproxy.windivert.hostsmanager.HostsData;
import io.vproxy.windivert.hostsmanager.HostsStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DNSHijacker {
    private static final IPv4 RESOLVED_IP = IPv4.fromIPv4("127.0.0.1");
    private static final IPv6 RESOLVED_IPv6 = IPv6.fromIPv6("::1");

    private volatile WinDivert winDivert;
    private final DNSResponder responder;

    public DNSHijacker(Map<String, IPv4> hijackingHosts) {
        var hosts = new HostsStorage();
        hosts.addOrReplace(1, new HostsData(new HashMap<>() {{
            for (var host : hijackingHosts.keySet()) {
                if (!host.endsWith(".")) {
                    host += ".";
                }
                put(host, new Tuple<>(
                    Set.of(RESOLVED_IP),
                    Set.of(RESOLVED_IPv6)
                ));
            }
        }}));
        responder = new DNSResponder(hosts);
    }

    public void start() throws Exception {
        winDivert = WinDivert.open("outbound && ip && udp.DstPort == 53");
        new Thread(this::handle, "dns-hijacker").start();
    }

    public void destroy() {
        var winDivert = this.winDivert;
        if (winDivert != null) {
            this.winDivert = null;
            winDivert.close();
            Logger.alert("dns-hijacker terminated!");
        }
    }

    private void handle() {
        try (var allocator = Allocator.ofConfined()) {
            var ctx = new WinDivertRcvSndCtx(allocator);
            while (true) {
                var winDivert = this.winDivert;
                if (winDivert == null || winDivert.isClosed()) {
                    break;
                }
                WinDivertPacketRecv recv;
                try {
                    recv = winDivert.receive(ctx);
                } catch (WinDivertException e) {
                    if (winDivert.isClosed()) {
                        break;
                    }
                    Logger.error(LogType.SYS_ERROR, "WinDivert: failed to receive packet", e);
                    continue;
                }
                var ok = handle(ctx, recv.packet());
                if (ok) {
                    continue;
                }
                try {
                    winDivert.send(recv.raw(), ctx);
                } catch (WinDivertException e) {
                    if (winDivert.isClosed()) {
                        break;
                    }
                    Logger.error(LogType.SYS_ERROR, "WinDivert: failed to send packet", e);
                }
            }
        }
        Logger.alert("dns-hijacker thread terminated!");
    }

    private boolean handle(WinDivertRcvSndCtx ctx, AbstractIpPacket pkt) {
        try {
            return responder.handle(pkt, winDivert, ctx);
        } catch (WinDivertException e) {
            if (winDivert.isClosed()) {
                return true; // return true to do nothing later
            }
            Logger.error(LogType.SYS_ERROR, "WinDivert: failed to send packet", e);
            return false;
        }
    }
}
