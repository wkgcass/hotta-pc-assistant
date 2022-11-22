package net.cassite.hottapcassistant.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.component.VPadding;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.Utils;
import net.cassite.hottapcassistant.util.Version;

import java.util.List;

public class AboutPane extends StackPane {
    private static final List<Author> authors = List.of(
        new Author("wkgcass", "https://github.com/wkgcass", "wkgcass",
            "星岛2", "vcassv")
    );

    private record Author(String name, String whereToFindMe, String biliAccount,
                          String gameServer, String gameName) {
    }

    private static String generate() {
        var sb = new StringBuilder();
        for (var a : authors) {
            sb.append(a.name).append("\n");
            sb.append("  ").append(a.whereToFindMe).append("\n");
            if (a.biliAccount != null) {
                sb.append("  ").append("bilibili: ").append(a.biliAccount).append("\n");
            }
            sb.append("  ").append(a.gameServer).append(" ").append(a.gameName).append("\n");
        }
        return sb.toString();
    }

    private final Label latestVersion = new Label() {{
        FontManager.setFont(this);
    }};
    private final Label latestReleaseTime = new Label() {{
        FontManager.setFont(this);
    }};
    private final Label lastSyncTime = new Label() {{
        FontManager.setFont(this);
    }};

    public AboutPane() {
        var vbox = new VBox();
        vbox.setPadding(new Insets(10, 0, 0, 0));
        vbox.getChildren().addAll(
            new VBox() {{
                getChildren().addAll(
                    new Label(I18n.get().version() + ": " + Version.version) {{
                        FontManager.setFont(this);
                    }},
                    new VPadding(2),
                    latestVersion,
                    new VPadding(2),
                    latestReleaseTime,
                    new VPadding(2),
                    lastSyncTime
                );
            }},
            new Separator() {{
                setPadding(new Insets(10, 0, 10, 0));
            }},
            new Label(I18n.get().about()) {{
                FontManager.setFont(this);
            }},
            new Separator() {{
                setPadding(new Insets(10, 0, 10, 0));
            }},
            new Label(I18n.get().contributor() + "\n" + generate()) {{
                FontManager.setFont(this);
            }});

        getChildren().add(vbox);

        Feed.updated.addListener((ob, old, now) -> Utils.runOnFX(this::updateFeed));
        updateFeed();
    }

    private void updateFeed() {
        var feed = Feed.get();
        if (feed.latestVersion != null) {
            latestVersion.setText(I18n.get().latestVersion() + ": " + feed.latestVersion);
        }
        if (feed.latestVersionReleaseTime != null) {
            latestReleaseTime.setText(I18n.get().latestVersionReleaseTime() + ": " + feed.latestVersionReleaseTime);
        }
        if (feed.feedTime != null) {
            lastSyncTime.setText(I18n.get().lastSyncTime() + ": " + feed.feedTime);
        }
    }
}
