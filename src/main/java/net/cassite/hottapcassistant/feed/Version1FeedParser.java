package net.cassite.hottapcassistant.feed;

import io.vproxy.base.util.LogType;
import javafx.scene.image.Image;
import io.vproxy.base.util.Logger;
import vjson.CharStream;
import vjson.JSON;
import vjson.deserializer.rule.LongRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.deserializer.rule.StringRule;
import vjson.parser.ParserOptions;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class Version1FeedParser implements FeedParser {
    @Override
    public void parse(String input) {
        var entity = JSON.deserialize(CharStream.from(input), Entity.rule, ParserOptions.allFeatures());
        if (entity.program != null) {
            var program = entity.program;
            if (program.version != null) {
                var version = program.version;
                if (version.ver != null) {
                    Feed.feed.latestVersion.set(version.ver);
                    Logger.alert("feed: latestVersion updated: " + Feed.feed.latestVersion.get());
                    if (version.ts != 0) {
                        long ts = version.ts;
                        Feed.feed.latestVersionReleaseTime.set(Instant.ofEpochSecond(ts).atZone(ZoneId.systemDefault()));
                        Logger.alert("feed: latestVersionReleaseTime updated: " + Feed.feed.latestVersionReleaseTime.get());
                    }
                    if (version.lastCriticalVer != null) {
                        Feed.feed.lastCriticalVersion.set(version.lastCriticalVer);
                        Logger.alert("feed: lastCriticalVersion updated: " + Feed.feed.lastCriticalVersion.get());
                    }
                }
            }
            if (program.resource != null) {
                var resource = program.resource;
                if (resource.image != null) {
                    var image = resource.image;
                    if (image.introBg != null) {
                        var body = httpGet(image.introBg, "image.introBg");
                        if (body != null) {
                            var img = new Image(new ByteArrayInputStream(body));
                            if (img.isError()) {
                                Logger.error(LogType.CONN_ERROR, "failed to load introBg from feed: " + image.introBg);
                            } else {
                                Feed.feed.introBg.set(img);
                                Logger.alert("feed: introBg updated: " + Feed.feed.introBg.get());
                            }
                        }
                    }
                }
            }
        }
        if (entity.game != null) {
            var game = entity.game;
            if (game.download != null) {
                var download = game.download;
                if (download.pmp != null) {
                    var pmp = download.pmp;
                    if (pmp.url != null) {
                        Feed.feed.pmpDownloadUrl.set(pmp.url);
                        Logger.alert("feed: pmpDownloadUrl updated: " + Feed.feed.pmpDownloadUrl.get());
                    }
                }
            }
        }
        if (entity.config != null) {
            var config = entity.config;
            if (config.tofServer != null) {
                var tofServer = config.tofServer;
                if (tofServer.hosts != null) {
                    var body = httpGet(tofServer.hosts, "tofServer.hosts");
                    if (body != null) {
                        Feed.feed.tofServerHosts.set(new String(body));
                        Logger.alert("feed: tofServerHosts updated: line count: " + Feed.feed.tofServerHosts.get().split("\n").length);
                    }
                }
                if (tofServer.names != null) {
                    var body = httpGet(tofServer.names, "tofServer.names");
                    if (body != null) {
                        Feed.feed.tofServerNames.set(new String(body));
                        Logger.alert("feed: tofServerNames updated: line count: " + Feed.feed.tofServerNames.get().split("\n").length);
                    }
                }
            }
        }
    }

    private byte[] httpGet(String url, String resourceName) {
        var req = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .build();
        HttpResponse<byte[]> resp = null;
        try {
            resp = FeedThread.get().client.send(req, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Throwable t) {
            Logger.error(LogType.CONN_ERROR, "failed to retrieve " + resourceName + " from feed: " + url + ": " + t.getMessage());
        }
        if (resp == null) {
            return null;
        }
        if (resp.statusCode() != 200) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "failed to retrieve " + resourceName + " from feed: " + url + ", status: " + resp.statusCode());
            return null;
        }
        return resp.body();
    }

    private static class Entity {
        EntityProgram program;
        EntityGame game;
        EntityConfig config;

        static Rule<Entity> rule = new ObjectRule<>(Entity::new)
            .put("program", (o, it) -> o.program = it, EntityProgram.rule)
            .put("game", (o, it) -> o.game = it, EntityGame.rule)
            .put("config", (o, it) -> o.config = it, EntityConfig.rule);
    }

    private static class EntityProgram {
        EntityProgramVersion version;
        EntityProgramResource resource;

        static Rule<EntityProgram> rule = new ObjectRule<>(EntityProgram::new)
            .put("version", (o, it) -> o.version = it, EntityProgramVersion.rule)
            .put("resource", (o, it) -> o.resource = it, EntityProgramResource.rule);
    }

    private static class EntityProgramVersion {
        String ver;
        long ts;
        String lastCriticalVer;

        static Rule<EntityProgramVersion> rule = new ObjectRule<>(EntityProgramVersion::new)
            .put("ver", (o, it) -> o.ver = it, StringRule.get())
            .put("ts", (o, it) -> o.ts = it, LongRule.get())
            .put("lastCriticalVer", (o, it) -> o.lastCriticalVer = it, StringRule.get());
    }

    private static class EntityProgramResource {
        EntityProgramResourceImage image;

        static Rule<EntityProgramResource> rule = new ObjectRule<>(EntityProgramResource::new)
            .put("image", (o, it) -> o.image = it, EntityProgramResourceImage.rule);
    }

    private static class EntityProgramResourceImage {
        String introBg;

        static Rule<EntityProgramResourceImage> rule = new ObjectRule<>(EntityProgramResourceImage::new)
            .put("intro.bg", (o, it) -> o.introBg = it, StringRule.get());
    }

    private static class EntityGame {
        EntityGameDownload download;

        static Rule<EntityGame> rule = new ObjectRule<>(EntityGame::new)
            .put("download", (o, it) -> o.download = it, EntityGameDownload.rule);
    }

    private static class EntityGameDownload {
        EntityGameDownloadPmp pmp;

        static Rule<EntityGameDownload> rule = new ObjectRule<>(EntityGameDownload::new)
            .put("pmp", (o, it) -> o.pmp = it, EntityGameDownloadPmp.rule);
    }

    private static class EntityGameDownloadPmp {
        String url;

        static Rule<EntityGameDownloadPmp> rule = new ObjectRule<>(EntityGameDownloadPmp::new)
            .put("url", (o, it) -> o.url = it, StringRule.get());
    }

    private static class EntityConfig {
        EntityConfigTofServer tofServer;
        static Rule<EntityConfig> rule = new ObjectRule<>(EntityConfig::new)
            .put("tof-server", (o, it) -> o.tofServer = it, EntityConfigTofServer.rule);
    }

    private static class EntityConfigTofServer {
        String names;
        String hosts;
        static Rule<EntityConfigTofServer> rule = new ObjectRule<>(EntityConfigTofServer::new)
            .put("names", (o, it) -> o.names = it, StringRule.get())
            .put("hosts", (o, it) -> o.hosts = it, StringRule.get());
    }
}
