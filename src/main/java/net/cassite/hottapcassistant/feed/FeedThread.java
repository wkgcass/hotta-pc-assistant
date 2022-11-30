package net.cassite.hottapcassistant.feed;

import net.cassite.hottapcassistant.util.Logger;
import vjson.CharStream;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.parser.ParserOptions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
        var arr = JSON.deserialize(CharStream.from(httpBody),
            new ArrayRule<List<GithubIssueComment>, GithubIssueComment>(
                ArrayList::new, List::add, GithubIssueComment.rule
            ));
        for (var i = 0; i < arr.size(); ++i) {
            handleIssue1(i, arr.get(i));
        }
        Feed.feed.feedTime = ZonedDateTime.now();
        Feed.alert();
    }

    private void handleIssue1(int index, GithubIssueComment comment) {
        if (comment == null) {
            Logger.error("github issue 1 comments[" + index + "] is null");
            return;
        }
        if (comment.user == null) {
            Logger.error("github issue 1 comments[" + index + "] does not contain field 'user': " + comment);
            return;
        }
        var userId = comment.user.id;
        if (userId != 10825968) {
            Logger.debug("retrieved comment not produced by trusted user, the retrieved userId is " + userId + ", skipping...");
            return;
        }
        if (comment.body == null) {
            Logger.error("github issue 1 comments[" + index + "] does not contain field 'body': " + comment);
            return;
        }
        var bodyStr = comment.body;
        bodyStr = bodyStr.trim();
        var split = bodyStr.split("\n");
        if (split.length == 0) {
            Logger.error("github issue 1 comments[" + index + "].body does not contain metadata: " + bodyStr);
            return;
        }
        var line0 = split[0];
        bodyStr = bodyStr.substring(line0.length());
        if (bodyStr.endsWith("```")) {
            bodyStr = bodyStr.substring(0, bodyStr.length() - "```".length());
        }
        bodyStr = bodyStr.trim();
        if (!line0.startsWith("```")) {
            Logger.error("github issue 1 comments[" + index + "].body does not contain metadata, not starting with '```': " + bodyStr);
            return;
        }
        line0 = line0.substring("```".length());
        Metadata metadata;
        try {
            metadata = JSON.deserialize(CharStream.from(line0), Metadata.rule, ParserOptions.allFeatures());
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
