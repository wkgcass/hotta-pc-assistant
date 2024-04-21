package net.cassite.hottapcassistant.feed;

import io.vproxy.base.util.LogType;
import io.vproxy.commons.util.IOUtils;
import io.vproxy.base.util.Logger;
import net.cassite.hottapcassistant.util.Utils;
import vjson.CharStream;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.parser.ParserOptions;
import vjson.simple.SimpleString;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedThread extends Thread {
    private static final FeedThread thread = new FeedThread();
    private static final FeedParser[] parsers = new FeedParser[]{
        new Version1FeedParser(),
    };

    public static FeedThread get() {
        return thread;
    }

    private volatile boolean needToStop = false;
    final HttpClient client;

    private FeedThread() {
        client = HttpClient.newBuilder()
            .connectTimeout(
                Duration.ofSeconds(10)
            ).build();
    }

    public void terminate() {
        needToStop = true;
        thread.interrupt();
    }

    @Override
    public void run() {
        checkLock();
        if (needToStop) {
            return;
        }
        try {
            loadCache();
        } catch (Throwable t) {
            Logger.error(LogType.FILE_ERROR, "failed loading cache", t);
        }
        while (!needToStop) {
            checkLock();
            try {
                exec();
            } catch (Throwable t) {
                Logger.error(LogType.USER_HANDLE_FAIL, "exception thrown in feed thread", t);
            }
            if (needToStop) {
                break;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(2 * 60 * 60_000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void checkLock() {
        Feed.feed.lockMacroPane.set(Utils.checkLock("macro", false, false));
        Feed.feed.lockFishingPane.set(Utils.checkLock("fishing", false, false));
        Feed.feed.lockMultiPane.set(Utils.checkLock("multi", false, false));
    }

    private void loadCache() throws IOException {
        var tmpdir = System.getProperty("java.io.tmpdir");
        var path = Path.of(tmpdir, "hotta-pc-assistant", "req-1");
        var file = path.toFile();
        if (!file.exists()) {
            Logger.warn(LogType.INVALID_EXTERNAL_DATA, file.getAbsolutePath() + " does not exist, skip loading from cache");
            return;
        }
        var fileAttrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (!fileAttrs.isRegularFile()) {
            Logger.warn(LogType.INVALID_EXTERNAL_DATA, file.getAbsolutePath() + " is not a valid file, skip loading from cache");
            try {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            } catch (Throwable ignore) {
            }
            return;
        }
        var lastModified = fileAttrs.lastModifiedTime().toMillis();
        var httpBody = Files.readString(path);
        Logger.alert("using cached github issue 1 comments: " + httpBody);
        handleIssue1(httpBody);
        Feed.feed.feedTime.set(Instant.ofEpochMilli(lastModified).atZone(ZoneId.systemDefault()));
    }

    private void exec() {
        var req = HttpRequest.newBuilder(
                URI.create("https://api.github.com/repos/wkgcass/hotta-pc-assistant/issues/1/comments?page=1&per_page=100")
            )
            .GET()
            .timeout(Duration.ofSeconds(10))
            .build();
        HttpResponse<String> resp;
        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            Logger.warn(LogType.CONN_ERROR, "unable to retrieve github issue 1 comments, skipping");
            return;
        }
        var httpBody = resp.body();
        if (resp.statusCode() != 200) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "calling github for issue 1 comments failed, status: " + resp.statusCode() + ", body: " + httpBody);
            return;
        }
        Logger.alert("calling github for issue 1 comments, retrieved http body: " + httpBody);
        try {
            IOUtils.writeFileWithBackup(Path.of(System.getProperty("java.io.tmpdir"), "hotta-pc-assistant", "req-1").toString(), httpBody);
        } catch (Throwable t) {
            Logger.error(LogType.FILE_ERROR, "failed storing github issue 1 comments response to file", t);
            // fallthrough
        }
        handleIssue1(httpBody);
    }

    private void handleIssue1(String httpBody) {
        var arr = JSON.deserialize(CharStream.from(httpBody),
            new ArrayRule<List<GithubIssueComment>, GithubIssueComment>(
                ArrayList::new, List::add, GithubIssueComment.rule
            ));
        for (var i = 0; i < arr.size(); ++i) {
            handleIssue1(i, arr.get(i));
        }
        Feed.feed.feedTime.set(ZonedDateTime.now());
    }

    private void handleIssue1(int index, GithubIssueComment comment) {
        if (comment == null) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "] is null");
            return;
        }
        if (comment.user == null) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "] does not contain field 'user': " + comment);
            return;
        }
        var userId = comment.user.id;
        if (userId != 10825968) {
            assert Logger.lowLevelDebug("retrieved comment not produced by trusted user, the retrieved userId is " + userId + ", skipping...");
            return;
        }
        if (comment.body == null) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "] does not contain field 'body': " + comment);
            return;
        }
        var bodyStr = comment.body;
        bodyStr = bodyStr.trim();
        var split = bodyStr.split("\n");
        if (split.length == 0) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "].body does not contain metadata: " + bodyStr);
            return;
        }
        var line0 = split[0];
        bodyStr = bodyStr.substring(line0.length());
        if (bodyStr.endsWith("```")) {
            bodyStr = bodyStr.substring(0, bodyStr.length() - "```".length());
        }
        bodyStr = bodyStr.trim();
        if (!line0.startsWith("```")) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "].body does not contain metadata, not starting with '```': " + bodyStr);
            return;
        }
        line0 = line0.substring("```".length());
        Metadata metadata;
        try {
            metadata = JSON.deserialize(CharStream.from(line0), Metadata.rule, ParserOptions.allFeatures());
        } catch (Exception e) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "].body does not contain metadata, not valid json or unable to deserialize: " + line0, e);
            return;
        }
        if (metadata.version <= 0) {
            Logger.error(LogType.INVALID_EXTERNAL_DATA, "github issue 1 comments[" + index + "] invalid metadata version: " + metadata.version);
            return;
        }

        if (metadata.version > parsers.length) {
            Logger.warn(LogType.INVALID_EXTERNAL_DATA, "unable to handle github issue 1 comments[" + index + "] with version " + metadata.version);
            return;
        }

        Logger.alert("get body from github issue 1 comments[" + index + "]: " + new SimpleString(bodyStr).stringify());

        try {
            parsers[metadata.version - 1].parse(bodyStr);
        } catch (Exception e) {
            Logger.error(LogType.SYS_ERROR, "handling github issue 1 comments[" + index + "].body failed, version: " + metadata.version + " ,body: " + bodyStr, e);
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    private static class GithubIssueComment {
        GithubUserRef user;
        String body;

        static final Rule<GithubIssueComment> rule = new ObjectRule<>(GithubIssueComment::new)
            .put("user", (o, it) -> o.user = it, GithubUserRef.rule)
            .put("body", (o, it) -> o.body = it, StringRule.get());

        @Override
        public String toString() {
            return "GithubIssueComment{" +
                   "user=" + user +
                   ", body='" + body + '\'' +
                   '}';
        }
    }

    private static class GithubUserRef {
        int id;

        static final Rule<GithubUserRef> rule = new ObjectRule<>(GithubUserRef::new)
            .put("id", (o, it) -> o.id = it, IntRule.get());

        @Override
        public String toString() {
            return "GithubUserRef{" +
                   "id=" + id +
                   '}';
        }
    }
}
