package net.cassite.hottapcassistant.multi;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.TrustOptions;
import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.util.MiscUtils;
import javafx.beans.property.BooleanProperty;
import net.cassite.hottapcassistant.util.GlobalValues;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

public class HottaLauncherProxyServer {
    private static final String reqSplitTag = "\n--------------------------------------";
    private static final String respEndLogTag = "\n========================================";

    private final HttpServer server;
    private final HttpClient client;

    private final String advBranch;
    private final String onlineBranch;
    private final String version;
    private final String subVersion;
    private final String clientVersion;

    private final BooleanProperty isHandlingAdv;

    public HottaLauncherProxyServer(String advBranch, String onlineBranch, String version, String subVersion, String clientVersion,
                                    BooleanProperty isHandlingAdv) {
        this.advBranch = advBranch;
        this.onlineBranch = onlineBranch;
        this.version = version;
        this.subVersion = subVersion;
        this.clientVersion = clientVersion;
        this.isHandlingAdv = isHandlingAdv;
        server = GlobalValues.vertx.createHttpServer(new HttpServerOptions()
            .setSsl(true)
            .setPemKeyCertOptions(new PemKeyCertOptions()
                .setCertValue(Buffer.buffer(Certs.HOTTA_CERT))
                .setKeyValue(Buffer.buffer(Certs.HOTTA_KEY))
            )
            .setReuseAddress(true)
        );
        server.requestHandler(this::handleRequest);
        client = GlobalValues.vertx.createHttpClient(new HttpClientOptions()
            .setTrustOptions(TrustOptions.wrap(new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }))
            .setVerifyHost(false));
    }

    public void start() throws Exception {
        server.listen(443).toCompletionStage().toCompletableFuture().get();
    }

    public void destroy() {
        server.close();
        client.close();
    }

    @SuppressWarnings("DuplicatedCode")
    private void handleRequest(HttpServerRequest req) {
        req.body().map(buffer -> {
            var method = req.method();
            var uri = req.uri();
            var headers = req.headers();
            var body = buffer.toString();
            var reqId = UUID.randomUUID().toString();
            String msg = "reqId: " + reqId + "\nmethod: " + method + "\nuri: " + uri + "\n" + headers + "\nbody: " + body;
            Logger.access(msg + reqSplitTag);

            if (method == HttpMethod.GET && uri.startsWith("/clientRes/AdvLaunchNull/Version/Windows/config.xml")) {
                nullConfigXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + advBranch + "/Version/Windows/config.xml")) {
                configXml(reqId, req);
            } else if (isHandlingAdv.get() && method == HttpMethod.GET && uri.startsWith("/clientRes/" + onlineBranch + "/Version/Windows/config.xml")) {
                configXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + advBranch + "/Version/Windows/version/" + subVersion + "/ResList.xml")) {
                resListXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + advBranch + "/Version/Windows/version/" + subVersion + "/lastdiff.xml")) {
                lastdiffXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/htydalphahd/client/Version.txt")) {
                versionTxt(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/pmp/update/200105/") && uri.endsWith("AllFiles.xml")) {
                allFilesXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/pmp/update/200105/Version.ini")) {
                versionIni(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/AdvLaunchNull/gameinfo.xml")) {
                isHandlingAdv.set(true);
                gameInfoXml(reqId, req);
            } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + onlineBranch + "/gameinfo.xml")) {
                isHandlingAdv.set(false);
                proxy(client, reqId, req, method, uri, headers, body);
            } else {
                proxy(client, reqId, req, method, uri, headers, body);
            }

            return null;
        });
    }

    private void nullConfigXml(String reqId, HttpServerRequest req) {
        var resConfig = "{" +
            /**/"\"appVersion\":\"" + version + "\"," +
            /**/"\"minVersion\":\"" + version + "\"," +
            /**/"\"gameResUrl\":[" +
            /*----*/"\"https://htcdn1.wmupd.com/clientRes\"," +
            /*----*/"\"https://htcdn2.wmupd.com/clientRes\"" +
            /**/"]," +
            /**/"\"gameGetServerListUrl\":[" +
            /*----*/"\"https://htcdn1.wmupd.com/\"," +
            /*----*/"\"https://htcdn2.wmupd.com/\"" +
            /**/"]," +
            /**/"\"branchName\":\"" + advBranch + "\"," +
            /**/"\"gameUpdateAssetUrl\":[" +
            /*----*/"\"https://htcdn1.wmupd.com/clientRes\"," +
            /*----*/"\"https://htcdn2.wmupd.com/clientRes\"" +
            /**/"]}";
        var body = "<?xml version=\"1.0\" ?>\n" +
                   "<config>\n" +
                   "        <AppVersion>1.0</AppVersion>\n" +
                   "        <ResVersion>" + subVersion + "</ResVersion>\n" +
                   "        <UpdateResVersion>1.0</UpdateResVersion>\n" +
                   "        <Section>" + version + "</Section>\n" +
                   "        <PreReleaseBranch>" + advBranch + "</PreReleaseBranch>\n" +
                   "        <PreReleaseVersion>" + subVersion + "</PreReleaseVersion>\n" +
                   "        <ResConfig>" + Base64.getEncoder().encodeToString(resConfig.getBytes()) + "</ResConfig>\n" +
                   "        <BaseVerson appVersion=\"1.0\"/>\n" +
                   "        <Extra>\n" +
                   "                <speed>50</speed>\n" +
                   "                <maxThreadCnt>5</maxThreadCnt>\n" +
                   "                <minThreadCnt>1</minThreadCnt>\n" +
                   "                <tagTaskThreadCnt>2</tagTaskThreadCnt>\n" +
                   "        </Extra>\n" +
                   "</config>\n";
        Logger.access("custom null config.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void configXml(String reqId, HttpServerRequest req) {
        var body = MultiHottaInstanceFlow.buildConfigXml(version, subVersion);
        Logger.access("custom config.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void resListXml(String reqId, HttpServerRequest req) {
        var body = MultiHottaInstanceFlow.buildResListXml(subVersion);
        Logger.access("custom resList.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void lastdiffXml(String reqId, HttpServerRequest req) {
        var body = """
            <?xml version="1.0" ?>
            <PatchList/>
            """;
        Logger.access("custom lastdiff.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void versionTxt(String reqId, HttpServerRequest req) {
        var version = clientVersion;
        Logger.access("custom version.txt response: " + version + "\nreqId: " + reqId + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(version);
    }

    private void allFilesXml(String reqId, HttpServerRequest req) {
        var now = ZonedDateTime.now();
        var timeStr = MiscUtils.YYYYMMddHHiissDateTimeFormatter.format(now);
        var body = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>\n" +
                   "<All_Files>\n" +
                   "    <BuildInfo Time=\"" + timeStr + "\"/>\n" +
                   "    <ProductVersion Version=\"1.0.8.0109\"/>\n" +
                   "    <Url BaseUrl=\"https://pmpcdn1.wmupd.com/pmp/update/200105\"/>\n" +
                   "    <Log></Log>\n" +
                   "</All_Files>\n";
        Logger.access("custom AllFiles.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void versionIni(String reqId, HttpServerRequest req) {
        var body = """
            [VERSION]
            Version=1.0.8.0109
            Build=29800
            FileListURL=https://pmpcdn1.wmupd.com/pmp/update/200105/1.0.8.0109/AllFiles.xml
            [UPDATEINFO]
            """;
        body = body.trim() + "\n\n";
        Logger.access("custom Version.ini response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void gameInfoXml(String reqId, HttpServerRequest req) {
        var body = """
            <?xml version="1.0" ?>
            <GameInfo>
                    <gamename>AdvLaunchNull</gamename>
                    <multiplatform>True</multiplatform>
            </GameInfo>
            """;
        body = body.trim() + "\n\n";
        Logger.access("custom gameinfo.xml response\nreqId: " + reqId + "\n" + body + respEndLogTag);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    public static void proxy(HttpClient client, String reqId, HttpServerRequest req, HttpMethod method, String uri, MultiMap headers, String body) {
        var host = headers.get("host");
        assert Logger.lowLevelDebug("reqId: " + reqId + ", host header is " + host);
        if (host == null) {
            req.response().setStatusCode(404);
            req.response().end("not found\r\n");
            return;
        }
        client.request(new RequestOptions()
                .setSsl(true)
                .setMethod(method)
                .setHost(host)
                .setPort(443)
                .setURI(uri)
                .setHeaders(headers)
            ).flatMap(creq -> {
                assert Logger.lowLevelDebug("reqId: " + reqId + ", sending new request");
                if (body == null || body.isEmpty()) {
                    return creq.send();
                } else {
                    return creq.send(body);
                }
            })
            .map(resp -> {
                Logger.access("proxy the response: reqId: " + reqId + "\nresp code: " + resp.statusCode() + "\n" + resp.headers() + respEndLogTag);
                req.response().setStatusCode(resp.statusCode());
                for (var entry : resp.headers()) {
                    req.response().putHeader(entry.getKey(), entry.getValue());
                }
                resp.pipe().to(req.response());
                return null;
            })
            .recover(t -> {
                Logger.error(LogType.CONN_ERROR, "reqId: " + reqId + ", failed to send request", t);
                return null;
            });
    }
}
