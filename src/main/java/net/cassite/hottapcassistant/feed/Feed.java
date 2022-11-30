package net.cassite.hottapcassistant.feed;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.time.ZonedDateTime;

public class Feed {
    static final Feed feed = new Feed();

    public static Feed get() {
        return feed;
    }

    Feed() {
    }

    public static final SimpleIntegerProperty updated = new SimpleIntegerProperty(0);

    public static void alert() {
        updated.set(updated.get() + 1);
    }

    public String latestVersion;
    public ZonedDateTime latestVersionReleaseTime;
    public String pmpDownloadUrl;
    public Image introBg;

    public ZonedDateTime feedTime;
}
