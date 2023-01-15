package net.cassite.hottapcassistant.multi;

import net.cassite.hottapcassistant.util.Utils;
import vjson.simple.SimpleString;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MultiHottaInstanceFlow {
    private MultiHottaInstanceFlow() {
    }

    private static final String hostsFileSuffixComment = " # hotta launcher request proxyZ, added by hotta-pc-assistant";
    private static final String[] hostsToAdd = new String[]{
        "htcdn1.wmupd.com",
        "htcdn2.wmupd.com",
        "htydhd.wmupd.com",
        "pmpcdn1.wmupd.com",
    };

    public static boolean setHostsFile() {
        return Utils.modifyHostsFile(lines -> {
            var ls = new ArrayList<>(lines.stream().filter(s -> !s.trim().endsWith(hostsFileSuffixComment)).toList());
            for (var h : hostsToAdd) {
                ls.add("127.0.0.1\t" + h + hostsFileSuffixComment);
            }
            return ls;
        });
    }

    public static boolean unsetHostsFile() {
        return Utils.modifyHostsFile(lines -> lines.stream().filter(s -> !s.trim().endsWith(hostsFileSuffixComment)).toList());
    }

    public static void makeLink(String advLocation, String normalClientLocation) throws IOException {
        var pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "mklink /d " + new SimpleString(advLocation).stringify() + " " + new SimpleString(normalClientLocation).stringify());
        Process process = pb.start();
        try {
            process.waitFor(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
        if (process.isAlive()) {
            throw new IOException("creating directory link timeout");
        }
        int code = process.exitValue();
        if (code != 0) {
            throw new IOException("creating directory link failed: " + code);
        }
    }

    public static String buildResListXml(String subVersion) {
        return "<?xml version=\"1.0\" ?>\n" +
               "<ResList version=\"" + subVersion + "\" tag=\"\">\n" +
               "</ResList>\n";
    }

    public static void writeResListXml(String advLocation, String subVersion) throws IOException {
        var dir = Path.of(advLocation, "WmGpLaunch", "UserData", "Patcher", "PatcherSDK");
        var path = Path.of(dir.toString(), "ResList.xml");
        if (!dir.toFile().exists()) {
            var ok = dir.toFile().mkdirs();
            if (!ok) {
                throw new IOException("failed creating PatcherSDK dir " + dir);
            }
        }
        Utils.writeFile(path, buildResListXml(subVersion));
    }
}
