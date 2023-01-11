package net.cassite.hottapcassistant.test;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.Utils;

import java.net.InetSocketAddress;
import java.util.function.Function;

public class MultiHottaInstancesTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var label = new Label("method:\nuri:\nbody:") {{
            FontManager.setNoto(this);
        }};

        Function<String, HttpHandler> httpHandlerProvider = name -> exchange -> {
            var bodyStream = exchange.getRequestBody();
            var bytes = bodyStream.readAllBytes();
            String msg = "from: " + name + "\nmethod: " + exchange.getRequestMethod() + "\nuri: " + exchange.getRequestURI() + "\nbody: " + new String(bytes);
            Platform.runLater(() -> label.setText(msg));
            System.out.println(msg);
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write(("hello world\r\n").getBytes());
            exchange.getResponseBody().close();
        };

        var server443 = HttpsServer.create(new InetSocketAddress("127.0.0.1", 443), 128);
        var ctx = Utils.buildSSLContext(new String[]{"""
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
            -----END CERTIFICATE-----"""
        }, """
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
            -----END PRIVATE KEY-----""");
        server443.setHttpsConfigurator(new HttpsConfigurator(ctx));
        server443.createContext("/", httpHandlerProvider.apply("443"));
        server443.start();

        var server80 = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 128);
        server80.createContext("/", httpHandlerProvider.apply("80"));
        server80.start();

        primaryStage.setScene(new Scene(new Pane(label)));
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setOnCloseRequest(e -> server443.stop(0));
        primaryStage.show();
    }
}
