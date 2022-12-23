package net.cassite.hottapcassistant.util;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageManager {
    public static final String[] ALL = new String[]{
        "/images/bg/bg0.jpg",
        "/images/bg/bg1.png",
        "/images/buff/ai-li-si-simulacra.png",
        "/images/buff/bee.png",
        "/images/buff/burn-settle.png",
        "/images/buff/charge.png",
        "/images/buff/dodge.png",
        "/images/buff/hui-qi.png",
        "/images/buff/li-zi-zhuo-shao.png",
        "/images/buff/ling-guang-yu-jing.png",
        "/images/buff/ling-guang-taunt.png",
        "/images/buff/mo-shu-shi-jian.png",
        "/images/buff/optical-space.png",
        "/images/buff/shi-zi-zhuo-shao.png",
        "/images/buff/xing-huan-simulacra.png",
        "/images/buff/ying-yue-zhi-jing.png",
        "/images/buff/yong-dong.png",
        "/images/buff/zhi-han-chang-yu.png",
        "/images/downloadgame-btn/downloadgame-down.png",
        "/images/downloadgame-btn/downloadgame-hover.png",
        "/images/downloadgame-btn/downloadgame-normal.png",
        "/images/global-download-btn/download-down.png",
        "/images/global-download-btn/download-hover.png",
        "/images/global-download-btn/download-normal.png",
        "/images/global-launch-btn/launch-down.png",
        "/images/global-launch-btn/launch-hover.png",
        "/images/global-launch-btn/launch-normal.png",
        "/images/icon/icon.ico",
        "/images/icon/icon.jpg",
        "/images/icon/reloading.png",
        "/images/launchgame-btn/launchgame-down.png",
        "/images/launchgame-btn/launchgame-hover.png",
        "/images/launchgame-btn/launchgame-normal.png",
        "/images/matrix/dummy.png",
        "/images/matrix/ke-lao-di-ya.png",
        "/images/matrix/lei-bei.png",
        "/images/matrix/lin-ye.png",
        "/images/misc/fishing-1.png",
        "/images/misc/fishing-2.png",
        "/images/misc/fishing-3.png",
        "/images/relics/dice.png",
        "/images/relics/kao-en-te.png",
        "/images/relics/kao-en-te-2.png",
        "/images/relics/dummy.png",
        "/images/weapons/a-lai-ye-shi.png",
        "/images/weapons/ba-er-meng-ke.png",
        "/images/weapons/bing-feng-zhi-shi.png",
        "/images/weapons/bu-mie-zhi-yi.png",
        "/images/weapons/chao-dian-ci-shuang-xing.png",
        "/images/weapons/chi-yan-zuo-lun.png",
        "/images/weapons/chu-dong-zhong-ji.png",
        "/images/weapons/dian-ci-ren.png",
        "/images/weapons/fou-jue-li-fang.png",
        "/images/weapons/fu-he-gong.png",
        "/images/weapons/ge-dou-dao.png",
        "/images/weapons/ge-lai-pu-ni.png",
        "/images/weapons/hei-ya-ming-lian.png",
        "/images/weapons/hong-lian-ren.png",
        "/images/weapons/huan-hai-lun-ren.png",
        "/images/weapons/ji-lei-shuang-ren.png",
        "/images/weapons/lei-ting-zhan-ji.png",
        "/images/weapons/ling-du-zhi-zhen.png",
        "/images/weapons/ling-guang.png",
        "/images/weapons/liu-quan-che-xin.png",
        "/images/weapons/mo-bi-wu-si.png",
        "/images/weapons/po-jun.png",
        "/images/weapons/po-xiao.png",
        "/images/weapons/qi-ming-xing.png",
        "/images/weapons/qiang-wei-zhi-feng.png",
        "/images/weapons/sheng-hen-quan-zhang.png",
        "/images/weapons/shuang-dong-chang-qiang.png",
        "/images/weapons/si-pa-ke.png",
        "/images/weapons/si-ye-shi-zi.png",
        "/images/weapons/v2-rong-qv-dun.png",
        "/images/weapons/wan-dao.png",
        "/images/weapons/ye-que-zhi-yu.png",
        "/images/weapons/ying-zhi.png",
        "/images/weapons/zhong-jie-zhe.png",
    };
    private static final ImageManager instance = new ImageManager();

    public static ImageManager get() {
        return instance;
    }

    private ImageManager() {
    }

    private final Map<String, Image> map = new ConcurrentHashMap<>();

    public Image load(String path) {
        try {
            return load(path, false);
        } catch (Exception ignore) {
            return null;
        }
    }

    @SuppressWarnings("RedundantThrows")
    public Image load(String path, boolean throwException) throws Exception {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        var image = map.get(path);
        if (image != null) {
            Logger.debug("using cached image: " + path);
            return image;
        }
        try {
            image = new Image(path, false);
        } catch (Exception e) {
            Logger.error("failed loading image " + path, e);
            if (throwException) {
                throw e;
            }
            return null;
        }
        map.put(path, image);
        Logger.debug("new image loaded: " + path);
        return image;
    }
}
