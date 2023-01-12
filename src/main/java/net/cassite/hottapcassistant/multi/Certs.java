package net.cassite.hottapcassistant.multi;

public class Certs {
    private Certs() {
    }

    public static final String CA_CERT = """
        -----BEGIN CERTIFICATE-----
        MIIDlDCCAnygAwIBAgIJAJ5iFF5jHGTNMA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV
        BAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMQswCQYD
        VQQKDAJ3bTELMAkGA1UECwwCd20wIBcNMjMwMTEyMDcxMDUwWhgPMjEyMjEyMTkw
        NzEwNTBaMEsxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQH
        DAdCZWlqaW5nMQswCQYDVQQKDAJ3bTELMAkGA1UECwwCd20wggEiMA0GCSqGSIb3
        DQEBAQUAA4IBDwAwggEKAoIBAQC8yyloS9uwl6MF7zFsIKH0qo2PL0oP1RMGS0fi
        8lpBysyh8syViTG1bpP2tz8YlZkMpn0M9US43+mGn3LoK4JOSrSzPt5G3SdpLqfS
        qo6ZeSAtFzLUeSiBhDYXbO4LNWWo94X80L4YWhIQqGFbYOYiqLXNd7+m+yvclK9u
        HgB4uGXUIbG3umGf4K97isqj2PQ0yyTc+TUJMIh4N+BJwHzr0T324sD1NZKleEQz
        xTjm/xcPe43q9CzK2oY1Rj4oBYdOwn44o9/skhE2wVTLo0aok+snlJeFVDxbmMKL
        FU5cVg09ipyifSqrcJTewipa95T/1YQNcRGcEfQXCO6Ju1pvAgMBAAGjeTB3MB0G
        A1UdDgQWBBQpDFHUa2ypCo/y73/xjoYljPSOPjAfBgNVHSMEGDAWgBQpDFHUa2yp
        Co/y73/xjoYljPSOPjASBgNVHRMBAf8ECDAGAQH/AgEDMA4GA1UdDwEB/wQEAwIB
        BjARBglghkgBhvhCAQEEBAMCAQYwDQYJKoZIhvcNAQELBQADggEBAHdoO7a9Q/tD
        KntezTJLHpnsHHPE7RKa08KdecVu+L2hBCSmBMczYfB7P2v8k90wyO5yYyryN7y+
        B4aoC9/nGXxfInKhpPwBjmbo/VVfNW3DW020VuQQ0wF7PL/yyflNSYISRzVjZflP
        ChPktCPaTxH96UG1kpLCBzTszIgPsGhanT85Owu3SLAGslO4r/ighSXVc85qvjP8
        orymdYwMSuE1jPKde2m90mj6aBLMD/9An4RMLr7mLLVxs+Lu0kTUl2Sd7JrOJ5rP
        KDayOsr+QADj1BFdml4RZztJ31KCzVjCdZJlCp8ImeIkesZFf6JB1oPKj+we5Iig
        Qv+9S9fjnW8=
        -----END CERTIFICATE-----
        """;

    public static final String HOTTA_CERT = """
        -----BEGIN CERTIFICATE-----
        MIIDpzCCAo+gAwIBAgIJAJScLhpGi6EKMA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV
        BAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMQswCQYD
        VQQKDAJ3bTELMAkGA1UECwwCd20wHhcNMjMwMTEyMDcxMzUyWhcNMjQwMTEyMDcx
        MzUyWjBrMQswCQYDVQQGEwJDTjEQMA4GA1UECAwHSmlhbmdTdTEPMA0GA1UEBwwG
        U3VaaG91MQ4wDAYDVQQKDAVIb3R0YTEOMAwGA1UECwwFSG90dGExGTAXBgNVBAMM
        EGh0Y2RuMS53bXVwZC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB
        AQCsiFsX4Ch0qiqXWdhqc522SRgDb/+T8g4Mv4sYnq7r3JsxypPsyBJcezeU4h6j
        cay1rhm0zFgvph8OyhzlJiqn3eJeYNIpl0Av9VDeqtzyJaeEzlR5l4cMm6mildsA
        TSkY3ZiHhuVomK1hw+76WOOVoYZBDRNNs8ac3yq9puFJRFjlyAGS27qBwa1RbUi6
        MWDq4DVzbWc3cOusK0isIKPJmAUgBzkDtfZQnDhSul6OcQPC+zGcafBCdw/HKwKi
        J5HSemuuGzHZwHNG1aGBFqljfePLmOB+45KwGRNxnDt/XrpW/26Jooz3YTKHIySB
        f1DZpiwl6EVmqnDo8S3/BKYBAgMBAAGjbjBsMAkGA1UdEwQCMAAwCwYDVR0PBAQD
        AgXgMFIGA1UdEQRLMEmCEGh0Y2RuMS53bXVwZC5jb22CEGh0Y2RuMi53bXVwZC5j
        b22CEGh0eWRoZC53bXVwZC5jb22CEXBtcGNkbjEud211cGQuY29tMA0GCSqGSIb3
        DQEBCwUAA4IBAQBIDX6+485MN9CehHuURJGn0+uKg/XsOSucoLqxVZNiztnFmc7v
        M8OGiGEvldTqBnfhsIw2gxidQgpi7xYPNPVRXRammXu1Z3NfxTtgJtjCdKX3dyja
        GuQZ9hqAeOZiGAJR0X9canXZrIdp07xYsSSHYV9knrlzsl8KngVl+OjO/huA+Gcy
        eVJ97xXYIWYY0WkUM72G4LaEOr+FcUcuO9XY3D1aQCwB1yrLIKIb7q6OwuFM4Gyb
        K6T2hI+MQzueVDm0rxsy7LERN2R3tkqcRNu6ycpchYD2AAYzNQOOaLbtiSKvc4mq
        WCrXE5TgNpcA8fI2iJ8ZCxznF1wojAuWtF/N
        -----END CERTIFICATE-----
        """;

    public static final String HOTTA_KEY = """
        -----BEGIN PRIVATE KEY-----
        MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCsiFsX4Ch0qiqX
        Wdhqc522SRgDb/+T8g4Mv4sYnq7r3JsxypPsyBJcezeU4h6jcay1rhm0zFgvph8O
        yhzlJiqn3eJeYNIpl0Av9VDeqtzyJaeEzlR5l4cMm6mildsATSkY3ZiHhuVomK1h
        w+76WOOVoYZBDRNNs8ac3yq9puFJRFjlyAGS27qBwa1RbUi6MWDq4DVzbWc3cOus
        K0isIKPJmAUgBzkDtfZQnDhSul6OcQPC+zGcafBCdw/HKwKiJ5HSemuuGzHZwHNG
        1aGBFqljfePLmOB+45KwGRNxnDt/XrpW/26Jooz3YTKHIySBf1DZpiwl6EVmqnDo
        8S3/BKYBAgMBAAECggEBAI44jJae0dpxl1BR4ILHsV7c2+2hehVzd79seXWfAOQu
        YsPlkJCjz/bqH0QcLVNf1hWhYVFXDO6iSoG/e5gtIQZlcj+IIlXkdyXRW4thX2ZA
        QdnI13uvu8RZ4LjEPNX/xPvZVu2I9jyFdo7bm3hEo73peyjOZXYs1nDjyXCySo1A
        bkQ9Cq+AnpTdLKP7v4kp+tk1V26YnuQ5wAS+mwFmdaFq+KWXECJko8X01lP2nZrl
        8iAHr34Sv14Tvtk3EUVu1aaKzMxZPc2Yv0vKiApITjwlSMJMqtkrBI4EgXO4cPmI
        5gtwIIKjzfMNi6cxkR0obaoanMb8McbPY+XU0ay2oEECgYEA2CaEqqbe/X4jZaOs
        c8GDwMjsd61goQfp3qZ5zIWMPn9EfabFwZmXrH+coi6zPQ9cAdb6IzzTXewVFUBZ
        FSxI64tGxkJo2ejthcI2GZyFqyKtmJEP4rM4iXWiFUuT66ctjFWFiSQJhpf0D2cj
        VvG+AMAutxlagYegk7PTLseOP0kCgYEAzFc97G0KSTPDzbU/gUbIvYy6laRFpCmQ
        LS59oVvve7W2XUqoENtf9iWNohGs0BZkP5Fra0W/LPeu/iDVabi5HlKI3aF2RlZO
        SWdxZARQbizH1fARnhagOAcgDc8Yq+FM77fpUpja4rbsqsbi8os460kOah5j9N1o
        WdIUR7ioWPkCgYAFztsnvr4lL5uB4qkCn0OMDZoVCSqciRrmqbCCJ2CCWWccvTt5
        HUhvESrDTN/yFxSQ1Bb6lWrUjkBuxsDeCceX1LE9KJGqsVCYlIRhocwRsifZ/qPf
        opM1VlzZl6NM5Lwbljy1V6uN201qkInpjytc180pjFvbOSq+bUmSazhOSQKBgDnO
        hGoZ/srupfnPyINd7vQQl8g1jK4ARBVEDa5UP8+TfF+v+xOpu9/6h7LNpUHI09tX
        3Degp5ClERA7YaRi6Zu2ZZmtT7RePg9uABn3TnmcmXvgSluaswCmo17Es4oMVqOK
        N6tyKpnwfctiMFnFIPkO1dEXf9eJuzeNwsb3akihAoGBAJhbFiWcRjthnykJ9oA6
        5UsccxRpqCYUAENmjl7zXLGV7Ee6A7oq463rHWGOZQsb2OmNFuOAZg7xvunwBg5u
        Riv4KPoGXy9DYL8U6kW65fKqbYbg4F5oA0y75BSKzXRYuCjcczFrp5uVBn21Bvc4
        nh1N65KCCjEIcGQLUe++pGP0
        -----END PRIVATE KEY-----
        """;
}
