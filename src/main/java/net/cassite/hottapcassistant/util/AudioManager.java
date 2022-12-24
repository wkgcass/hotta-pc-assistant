package net.cassite.hottapcassistant.util;

import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioManager {
    public static final String[] ALL = new String[]{
        "/audio/simulacra/pei-pei/skill03.wav",
        "/audio/simulacra/pei-pei/skill02.wav",
        "/audio/simulacra/pei-pei/skill01.wav",
        "/audio/simulacra/ling/skill03.wav",
        "/audio/simulacra/ling/skill02.wav",
        "/audio/simulacra/ling/skill01.wav",
        "/audio/simulacra/xi-luo/skill02.wav",
        "/audio/simulacra/xi-luo/skill01.wav",
        "/audio/simulacra/tian-lang/skill03.wav",
        "/audio/simulacra/tian-lang/skill02.wav",
        "/audio/simulacra/tian-lang/skill01.wav",
        "/audio/simulacra/tian-lang/skill05.wav",
        "/audio/simulacra/tian-lang/skill04.wav",
        "/audio/simulacra/xiu-ma/skill03.wav",
        "/audio/simulacra/xiu-ma/skill02.wav",
        "/audio/simulacra/xiu-ma/skill01.wav",
        "/audio/simulacra/si-feng-yuan-yu/skill03.wav",
        "/audio/simulacra/si-feng-yuan-yu/skill02.wav",
        "/audio/simulacra/si-feng-yuan-yu/skill01.wav",
        "/audio/simulacra/wu-mi/skill03.wav",
        "/audio/simulacra/wu-mi/skill02.wav",
        "/audio/simulacra/wu-mi/skill01.wav",
        "/audio/simulacra/wu-mi/skill05.wav",
        "/audio/simulacra/wu-mi/skill04.wav",
        "/audio/simulacra/gu-lan/skill03.wav",
        "/audio/simulacra/gu-lan/skill02.wav",
        "/audio/simulacra/gu-lan/skill01.wav",
        "/audio/simulacra/nai-mei-xi-si/skill03.wav",
        "/audio/simulacra/nai-mei-xi-si/skill02.wav",
        "/audio/simulacra/nai-mei-xi-si/skill01.wav",
        "/audio/simulacra/nai-mei-xi-si/skill05.wav",
        "/audio/simulacra/nai-mei-xi-si/skill04.wav",
        "/audio/simulacra/fu-li-jia/skill03.wav",
        "/audio/simulacra/fu-li-jia/skill02.wav",
        "/audio/simulacra/fu-li-jia/skill01.wav",
        "/audio/simulacra/bu-po-xiao/skill03.wav",
        "/audio/simulacra/bu-po-xiao/skill02.wav",
        "/audio/simulacra/bu-po-xiao/skill01.wav",
        "/audio/simulacra/ma-ke/skill03.wav",
        "/audio/simulacra/ma-ke/skill02.wav",
        "/audio/simulacra/ma-ke/skill01.wav",
        "/audio/simulacra/lei-bei/skill03.wav",
        "/audio/simulacra/lei-bei/skill02.wav",
        "/audio/simulacra/lei-bei/skill01.wav",
        "/audio/simulacra/xi/skill03.wav",
        "/audio/simulacra/xi/skill02.wav",
        "/audio/simulacra/xi/skill01.wav",
        "/audio/simulacra/bai-yue-kui/skill03.wav",
        "/audio/simulacra/bai-yue-kui/skill02.wav",
        "/audio/simulacra/bai-yue-kui/skill01.wav",
        "/audio/simulacra/ke-ke-li-te/skill03.wav",
        "/audio/simulacra/ke-ke-li-te/skill02.wav",
        "/audio/simulacra/ke-ke-li-te/skill01.wav",
        "/audio/simulacra/ke-lao-di-ya/skill03.wav",
        "/audio/simulacra/ke-lao-di-ya/skill02.wav",
        "/audio/simulacra/ke-lao-di-ya/skill01.wav",
        "/audio/simulacra/ai-ge/skill03.wav",
        "/audio/simulacra/ai-ge/skill02.wav",
        "/audio/simulacra/ai-ge/skill01.wav",
        "/audio/simulacra/lan/skill03.wav",
        "/audio/simulacra/lan/skill02.wav",
        "/audio/simulacra/lan/skill01.wav",
        "/audio/simulacra/lan/skill05.wav",
        "/audio/simulacra/lan/skill04.wav",
        "/audio/simulacra/mei-li-er/skill03.wav",
        "/audio/simulacra/mei-li-er/skill02.wav",
        "/audio/simulacra/mei-li-er/skill01.wav",
        "/audio/simulacra/fen-li-er/skill03.wav",
        "/audio/simulacra/fen-li-er/skill02.wav",
        "/audio/simulacra/fen-li-er/skill01.wav",
        "/audio/simulacra/fen-li-er/skill05.wav",
        "/audio/simulacra/fen-li-er/skill04.wav",
        "/audio/simulacra/an-na-bei-la/skill03.wav",
        "/audio/simulacra/an-na-bei-la/skill02.wav",
        "/audio/simulacra/an-na-bei-la/skill01.wav",
        "/audio/simulacra/an-na-bei-la/skill05.wav",
        "/audio/simulacra/an-na-bei-la/skill04.wav",
        "/audio/simulacra/xing-huan/skill03.wav",
        "/audio/simulacra/xing-huan/skill02.wav",
        "/audio/simulacra/xing-huan/skill01.wav",
        "/audio/simulacra/xing-huan/skill05.wav",
        "/audio/simulacra/xing-huan/skill04.wav",
        "/audio/simulacra/bai-ling/skill03.wav",
        "/audio/simulacra/bai-ling/skill02.wav",
        "/audio/simulacra/bai-ling/skill01.wav",
        "/audio/simulacra/sai-mi-er/skill03.wav",
        "/audio/simulacra/sai-mi-er/skill02.wav",
        "/audio/simulacra/sai-mi-er/skill01.wav",
        "/audio/simulacra/lin-ye/skill03.wav",
        "/audio/simulacra/lin-ye/skill02.wav",
        "/audio/simulacra/lin-ye/skill01.wav",
        "/audio/simulacra/lin-ye/skill05.wav",
        "/audio/simulacra/lin-ye/skill04.wav",
        "/audio/simulacra/ai-li-si/skill03.wav",
        "/audio/simulacra/ai-li-si/skill02.wav",
        "/audio/simulacra/ai-li-si/skill01.wav",
        "/audio/simulacra/ai-li-si/skill05.wav",
        "/audio/simulacra/ai-li-si/skill04.wav",
        "/audio/simulacra/king/skill03.wav",
        "/audio/simulacra/king/skill02.wav",
        "/audio/simulacra/king/skill01.wav",
        "/audio/simulacra/wu-wan/skill03.wav",
        "/audio/simulacra/wu-wan/skill02.wav",
        "/audio/simulacra/wu-wan/skill01.wav",
        "/audio/simulacra/xi-er-da/skill03.wav",
        "/audio/simulacra/xi-er-da/skill02.wav",
        "/audio/simulacra/xi-er-da/skill01.wav",
    };
    private static final AudioManager instance = new AudioManager();

    public static AudioManager get() {
        return instance;
    }

    private AudioManager() {
    }

    private final Map<String, AudioClip> map = new ConcurrentHashMap<>();

    public AudioClip loadAudio(String path) {
        try {
            return loadAudio(path, false);
        } catch (Exception ignore) {
            return null;
        }
    }

    @SuppressWarnings("RedundantThrows")
    public AudioClip loadAudio(String path, boolean throwException) throws Exception {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        var audio = map.get(path);
        if (audio != null) {
            Logger.debug("using cached audio: " + path);
            return audio;
        }
        try {
            var res = getClass().getClassLoader().getResource(path);
            if (res == null) {
                Logger.error("unable to find resource for audio " + path);
                if (throwException) {
                    throw new IOException("cannot find audio " + path);
                }
                return null;
            }
            audio = new AudioClip(res.toExternalForm());
        } catch (Exception e) {
            Logger.error("failed loading audio " + path, e);
            if (throwException) {
                throw e;
            }
            return null;
        }
        map.put(path, audio);
        Logger.debug("new audio loaded: " + path);
        return audio;
    }
}
