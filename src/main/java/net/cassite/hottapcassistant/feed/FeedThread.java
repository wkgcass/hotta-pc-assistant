package net.cassite.hottapcassistant.feed;

import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;

public class FeedThread extends Thread {
    private static final FeedThread thread = new FeedThread();
    private static final FeedParser[] parsers = new FeedParser[]{
        new Version1FeedParser(),
    };

    public static FeedThread get() {
        return thread;
    }

    private volatile boolean needToStop = false;
    private final HttpClient client;

    private FeedThread() {
        client = HttpClient.newBuilder().connectTimeout(
            Duration.ofSeconds(10)
        ).build();
    }

    public void terminate() {
        needToStop = true;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!needToStop) {
            try {
                exec();
            } catch (Throwable t) {
                Logger.error("exception thrown in feed thread", t);
            }
            try {
                //noinspection BusyWait
                Thread.sleep(10 * 60_000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void exec() throws Exception {
        var req = HttpRequest.newBuilder(
                URI.create("https://api.github.com/repos/wkgcass/hotta-pc-assistant/issues/1/comments")
            )
            .GET()
            .timeout(Duration.ofSeconds(10))
            .build();
        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        var httpBody = resp.body();
        Logger.info("calling github for issue 1 comments, retrieved http body: " + httpBody);
        var arr = JSON.parse(httpBody);
        if (!(arr instanceof JSON.Array)) {
            Logger.error("github issue 1 comments not responding json array: " + arr);
            return;
        }
        for (var i = 0; i < ((JSON.Array) arr).length(); ++i) {
            handleIssue1(i, ((JSON.Array) arr).get(i));
        }
        Feed.feed.feedTime = ZonedDateTime.now();
        Feed.alert();
    }

    private void handleIssue1(int index, JSON.Instance<?> instance) {
        if (!(instance instanceof JSON.Object)) {
            Logger.error("github issue 1 comments[" + index + "] is not json object: " + instance.stringify());
            return;
        }
        if (!((JSON.Object) instance).containsKey("body")) {
            Logger.error("github issue 1 comments[" + index + "] does not contain field 'body': " + instance.stringify());
            return;
        }
        var body = ((JSON.Object) instance).get("body");
        if (!(body instanceof JSON.String)) {
            Logger.error("github issue 1 comments[" + index + "].body is not json string: " + body.stringify());
            return;
        }
        var bodyStr = (String) body.toJavaObject();
        bodyStr = bodyStr.trim();
        var split = bodyStr.split("\n");
        if (split.length == 0) {
            Logger.error("github issue 1 comments[" + index + "].body does not contain metadata: " + body.stringify());
            return;
        }
        var line0 = split[0];
        bodyStr = bodyStr.substring(line0.length());
        if (bodyStr.endsWith("```")) {
            bodyStr = bodyStr.substring(0, bodyStr.length() - "```".length());
        }
        bodyStr = bodyStr.trim();
        if (!line0.startsWith("```")) {
            Logger.error("github issue 1 comments[" + index + "].body does not contain metadata, not starting with '```': " + body.stringify());
            return;
        }
        line0 = line0.substring("```".length());
        Metadata metadata;
        try {
            metadata = Utils.deserializeWithAllFeatures(line0, Metadata.rule);
        } catch (Exception e) {
            Logger.error("github issue 1 comments[" + index + "].body does not contain metadata, not valid json or unable to deserialize: " + line0, e);
            return;
        }
        if (metadata.version <= 0) {
            Logger.error("github issue 1 comments[" + index + "] invalid metadata version: " + metadata.version);
            return;
        }

        if (metadata.version > parsers.length) {
            Logger.warn("unable to handle github issue 1 comments[" + index + "] with version " + metadata.version);
            return;
        }

        Logger.info("get body from github issue 1 comments[" + index + "]: " + bodyStr);

        try {
            parsers[metadata.version - 1].parse(bodyStr);
        } catch (Exception e) {
            Logger.error("handling github issue 1 comments[" + index + "].body failed, version: " + metadata.version + " ,body: " + bodyStr, e);
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
}
