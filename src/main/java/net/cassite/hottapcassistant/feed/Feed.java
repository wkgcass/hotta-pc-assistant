package net.cassite.hottapcassistant.feed;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.time.ZonedDateTime;

public class Feed {
    static final Feed feed = new Feed();

    public static Feed get() {
        return feed;
    }

    Feed() {
    }

    public final SimpleObjectProperty<String> latestVersion = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<ZonedDateTime> latestVersionReleaseTime = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<String> lastCriticalVersion = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<String> pmpDownloadUrl = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<String> tofMiniLoaderUrl = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Image> introBg = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<String> tofServerNames = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<String> tofServerHosts = new SimpleObjectProperty<>();

    public final SimpleObjectProperty<Boolean> lockMacroPane = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Boolean> lockFishingPane = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Boolean> lockMultiPane = new SimpleObjectProperty<>();

    public final SimpleObjectProperty<ZonedDateTime> feedTime = new SimpleObjectProperty<>();
}
