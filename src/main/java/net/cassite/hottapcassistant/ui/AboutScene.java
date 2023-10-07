package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.feed.Feed;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.Consts;
import net.cassite.hottapcassistant.util.Version;

import java.util.List;

public class AboutScene extends AbstractMainScene {
    private static final List<Author> authors = List.of(
        new Author("wkgcass", "https://github.com/wkgcass", "wkgcass",
            "星岛2", "vcassv",
            "Maintainer")
    );

    private record Author(String name, String whereToFindMe, String biliAccount,
                          String gameServer, String gameName,
                          String contribution) {
    }

    private static String generate() {
        var sb = new StringBuilder();
        for (var a : authors) {
            sb.append(a.name).append("\n");
            sb.append("  ").append(a.whereToFindMe).append("\n");
            if (a.biliAccount != null) {
                sb.append("  ").append(I18n.get().authorBilibili()).append(": ").append(a.biliAccount).append("\n");
            }
            sb.append("  ").append(I18n.get().authorGameAccount()).append(": ").append(a.gameServer).append(" ").append(a.gameName).append("\n");
            sb.append("  ").append(I18n.get().authorContribution()).append(": ").append(a.contribution).append("\n");
        }
        return sb.toString();
    }

    private final ThemeLabel latestVersion = new ThemeLabel() {{
        FontManager.get().setFont(Consts.NotoFont, this);
    }};
    private final ThemeLabel latestReleaseTime = new ThemeLabel() {{
        FontManager.get().setFont(Consts.NotoFont, this);
    }};
    private final ThemeLabel lastSyncTime = new ThemeLabel() {{
        FontManager.get().setFont(Consts.NotoFont, this);
    }};

    public AboutScene() {
        enableAutoContentWidth();
        var vbox = new VBox();
        FXUtils.observeWidthCenter(getContentPane(), vbox);

        vbox.setPadding(new Insets(50, 0, 50, 0));
        vbox.getChildren().addAll(
            new VBox() {{
                String versionStr = Version.version;
                //noinspection ConstantConditions
                if (versionStr.endsWith("-dev")) {
                    versionStr += "    ";
                    versionStr += Version.devVersion;
                }

                getChildren().addAll(
                    new ThemeLabel(I18n.get().version() + ": " + versionStr) {{
                        FontManager.get().setFont(Consts.NotoFont, this);
                    }},
                    new VPadding(2),
                    latestVersion,
                    new VPadding(2),
                    latestReleaseTime,
                    new VPadding(2),
                    lastSyncTime
                );
            }},
            new VPadding(20),
            new ThemeLabel(I18n.get().about()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
            }},
            new VPadding(20),
            new ThemeLabel(I18n.get().contributor() + "\n" + generate()) {{
                FontManager.get().setFont(Consts.NotoFont, this);
            }});

        getContentPane().getChildren().add(vbox);

        Runnable updateFeedRunnable = () -> FXUtils.runOnFX(this::updateFeed);
        Feed.get().latestVersion.addListener((ob) -> updateFeedRunnable.run());
        Feed.get().latestVersionReleaseTime.addListener((ob) -> updateFeedRunnable.run());
        Feed.get().feedTime.addListener((ob) -> updateFeedRunnable.run());
        updateFeed();
    }

    @Override
    public String title() {
        return I18n.get().toolNameAbout();
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
