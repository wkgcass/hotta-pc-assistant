package net.cassite.hottapcassistant.multi;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.callback.BlockCallback;
import io.vproxy.base.util.exception.NoException;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.vfd.IPv4;
import io.vproxy.vfx.util.FXUtils;
import net.cassite.hottapcassistant.util.Utils;
import vjson.simple.SimpleString;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MultiHottaInstanceFlow {
    public static final String DEFAULT_RES_VERSION = "114.514";
    public static final String DEFAULT_RES_SUB_VERSION = DEFAULT_RES_VERSION + ".1919810";

    private MultiHottaInstanceFlow() {
    }

    private static final String hostsFileSuffixComment = " # hotta launcher request proxyZ, added by hotta-pc-assistant";
    static final Map<String, IPv4> resolvedHosts = new HashMap<>() {{
        put("htcdn1.wmupd.com", IPv4.fromIPv4("218.98.31.229"));
        put("htcdn2.wmupd.com", IPv4.fromIPv4("112.86.135.244"));
        put("htydhd.wmupd.com", IPv4.fromIPv4("218.98.31.231"));
        put("pmpcdn1.wmupd.com", IPv4.fromIPv4("116.148.164.57"));
    }};

    public static boolean checkLock() {
        var block = new BlockCallback<Boolean, NoException>();
        FXUtils.runOnFX(() -> block.succeeded(Utils.checkLock("multi")));
        return block.block();
    }

    public static boolean resolveHosts() {
        for (var host : resolvedHosts.keySet()) {
            InetAddress[] addrs;
            try {
                addrs = InetAddress.getAllByName(host);
            } catch (UnknownHostException ignore) {
                Logger.warn(LogType.ALERT, STR."failed to resolve \{host}, using previous value \{resolvedHosts.get(host)}");
                continue;
            }
            if (addrs.length == 0) {
                Logger.warn(LogType.ALERT, STR."failed to resolve \{host}, using previous value \{resolvedHosts.get(host)}");
                continue;
            }
            var updated = false;
            for (var h : addrs) {
                if (h instanceof Inet4Address) {
                    var v4 = IPv4.fromIPv4(h.getAddress());
                    if (v4.bytes.get(0) == 127) {
                        continue;
                    }
                    resolvedHosts.put(host, v4);
                    Logger.alert(STR."ip for \{host} updated to \{v4.formatToIPString()}");
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                Logger.warn(LogType.ALERT, STR."unable to retrieve ipv4 for \{host}, using previous value \{resolvedHosts.get(host)}");
            }
        }
        return true;
    }

    public static boolean unsetHostsFile() {
        for (int i = 0; i < 3; ++i) {
            boolean b = Utils.modifyHostsFile(lines -> lines.stream().filter(s -> !s.trim().endsWith(hostsFileSuffixComment)).toList());
            if (b) return true;
        }
        return false;
    }

    public static void makeLink(String advLocation, String normalClientLocation) throws Exception {
        io.vproxy.base.util.Utils.ExecuteResult res;
        try {
            res = io.vproxy.base.util.Utils.execute(STR."""
            mklink /d \{new SimpleString(advLocation).stringify()} \{new SimpleString(normalClientLocation).stringify()}
            """.trim(), 1000, true);
        } catch (Exception e) {
            throw new Exception(STR."creating directory link failed: \{io.vproxy.base.util.Utils.formatErr(e)}");
        }
        if (res.exitCode != 0) {
            throw new IOException("creating directory link failed: " + res.exitCode);
        }
    }

    public static String buildResListXml(String subVersion) {
        return "<?xml version=\"1.0\" ?>\n" +
               "<ResList version=\"" + subVersion + "\" tag=\"\">\n" +
               "</ResList>\n";
    }

    public static String buildConfigXml(String version, String subVersion) {
        return "<?xml version=\"1.0\" ?>\n" +
               "<config>\n" +
               "        <AppVersion>" + version + "</AppVersion>\n" +
               "        <ResVersion>" + subVersion + "</ResVersion>\n" +
               "        <UpdateResVersion>" + version + "</UpdateResVersion>\n" +
               "        <Section>" + version + "</Section>\n" +
               "        <BaseVerson appVersion=\"" + version + "\"/>\n" +
               "        <Extra>\n" +
               "                <speed>50</speed>\n" +
               "                <maxThreadCnt>5</maxThreadCnt>\n" +
               "                <minThreadCnt>1</minThreadCnt>\n" +
               "                <tagTaskThreadCnt>2</tagTaskThreadCnt>\n" +
               "        </Extra>\n" +
               "</config>\n";
    }

    public static void writeResListXml(String advLocation, String subVersion) throws Exception {
        var dir = makePatcherSDKDir(advLocation);
        var path = Path.of(dir.toString(), "ResList.xml");
        IOUtils.writeFileWithBackup(path.toString(), buildResListXml(subVersion));
    }

    public static void writeConfigXml(String advLocation, String version, String subVersion) throws Exception {
        var dir = makePatcherSDKDir(advLocation);
        var path = Path.of(dir.toString(), "config.xml");
        IOUtils.writeFileWithBackup(path.toString(), buildConfigXml(version, subVersion));
    }

    private static Path makePatcherSDKDir(String advLocation) throws IOException {
        var dir = Path.of(advLocation, "WmGpLaunch", "UserData", "Patcher", "PatcherSDK");
        if (!dir.toFile().exists()) {
            var ok = dir.toFile().mkdirs();
            if (!ok) {
                throw new IOException("failed creating PatcherSDK dir " + dir);
            }
        }
        return dir;
    }

    public static void setClientVersion(String location, String version) throws Exception {
        var dir = Path.of(location, "Client", "WindowsNoEditor", "Hotta", "Binaries", "Win64");
        if (!dir.toFile().exists()) {
            throw new IOException(dir + " does not exist");
        }
        var path = Path.of(dir.toString(), "Win_pc_version.txt");
        IOUtils.writeFileWithBackup(path.toString(), version);
    }

    public static void replaceUserDataDir(String advLocation, String onlineLocation) throws IOException {
        var advPath = Path.of(advLocation, "WmGpLaunch", "UserData");
        var advFile = advPath.toFile();
        if (advFile.exists()) {
            var ok = IOUtils.deleteDirectory(advPath.toFile());
            if (!ok) {
                throw new IOException("failed deleting UserData directory: " + advPath);
            }
        }
        IOUtils.copyDirectory(Path.of(onlineLocation, "WmGpLaunch", "UserData"), advPath);
    }

    public static void flushDNS() {
        io.vproxy.base.util.Utils.ExecuteResult res;
        try {
            res = io.vproxy.base.util.Utils.execute("ipconfig /flushdns", true);
        } catch (Exception e) {
            Logger.warn(LogType.ALERT, "flushing dns failed", e);
            return;
        }
        if (res.exitCode != 0) {
            Logger.warn(LogType.ALERT, STR."flushing dns failed \{res}");
        }
    }
}
