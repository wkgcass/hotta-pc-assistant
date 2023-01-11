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
import java.util.function.Function;

public class MultiHottaInstancesTest extends Application {
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

                var handled = false;
                if (method == HttpMethod.GET && uri.startsWith("/clientRes/AdvLaunch")) {
                    handled = handleClientRes(req, uri, headers);
                }
                if (!handled) {
                    req.response().setStatusCode(404);
                    req.response().end("not found\r\n");
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
                    MIIDjjCCAnagAwIBAgIJAIvTzI2C9kiYMA0GCSqGSIb3DQEBCwUAMGkxCzAJBgNV
                    BAYTAkNOMREwDwYDVQQIDAhaaGVqaWFuZzERMA8GA1UEBwwISGFuZ3pob3UxDzAN
                    BgNVBAoMBnZwcm94eTEPMA0GA1UECwwGdnByb3h5MRIwEAYDVQQDDAl2cHJveHkt
                    Y2EwHhcNMjMwMTExMTQwMjAyWhcNMjQwMTExMTQwMjAyWjBrMQswCQYDVQQGEwJD
                    TjEQMA4GA1UECAwHSmlhbmdTdTEPMA0GA1UEBwwGU3VaaG91MQ4wDAYDVQQKDAVI
                    b3R0YTEOMAwGA1UECwwFSG90dGExGTAXBgNVBAMMEGh0Y2RuMS53bXVwZC5jb20w
                    ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDsD6e0y5Mmn1Wom+4z7H9z
                    PjT7WJYvcs5HKCYTD7EpRyniz19V6CYZf8oNte4+zUQGz0Zc/2LeESXsqTg9YhI+
                    CBXXEZ+6b4UW6aF1+fophVuZptPM70tkn8otouJZKFZ2udqYPDlhhOItl8Z4JEdC
                    ROsoArPY6jzabedz6uvhoYWuCZZIRE0Zt3faSsZr7SOhIlBceXz8eFLYUcrx4DtX
                    2bHhP/BJ5+taN0KoDx8YfoxxhweT2ZxMx1jx4TFJIOUQoZHPKT+jWcW043ns/65f
                    3s3Rv4bhMCnNAE8M2WuFpVMRMjH8cMZLFirdUmMYLqKkUi0ODL6AhEF55gk3y6ev
                    AgMBAAGjNzA1MAkGA1UdEwQCMAAwCwYDVR0PBAQDAgXgMBsGA1UdEQQUMBKCEGh0
                    Y2RuMS53bXVwZC5jb20wDQYJKoZIhvcNAQELBQADggEBAIrgrIwoUbidFPSoQJHm
                    J6qXaKmU5BpI6mCVCJWSwEimTCHjUSg7YYXKDCf+9mNgu0iAEoNQpVbW+VgHBvQC
                    sp1CK/4wf3FNBh2ZtDIjMzaUfhRbsXLilfi+lVwC9jAZ24m4kXE64x+kCUyFDjcM
                    m1+n+I9nE0dpsp4UL2MI7h44RyPcKlk1tgd0sWloXUiHu6CISCDzLmeV5eMHHxMl
                    gUdIIuyADpUN4gvrHaVIWDPmN0Jjb/4msxLRZMgwYbgEV65F+3hcwMzXQTn1/c3/
                    JxQVijh8svCxkg/sqk0AYHVq8G/XNIX79qzOMj9wU2H95NIAO6gyzXBcthf28Amk
                    KEE=
                    -----END CERTIFICATE-----"""))
                .setKeyValue(Buffer.buffer("""
                    -----BEGIN PRIVATE KEY-----
                    MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDsD6e0y5Mmn1Wo
                    m+4z7H9zPjT7WJYvcs5HKCYTD7EpRyniz19V6CYZf8oNte4+zUQGz0Zc/2LeESXs
                    qTg9YhI+CBXXEZ+6b4UW6aF1+fophVuZptPM70tkn8otouJZKFZ2udqYPDlhhOIt
                    l8Z4JEdCROsoArPY6jzabedz6uvhoYWuCZZIRE0Zt3faSsZr7SOhIlBceXz8eFLY
                    Ucrx4DtX2bHhP/BJ5+taN0KoDx8YfoxxhweT2ZxMx1jx4TFJIOUQoZHPKT+jWcW0
                    43ns/65f3s3Rv4bhMCnNAE8M2WuFpVMRMjH8cMZLFirdUmMYLqKkUi0ODL6AhEF5
                    5gk3y6evAgMBAAECggEBAKADXKLo+65Kgz3vtN/th9cOa8uUmPlA0SLazDRPhr98
                    RaBBAVayLmFKhiQVFpHrghk7l26ijtoItbReYIj7WXJC+Mr4X1V5ErYdWB0ofbJ3
                    e0gEFb3s1yY2Sk31A4l8FmAUS2C2Mg8wjEBriVUXAlahcOWGL3LyFJ3bsfCZZZtj
                    HEu4K9wl2SxNgScDzG7voiY/29ity7RQ+LdKAUfllsE3JRMzeqerOZFjZbBBvpkP
                    U9nZRJiEEI6BIShpd0GrgJ+upl0zxv7gqRTrugd8K75HtPKvhj1/fnwROUgDTZne
                    WI+Qp+JmC+zjuLw/ABaVWF2FeBMKvtSLTa2UQlBWD8ECgYEA/kwfgxE+0jcreV44
                    yYZHNDT1XHNXOWj6judkWGarsHVyEC8UBTs3lzlHCr4yaPdKYq6zOpWx9e0gE4cE
                    /I7vRZ+1cVfiAfqFlmNzL3tHIRFkcSxZpiZl579j7amK2MHYAfRBvGZKmooMmFF8
                    xhrZD/1gdr1eZrEACg7ubtz/2U0CgYEA7aRGO9nu5+bjUw3n7HgiZvmmDG70yG16
                    GCqhbFrxnIB/ec4PQW7Sb7d57ZPLYk0yPqNbWOZRFrUdhc/aUVaNoKrgMqdZeQXF
                    VynbKNFkKwz+j8IveRx92GovCyvpt7VNsdhtBygSQ81NZqh2/RQTs0Z3gwBnD0wp
                    bi5VPDCE5usCgYAiI1n/oGsMkXd2nZ2GAE7CxXFC7gEBpgQs9wdTjsTxtXKQlaFc
                    amy2EmxlFs9xo/EKemV5MetoFmM9+9xBg3a/pAcZpjCjD1yrqcIm16fe2pTfVJLc
                    aT8480qY9y+6lZtH9+BzSFAvmuTB9f6IIcaODPfMOPPZWV2l/AcqPuTh6QKBgD8U
                    P+BbNOMxkl3Vbx6BtQAIfF8IhMk+5g3MIxUT/tY/9ZZrb/k6ZXlqBl9dv7rUKuYd
                    jn3GhPe2E6QVTCoTA30GXoSTj1hkg8FjRt1K13/l7xxRuzA2s+DiLqKUgXFWGs1+
                    WVJw/Igq0sTRKuR6k9AegFKxpKmW2dh5S/6yElUJAoGAG05wBSTuM7jpXvDT3FAA
                    47Ywfo8nqT9/WnuCae6AteCCmOu+kv+If8v+MWiX005aVKbStECdCqziCEfI0Nbl
                    ieKEfYB04kAQyPgdrDaD8p51H56wRlkYU9koxCGZx8s3s/LEa4EOI7/+ZJsOFdMp
                    PwTmrmYRTwFwQjQPZPYaWhc=
                    -----END PRIVATE KEY-----"""))
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

    private HttpClient client;

    private boolean handleClientRes(HttpServerRequest req, String uri, MultiMap headers) {
        uri = uri.substring("/clientRes/".length());
        int index = uri.indexOf("/");
        if (index == -1) {
            return false;
        }
        uri = uri.substring(index);
        uri = "/clientRes/Launcher24/" + uri;

        client.request(new RequestOptions()
                .setSsl(true)
                .setMethod(HttpMethod.GET)
                .setHost("htcdn1.wmupd.com")
                .setPort(443)
                .setURI(uri)
                .setHeaders(headers)
            ).flatMap(creq -> {
                System.out.println("sending new request");
                return creq.send();
            })
            .flatMap(resp -> resp.body().map(respBodyBuffer -> {
                var respCode = resp.statusCode();
                var respHeaders = resp.headers();
                var respBody = respBodyBuffer.toString();
                System.out.println("resp code: " + respCode + "\n" + respHeaders + "\nresp body: " + respBody);

                req.response().setStatusCode(respCode);
                for (var entry : respHeaders) {
                    req.response().putHeader(entry.getKey(), entry.getValue());
                }
                req.response().end(respBody);
                return null;
            }))
            .recover(t -> {
                System.out.println("failed to send request");
                t.printStackTrace();
                return null;
            });
        return true;
    }
}
