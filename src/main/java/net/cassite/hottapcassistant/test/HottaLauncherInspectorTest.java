package net.cassite.hottapcassistant.test;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.http.*;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.TrustOptions;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class HottaLauncherInspectorTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("started");
        var isDebug = System.getProperty("IsDebug", "false").equals("true");

        //noinspection CodeBlock2Expr
        Function<String, Handler<HttpServerRequest>> httpHandlerProvider = name -> req -> {
            req.body().map(buffer -> {
                var method = req.method();
                var uri = req.uri();
                var headers = req.headers();
                var body = buffer.toString();
                String msg = "from: " + name + "\nmethod: " + method + "\nuri: " + uri + "\n" + headers + "\nbody: " + body;
                System.out.println(msg);

                String branch = "AdvLaunch24";
                String version = "2.4.2";
                if (method == HttpMethod.GET && uri.startsWith("/clientRes/AdvLaunchNull/Version/Windows/config.xml")) {
                    nullConfigXml(req);
                } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + branch + "/Version/Windows/config.xml")) { // requires htcdn1 or htcdn2 .wmupd.com
                    configXml(req);
                } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + branch + "/Version/Windows/version/" + version + "/ResList.xml")) {
                    resListXml(req);
                } else if (method == HttpMethod.GET && uri.startsWith("/clientRes/" + branch + "/Version/Windows/version/" + version + "/lastdiff.xml")) {
                    lastdiffXml(req);
                } else if (method == HttpMethod.GET && uri.startsWith("/htydalphahd/client/Version.txt")) { // requires htydhd.wmupd.com
                    versionTxt(req);
                } /*else if (method == HttpMethod.GET && uri.startsWith("/pmp/update/") && uri.endsWith("AllFiles.xml")) { // requires pmpcdn1.wmupd.com
                    allFilesXml(req);
                } */ else {
                    proxy(req, method, uri, headers, body);
                }

                return null;
            });
        };

        var vertx = Vertx.vertx(new VertxOptions()
            .setAddressResolverOptions(new AddressResolverOptions()
                .setHostsValue(Buffer.buffer(""))
                .setServers(List.of("8.8.8.8"))));
        System.out.println("vertx instance created");
        client = vertx.createHttpClient(new HttpClientOptions()
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
        var server443 = vertx.createHttpServer(new HttpServerOptions()
            .setSsl(true)
            .setPemKeyCertOptions(new PemKeyCertOptions()
                .setCertValue(Buffer.buffer("""
                    -----BEGIN CERTIFICATE-----
                    MIID0jCCArqgAwIBAgIJAIvTzI2C9kiaMA0GCSqGSIb3DQEBCwUAMGkxCzAJBgNV
                    BAYTAkNOMREwDwYDVQQIDAhaaGVqaWFuZzERMA8GA1UEBwwISGFuZ3pob3UxDzAN
                    BgNVBAoMBnZwcm94eTEPMA0GA1UECwwGdnByb3h5MRIwEAYDVQQDDAl2cHJveHkt
                    Y2EwHhcNMjMwMTExMTgzNTM4WhcNMjQwMTExMTgzNTM4WjBkMQswCQYDVQQGEwJD
                    TjEQMA4GA1UECAwHSmlhbmdTdTEPMA0GA1UEBwwGU3VaaG91MQ4wDAYDVQQKDAVI
                    b3R0YTEOMAwGA1UECwwFSG90dGExEjAQBgNVBAMMCXdtdXBkLmNvbTCCASIwDQYJ
                    KoZIhvcNAQEBBQADggEPADCCAQoCggEBAMjPJdmYQbQlhsFgiztzjscQ7yYKFVCv
                    1BYu2NlThkl8zeJKIvBnmk3cIaHevGNKPD/2W/h6oT9thgJLj5ULlcbObcFmvecv
                    jGOvFxBawk2Bfn+FunMaA+T4LGqStV40K5KYunMfSlroyU8YkBPu3GgA07S2KAsf
                    Z85ojJU87lbnUH4W15Anc/17LnbGPWA/d/zp40XU6zFioDM5rG2XGv1ZrVfCXUW2
                    31nnACnhx5Ix3OZb5J/s03Sv2o+Ed9uDlZwIQnmtoi7N6U15khsWIynIqM8tAmkI
                    29oE2HrRfWD2AagbR6OjPsIXKCuAvM+1JHlPX6nzETA1wUIgMPCxsWcCAwEAAaOB
                    gTB/MAkGA1UdEwQCMAAwCwYDVR0PBAQDAgXgMGUGA1UdEQReMFyCCXdtdXBkLmNv
                    bYILKi53bXVwZC5jb22CGnBncHN0YXRpYy5nYW1lcy53YW5tZWkuY29tgg1odC53
                    YW5tZWkuY29tghdzdGF0aWMuZ2FtZXMud2FubWVpLmNvbTANBgkqhkiG9w0BAQsF
                    AAOCAQEAsB5ksz3zrwZBsplv1zW1u8SIwD41xEaHbAt7HxZ6388Jn30lKRdkJDY2
                    SD0DtYO4KjdOteykn9oH0bxvadQ47Z79XJLsz0Dp1OOog5grxOZV/Zfy9SjahcT7
                    bYIvoIZMomISg3GnuZdfn3br7Edb8cYYxnPXEokQsEJd2egyMuchSx4MymlDDkXc
                    KMpsL1T99cOzMiAB2MEvx553Qj2YlD1Ufm4z3rSpc+5R5gEUhmdT7C6PCNsi2ZUs
                    bt2+2H9Wxh6L1nwsz2NY0IgKs9SUFVSUIYW2r5S1O9Zc9orIIkSZ6uMHSDmY56B+
                    s8OZ06rxywosZUpxvftf44/0YrsspA==
                    -----END CERTIFICATE-----
                    """))
                .setKeyValue(Buffer.buffer("""
                    -----BEGIN PRIVATE KEY-----
                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDIzyXZmEG0JYbB
                    YIs7c47HEO8mChVQr9QWLtjZU4ZJfM3iSiLwZ5pN3CGh3rxjSjw/9lv4eqE/bYYC
                    S4+VC5XGzm3BZr3nL4xjrxcQWsJNgX5/hbpzGgPk+CxqkrVeNCuSmLpzH0pa6MlP
                    GJAT7txoANO0tigLH2fOaIyVPO5W51B+FteQJ3P9ey52xj1gP3f86eNF1OsxYqAz
                    Oaxtlxr9Wa1Xwl1Ftt9Z5wAp4ceSMdzmW+Sf7NN0r9qPhHfbg5WcCEJ5raIuzelN
                    eZIbFiMpyKjPLQJpCNvaBNh60X1g9gGoG0ejoz7CFygrgLzPtSR5T1+p8xEwNcFC
                    IDDwsbFnAgMBAAECggEADt942DL0utkEsBHsfgzDCG9ypwicJ4u+U55IpWAQVCUQ
                    QrBnAhNKVHX3b7vFjD0VVU9z5GWAx+fewxDBo7Jl94GWk3p+mj7lUQTI2oc+WOs8
                    aUmIU7obi7vt3j7bfAy1JXh2Zsxsf2s6bcdN9Iv+ex2EkJ/lO12qHJmVv//xTzJU
                    6nFaoy9iq/lsdYUKjvzffxZJN9qARVe+4X9pIriyy8ZSdbmTlRJHJ1Wz/D3wqEdL
                    7GB6MSC6Zl+C1IunMB9Y2TaMf+YpJ7Dn84r+onoX2PoIVkHvSXf3OxR5xm1TskmM
                    B2Rwe5qPXgmOGpZzWmc3kxv7bo5eKK9Rqhk4wrJQUQKBgQD/fBnUAibvnSTIr6Dv
                    jEuK4v6bkeGyE317GwC6iRVfWb5UzEAfqQsmU3D6qNzclkHR769GodmxJ1qXDQoe
                    tznjUJWFcDsKktgdOG0FlAOino/AIf5u/7dkRFS+4sg4MH17WgHxQyjgbKJMO5H7
                    Bm0vfuaTG5GlXiyDUJbKqXzCnQKBgQDJNtHQylXbpF34ImmLo0BEptoSHJnf/pEQ
                    xuHI9Zpw1CyUWYJh9+sauFxR+tpBrVuELWW0YARkRkVF08cL1jRWmmEIbnZwe+QY
                    bRMvT6gpQWSCInYdhCZJ9lK0fxW13MJzDuRJnIvutFcANv83CPYACU4whdjkbx+V
                    1s20jHNS0wKBgQDsNiBnLSo0plTpG8CvWkZJ/f+rdGB41Z5RoLC2xqG1C/N20XWE
                    EDYX1FQD3PZ/GIl4gaPLqT7tlNYkDFFL/toNumbT/eUIrKYVtiLOYoBN7U45NPTa
                    SaGUmbzE1EaL9QE/eAONvitf+/C+jOE2d0WiATFL19R8UarUKkGQ1M1d3QKBgQCO
                    Xe1Cj4cEK14ZjS+kj1OPv4aYPghZxmKZvj5O1Af8QQNxy4tHCgUdGMipcJYw1hnk
                    gcJuR2mUEWw+JcO2Ck9p3z08m/vNRrty1Oo4FWJkHlKhCQUNiU0WE+UctLaFwLcM
                    dcLsmokf0hWkn0UgiYSVLgBWsL1vk3iDqkJNtvp55QKBgG06RdBm9Ul6oN0CcnKd
                    ZaPhcdNQ9lbvp6cSvzyFuLocdmc+GDqE4x5hQVnA7wzCdemEOJGEWSNS1v2cn4nF
                    120SCiQkwUMKw6wppALZoU4R6kfwbA+v9oNlxxeOlL/0cxMlrYrYzWIbyL0pIUEj
                    7UXHmE/pCVXyUZOT/iRiuuKY
                    -----END PRIVATE KEY-----
                    """))
            )
        );
        server443.requestHandler(httpHandlerProvider.apply("443"));
        if (isDebug) {
            server443.listen(4443).map(v -> {
                System.out.println("listening 4443");
                return null;
            });
        } else {
            server443.listen(443).map(v -> {
                System.out.println("listening 443");
                return null;
            });
        }

        var server80 = vertx.createHttpServer();
        server80.requestHandler(httpHandlerProvider.apply("80"));
        if (isDebug) {
            server80.listen(8080).map(v -> {
                System.out.println("listening 8080");
                return null;
            });
        } else {
            server80.listen(80).map(v -> {
                System.out.println("listening 80");
                return null;
            });
        }
    }

    private void nullConfigXml(HttpServerRequest req) {
        var body = """
            <?xml version="1.0" ?>
            <config>
                    <AppVersion>1.0</AppVersion>
                    <ResVersion>2.4.2</ResVersion>
                    <UpdateResVersion>1.0</UpdateResVersion>
                    <Section>2.4</Section>
                    <PreReleaseBranch>AdvLaunch24</PreReleaseBranch>
                    <PreReleaseVersion>2.4.2</PreReleaseVersion>
                    <ResConfig>eyJhcHBWZXJzaW9uIjoiMi40IiwibWluVmVyc2lvbiI6IjIuNCIsImdhbWVSZXNVcmwiOlsiaHR0cHM6Ly9odGNkbjEud211cGQuY29tL2NsaWVudFJlcyIsImh0dHBzOi8vaHRjZG4yLndtdXBkLmNvbS9jbGllbnRSZXMiXSwiZ2FtZUdldFNlcnZlckxpc3RVcmwiOlsiaHR0cHM6Ly9odGNkbjEud211cGQuY29tLyIsImh0dHBzOi8vaHRjZG4yLndtdXBkLmNvbS8iXSwiYnJhbmNoTmFtZSI6IkFkdkxhdW5jaDI0IiwiZ2FtZVVwZGF0ZUFzc2V0VXJsIjpbImh0dHBzOi8vaHRjZG4xLndtdXBkLmNvbS9jbGllbnRSZXMiLCJodHRwczovL2h0Y2RuMi53bXVwZC5jb20vY2xpZW50UmVzIl19</ResConfig>
                    <BaseVerson appVersion="1.0"/>
                    <Extra>
                            <speed>50</speed>
                            <maxThreadCnt>5</maxThreadCnt>
                            <minThreadCnt>1</minThreadCnt>
                            <tagTaskThreadCnt>2</tagTaskThreadCnt>
                    </Extra>
            </config>
            """;
        System.out.println("custom null config.xml response\n" + body);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void configXml(HttpServerRequest req) {
        var body = """
            <?xml version="1.0" ?>
            <config>
                    <AppVersion>2.4</AppVersion>
                    <ResVersion>2.4.2</ResVersion>
                    <UpdateResVersion>2.4</UpdateResVersion>
                    <Section>2.4</Section>
                    <BaseVerson appVersion="2.4"/>
                    <Extra>
                            <speed>50</speed>
                            <maxThreadCnt>5</maxThreadCnt>
                            <minThreadCnt>1</minThreadCnt>
                            <tagTaskThreadCnt>2</tagTaskThreadCnt>
                    </Extra>
            </config>
            """;
        System.out.println("custom config.xml response\n" + body);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void resListXml(HttpServerRequest req) {
        /*
        var body = """
            <?xml version="1.0" ?>
            <ResList version="2.4.2" tag="">
                    <Res filename="Hotta/Content/Paks/pakchunk0-WindowsNoEditor.pak" filesize="5107952619" md5="f481d1186f184d4ffcd42c157e885861" blockSize="10">
                            <Block index="0" start="0" size="536870912" md5="1a45f5e4782927501e4c21f1a9d64ecc"/>
                            <Block index="1" start="536870912" size="536870912" md5="93bfd4c1b04470654fd25462635c5e2f"/>
                            <Block index="2" start="1073741824" size="536870912" md5="02e8b3bc830dcee91be3d3c92f19c406"/>
                            <Block index="3" start="1610612736" size="536870912" md5="d0586fb72fb453272e7a2655308b5d7e"/>
                            <Block index="4" start="2147483648" size="536870912" md5="1215ea5d5b53254e1b59a0efbec43b24"/>
                            <Block index="5" start="2684354560" size="536870912" md5="fa9253e0b29163504607c644b8b73570"/>
                            <Block index="6" start="3221225472" size="536870912" md5="7fb55aade6d0aa87475adfc3928d2209"/>
                            <Block index="7" start="3758096384" size="536870912" md5="aae011691f4bd471ca626e074979e1b2"/>
                            <Block index="8" start="4294967296" size="536870912" md5="faa7f1a5649699203ce1992341bb7c83"/>
                            <Block index="9" start="4831838208" size="276114411" md5="2896c2b69cc29a56e5a0632a10e1ab18"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk0-WindowsNoEditor.sig" filesize="312296" md5="dc66dafc7dc1c7a5dc9000c6fe560c72"/>
                    <Res filename="Hotta/Content/Paks/pakchunk0optional-WindowsNoEditor.pak" filesize="816310" md5="af1e7d7f8f4ea0fbc57698f9a3bd09c8"/>
                    <Res filename="Hotta/Content/Paks/pakchunk0optional-WindowsNoEditor.sig" filesize="580" md5="31220281bfc7f13d88529ef80b6201cc"/>
                    <Res filename="Hotta/Content/Paks/pakchunk1-WindowsNoEditor.pak" filesize="4439773597" md5="900ec0514d0a13fb28e0d7780c2887ce" blockSize="9">
                            <Block index="0" start="0" size="536870912" md5="3e34827250024ac6878faa73b808f279"/>
                            <Block index="1" start="536870912" size="536870912" md5="3499f3f8995a329d1195d3e7bd8846b1"/>
                            <Block index="2" start="1073741824" size="536870912" md5="0d25add5223e42aaa006d10a3f78d4c0"/>
                            <Block index="3" start="1610612736" size="536870912" md5="d8225f5738b538bf9a811c8b3873d622"/>
                            <Block index="4" start="2147483648" size="536870912" md5="117db940baf219e83eee68b83eacca27"/>
                            <Block index="5" start="2684354560" size="536870912" md5="94c1bffa0c4830ce4019248ad57872ec"/>
                            <Block index="6" start="3221225472" size="536870912" md5="203cf21db15a67b11d6a2f339d73dd24"/>
                            <Block index="7" start="3758096384" size="536870912" md5="f9c7573c93d842a27ff617d837bd2180"/>
                            <Block index="8" start="4294967296" size="144806301" md5="67bec1805e10aca882a8a341857400ca"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk1-WindowsNoEditor.sig" filesize="271512" md5="bf71bf67215014c97735f3cb69903aa1"/>
                    <Res filename="Hotta/Content/Paks/pakchunk10-WindowsNoEditor.pak" filesize="2912709403" md5="333ef167ef196f297f5821812e626055" blockSize="6">
                            <Block index="0" start="0" size="536870912" md5="a9a4e528984b2d1d7dcf5aee12807257"/>
                            <Block index="1" start="536870912" size="536870912" md5="e5973c69e64809161c36d424ab8186d4"/>
                            <Block index="2" start="1073741824" size="536870912" md5="202f2897bb1fa870c98d4985bcebb57d"/>
                            <Block index="3" start="1610612736" size="536870912" md5="d76f80171dcde853c8c9f58e340101c0"/>
                            <Block index="4" start="2147483648" size="536870912" md5="463f17062638efc356c926e26415c719"/>
                            <Block index="5" start="2684354560" size="228354843" md5="bd293d59260604298cb351f73aec00d5"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk10-WindowsNoEditor.sig" filesize="178308" md5="03ce2a60c5e6f59c5c499ab03818ab9d"/>
                    <Res filename="Hotta/Content/Paks/pakchunk1optional-WindowsNoEditor.pak" filesize="9743261" md5="56f07858874d50797603abbb9a3a648f"/>
                    <Res filename="Hotta/Content/Paks/pakchunk1optional-WindowsNoEditor.sig" filesize="1124" md5="b131e320388b0b0e67d9188d093312ed"/>
                    <Res filename="Hotta/Content/Paks/pakchunk2-WindowsNoEditor.pak" filesize="9953760445" md5="5f18ffb801ea8478ac6de479d1a7e3c2" blockSize="19">
                            <Block index="0" start="0" size="536870912" md5="456c71128dee46235f5941910ab07864"/>
                            <Block index="1" start="536870912" size="536870912" md5="da1ed4b2159127a29afe9cba6690d76d"/>
                            <Block index="2" start="1073741824" size="536870912" md5="b99f380de43e4f44b784078f217a756c"/>
                            <Block index="3" start="1610612736" size="536870912" md5="ba5078d015047456d5e85d2c26bc22ca"/>
                            <Block index="4" start="2147483648" size="536870912" md5="dfab67cbcda4931c5a9359e35c6b3ccb"/>
                            <Block index="5" start="2684354560" size="536870912" md5="99987cee1a5edfe5398cf1a6374a1805"/>
                            <Block index="6" start="3221225472" size="536870912" md5="bb7e8aa060eda19fa9491c5a8289f67a"/>
                            <Block index="7" start="3758096384" size="536870912" md5="76e5429af244bbd19137fb49b8a70764"/>
                            <Block index="8" start="4294967296" size="536870912" md5="fb1222c428c762f47403685d726df2c1"/>
                            <Block index="9" start="4831838208" size="536870912" md5="8638da01cbd77c4c7e550bbcfcac5ed0"/>
                            <Block index="10" start="5368709120" size="536870912" md5="7911c9d09c19c2f3b7be40791a3f38cc"/>
                            <Block index="11" start="5905580032" size="536870912" md5="9a160a0346b52c293f4088de271d2617"/>
                            <Block index="12" start="6442450944" size="536870912" md5="a8dd161de992d13d9492cda927d9312e"/>
                            <Block index="13" start="6979321856" size="536870912" md5="c81a2ba097d9f1ed516a027305f1a5af"/>
                            <Block index="14" start="7516192768" size="536870912" md5="5ed8dec0b887c59b60ee3d3a20fc329d"/>
                            <Block index="15" start="8053063680" size="536870912" md5="4b833e21006ec356919fb3c5052fecef"/>
                            <Block index="16" start="8589934592" size="536870912" md5="de833ae9b195b8b068c1b272b6e4116d"/>
                            <Block index="17" start="9126805504" size="536870912" md5="e46fc2214e1c30d6fbb577a375fb36ab"/>
                            <Block index="18" start="9663676416" size="290084029" md5="ee356147865cace59e7da6d857972c89"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk2-WindowsNoEditor.sig" filesize="608060" md5="66ae6340589edf55f849014e20321245"/>
                    <Res filename="Hotta/Content/Paks/pakchunk2optional-WindowsNoEditor.pak" filesize="32843104" md5="afd601ed3e2732582feed3335cf38f7d"/>
                    <Res filename="Hotta/Content/Paks/pakchunk2optional-WindowsNoEditor.sig" filesize="2536" md5="eb34c207d24b521aace1a148533978c8"/>
                    <Res filename="Hotta/Content/Paks/pakchunk3-WindowsNoEditor.pak" filesize="6943391220" md5="ddd966c13fa4a625f0ce80bd013b0142" blockSize="13">
                            <Block index="0" start="0" size="536870912" md5="a6e4d7f102c4d9aaa1ed41c7b456bead"/>
                            <Block index="1" start="536870912" size="536870912" md5="05fbd25491498fba33c5c94e2b4cbdb9"/>
                            <Block index="2" start="1073741824" size="536870912" md5="b3c8b23a5a7cdf49a8cee8a6d06690f9"/>
                            <Block index="3" start="1610612736" size="536870912" md5="fff46ff6f8425346d21e73557fd3a490"/>
                            <Block index="4" start="2147483648" size="536870912" md5="0be3cae22afd3a31852c28ab27de2887"/>
                            <Block index="5" start="2684354560" size="536870912" md5="5ffa4ecf46a74cb2d1244bb7595a10f8"/>
                            <Block index="6" start="3221225472" size="536870912" md5="066317ddf91902b148298f39750633b7"/>
                            <Block index="7" start="3758096384" size="536870912" md5="0ff2cf533f7e46ef44bd3c09b373e707"/>
                            <Block index="8" start="4294967296" size="536870912" md5="f263f0965ee7a6242ff92d7f57349a94"/>
                            <Block index="9" start="4831838208" size="536870912" md5="2f05c72650a6b89e2343c772a92c1228"/>
                            <Block index="10" start="5368709120" size="536870912" md5="63ce129191736b0b907ceeec80a5680a"/>
                            <Block index="11" start="5905580032" size="536870912" md5="f60d9669a9f9ce7882a4a7dd7029af77"/>
                            <Block index="12" start="6442450944" size="500940276" md5="8120f9e4b2c728908bd951cb42aca500"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk3-WindowsNoEditor.sig" filesize="424320" md5="e68b479a9532679626457d96c40003c4"/>
                    <Res filename="Hotta/Content/Paks/pakchunk3optional-WindowsNoEditor.pak" filesize="14516217" md5="920e66cc4de26602a1625d4e843c641f"/>
                    <Res filename="Hotta/Content/Paks/pakchunk3optional-WindowsNoEditor.sig" filesize="1416" md5="64bce6af65f268a3e7cb530f1aa97754"/>
                    <Res filename="Hotta/Content/Paks/pakchunk4-WindowsNoEditor.pak" filesize="1813171065" md5="7c0a9f1917e17598d2c7089c05101717" blockSize="4">
                            <Block index="0" start="0" size="536870912" md5="39d5069815789362acf958d7c51219fd"/>
                            <Block index="1" start="536870912" size="536870912" md5="68c44a0404f57d58cae824935caf3c55"/>
                            <Block index="2" start="1073741824" size="536870912" md5="0899b5acd4094e42dd79cf88fa7e152a"/>
                            <Block index="3" start="1610612736" size="202558329" md5="43236060858fb05b9864735f29dd3870"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk4-WindowsNoEditor.sig" filesize="111196" md5="76d714d6392dafbb2ec4b9563bd3cc8f"/>
                    <Res filename="Hotta/Content/Paks/pakchunk5-WindowsNoEditor.pak" filesize="819643748" md5="80596f8e7efb49d971dcef1b839eb6d7" blockSize="2">
                            <Block index="0" start="0" size="536870912" md5="e4ea79c1d57b30a670474cc2e9a4820b"/>
                            <Block index="1" start="536870912" size="282772836" md5="0a0838f878ea7df70f12977fd9c17c6d"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk5-WindowsNoEditor.sig" filesize="50556" md5="ff2644cf482420a684dad32f72074ced"/>
                    <Res filename="Hotta/Content/Paks/pakchunk5optional-WindowsNoEditor.pak" filesize="26809859" md5="0b160aa9baf16b9e1eb9754fe0685dcb"/>
                    <Res filename="Hotta/Content/Paks/pakchunk5optional-WindowsNoEditor.sig" filesize="2168" md5="0e3fefb709383d0d8f3735bd99ba7f13"/>
                    <Res filename="Hotta/Content/Paks/pakchunk6-WindowsNoEditor.pak" filesize="2104974309" md5="e51bb4a8735e3403bc4cb681ad60263a" blockSize="4">
                            <Block index="0" start="0" size="536870912" md5="476e128a4a82149473ba720947d997cf"/>
                            <Block index="1" start="536870912" size="536870912" md5="3838237d2c6bb9a76da77aa2618f412e"/>
                            <Block index="2" start="1073741824" size="536870912" md5="5199dd601ee14618782ae0c9ee6e003d"/>
                            <Block index="3" start="1610612736" size="494361573" md5="7dae0c6aa1f14da33456730406b2a9d0"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk6-WindowsNoEditor.sig" filesize="129008" md5="2085e296a58160867faf1dbc8b138f5d"/>
                    <Res filename="Hotta/Content/Paks/pakchunk7-WindowsNoEditor.pak" filesize="1084690558" md5="a3c65cea3a3b70918dd7e48a8b112460" blockSize="3">
                            <Block index="0" start="0" size="536870912" md5="4391737dfadf745de69a5d452131bd52"/>
                            <Block index="1" start="536870912" size="536870912" md5="c237ed7a461f1b6e024c89372b918674"/>
                            <Block index="2" start="1073741824" size="10948734" md5="1748226f722239f474c621ce907976f3"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk7-WindowsNoEditor.sig" filesize="66736" md5="7a161c30cc866d4263f80d9fef623242"/>
                    <Res filename="Hotta/Content/Paks/pakchunk7optional-WindowsNoEditor.pak" filesize="197293191" md5="c7b08d0d33ba4a5f11d4da8f8328dcd5"/>
                    <Res filename="Hotta/Content/Paks/pakchunk7optional-WindowsNoEditor.sig" filesize="12572" md5="c1c2812226ec68ddf71810840a479c3b"/>
                    <Res filename="Hotta/Content/Paks/pakchunk8-WindowsNoEditor.pak" filesize="898097435" md5="8539e98472ceca61a05a27014f95dabe" blockSize="2">
                            <Block index="0" start="0" size="536870912" md5="8b2976040a652baf9ea519cf6588e16f"/>
                            <Block index="1" start="536870912" size="361226523" md5="b1850da52fde47bf6594522ca8ecaf0a"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk8-WindowsNoEditor.sig" filesize="55344" md5="3347b3980b873f929cb4f7079333ee5d"/>
                    <Res filename="Hotta/Content/Paks/pakchunk9-WindowsNoEditor.pak" filesize="638546214" md5="f1cab55c5ce3e782dc26ac2995d7cfc5" blockSize="2">
                            <Block index="0" start="0" size="536870912" md5="e3456b31eb77d699b23f06a6a93d4594"/>
                            <Block index="1" start="536870912" size="101675302" md5="59240e3a11b578e2fd746e3a81ecdc57"/>
                    </Res>
                    <Res filename="Hotta/Content/Paks/pakchunk9-WindowsNoEditor.sig" filesize="39504" md5="783a85e77ca601b18609fc0787d73ec1"/>
            </ResList>
            """;
         */
        var body = """
            <?xml version="1.0" ?>
            <ResList version="2.4.2" tag="">
            </ResList>
            """;
        System.out.println("custom resList.xml response\n" + body);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void lastdiffXml(HttpServerRequest req) {
        var body = """
            <?xml version="1.0" ?>
            <PatchList/>
            """;
        System.out.println("custom resList.xml response\n" + body);
        req.response().setStatusCode(200);
        req.response().end(body);
    }

    private void versionTxt(HttpServerRequest req) {
        var version = "2.4.73407";
        System.out.println("custom version.txt response: " + version);
        req.response().setStatusCode(200);
        req.response().end(version);
    }

    private void allFilesXml(HttpServerRequest req) {
        proxy(req.method(), req.uri(), req.headers(), null, (resp, respBodyBuffer) -> {
            if (resp == null) {
                req.response().setStatusCode(404);
                req.response().end("not found\r\n");
                return;
            }
            req.response().setStatusCode(resp.statusCode());
            for (var entry : resp.headers()) {
                req.response().putHeader(entry.getKey(), entry.getValue());
            }
            if (resp.statusCode() != 200) {
                req.response().end(respBodyBuffer);
                return;
            }

            var string = respBodyBuffer.toString();
            var file = "/Config/game/zh_cn/game_200105.cfg";
            var index = string.indexOf(file);
            if (index == -1) {
                System.out.println(file + " not found in xml");
                req.response().end(respBodyBuffer);
                return;
            }

            index = string.lastIndexOf("Checksum", index);
            if (index == -1) {
                System.out.println("Checksum field not found in xml");
                req.response().end(respBodyBuffer);
                return;
            }

            var md5 = "88aa3c3ffb796f662b878bff89f69c38".toCharArray();
            var sb = new StringBuilder(string);
            var off = index + "Checksum=\"".length();
            for (int i = 0; i < md5.length; ++i) {
                sb.setCharAt(off + i, md5[i]);
            }
            string = sb.toString();
            System.out.println("responding with modified response: " + string);
            req.response().end(string);
        });
    }

    private HttpClient client;

    private void proxy(HttpServerRequest req, HttpMethod method, String uri, MultiMap headers, String body) {
        proxy(method, uri, headers, body, (resp, respBodyBuffer) -> {
            if (resp == null) {
                req.response().setStatusCode(404);
                req.response().end("not found\r\n");
                return;
            }
            var respCode = resp.statusCode();
            var respHeaders = resp.headers();
            if (respBodyBuffer.length() > 1024) {
                System.out.println("resp code: " + respCode + "\n" + respHeaders + "\nresp body: (length=" + respBodyBuffer.length() + ")");
            } else {
                var respBody = respBodyBuffer.toString();
                System.out.println("resp code: " + respCode + "\n" + respHeaders + "\nresp body: " + respBody);
            }

            req.response().setStatusCode(respCode);
            for (var entry : respHeaders) {
                req.response().putHeader(entry.getKey(), entry.getValue());
            }
            req.response().end(respBodyBuffer);
        });
    }

    private void proxy(HttpMethod method, String uri, MultiMap headers, String body, BiConsumer<HttpClientResponse, Buffer> cb) {
        var host = headers.get("host");
        System.out.println("host header is " + host);
        if (host == null) {
            cb.accept(null, null);
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
                System.out.println("sending new request");
                if (body == null || body.isEmpty()) {
                    return creq.send();
                } else {
                    return creq.send(body);
                }
            })
            .flatMap(resp -> resp.body().map(respBodyBuffer -> {
                cb.accept(resp, respBodyBuffer);
                return null;
            }))
            .recover(t -> {
                System.out.println("failed to send request");
                t.printStackTrace();
                return null;
            });
    }
}
