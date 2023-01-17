package net.cassite.hottapcassistant.config;

import net.cassite.hottapcassistant.entity.TofServer;
import net.cassite.hottapcassistant.feed.Feed;
import io.vproxy.vfx.util.Logger;
import net.cassite.hottapcassistant.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TofServerListConfig {
    private TofServerListConfig() {
    }

    public static List<TofServer> read() throws IOException {
        String hosts = Feed.get().tofServerHosts;
        if (hosts == null) {
            hosts = Utils.readClassPath("config/global-server-hosts");
        }
        String names = Feed.get().tofServerNames;
        if (names == null) {
            names = Utils.readClassPath("config/global-server-names");
        }
        return formatTofServers(hosts, names);
    }

    private static List<TofServer> formatTofServers(String hosts, String names) {
        var hostMap = new HashMap<String, String>();
        var lines = hosts.split("\n");
        for (var line : lines) {
            if (line.isBlank()) {
                continue;
            }
            line = line.trim();
            var split = line.split("\\s+");
            if (split.length != 2) {
                Logger.warn("invalid line in hosts file: " + line);
                continue;
            }
            var ip = split[0].trim();
            var host = split[1].trim();
            if (host.endsWith(".")) {
                host = host.substring(0, host.length() - 1);
            }
            hostMap.put(host, ip);
        }
        var ret = new ArrayList<TofServer>();
        lines = names.split("\n");
        String currentRegion = null;
        for (var line : lines) {
            if (line.isBlank()) {
                continue;
            }
            line = line.trim();
            if (line.startsWith(":")) {
                line = line.substring(":".length());
                line = line.trim();
                currentRegion = line;
                continue;
            }
            var split = line.split("\t+");
            if (split.length != 2) {
                Logger.warn("invalid line in names file: " + line);
                continue;
            }
            var name = split[0].trim();
            var host = split[1].trim();
            var ip = hostMap.get(host);
            if (currentRegion == null) {
                Logger.warn("missing region for name line: " + line);
                continue;
            }
            if (ip == null) {
                Logger.warn("missing ip for name line: " + line);
                continue;
            }
            var tofServer = new TofServer(currentRegion, name, host, ip);
            ret.add(tofServer);
        }
        return ret;
    }

    private static final String hostsFileSuffixComment = " # tof server, added by hotta-pc-assistant";

    public static boolean setHosts(List<TofServer> tofServers) {
        return Utils.modifyHostsFile(lines -> {
            var ls = new ArrayList<>(lines.stream().filter(s -> !s.trim().endsWith(hostsFileSuffixComment)).toList());
            for (var s : tofServers) {
                if (s.selected) {
                    ls.add(s.ip + "\t" + s.domain + hostsFileSuffixComment);
                }
            }
            return ls;
        });
    }
}
