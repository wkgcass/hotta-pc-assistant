package net.cassite.hottapcassistant.feed;

import javafx.beans.property.SimpleIntegerProperty;

import java.time.ZonedDateTime;

public class Feed {
    static Feed feed = new Feed();

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

    public ZonedDateTime feedTime;
}
