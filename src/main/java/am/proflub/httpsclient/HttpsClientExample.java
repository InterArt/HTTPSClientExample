package am.proflub.httpsclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by arthur on 8/4/17.
 */
public class HttpsClientExample {

    String testUrl = "<HTTPS URL>";
    String keyPassphrase = "<PASSWORD>";
    String fileAddress = "<p12 file address>";

    public void example_apacheHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(fileAddress), keyPassphrase.toCharArray());

        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keyPassphrase.toCharArray())
                .useProtocol("TLS")
                .setSecureRandom(new SecureRandom())
                .build();

        HttpClient httpClient = HttpClients.custom().setSslcontext(sslContext).build();
        HttpResponse response = httpClient.execute(new HttpGet(testUrl));
        printStream(response.getEntity().getContent());
    }

    public void example_javaxNetSSL() throws Exception {
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(new FileInputStream(fileAddress),keyPassphrase.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, keyPassphrase.toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kms, null, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        URL url = new URL(testUrl);

        HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
        urlConn.connect();
        printStream(urlConn.getInputStream());
    }

    public void printStream(InputStream inputStream) throws Exception {
        StringBuilder builder = new StringBuilder();
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buff = new BufferedReader(in);
        String line;
        do {
            line = buff.readLine();
            builder.append(line + "\n");
        } while (line != null);

        System.out.println(builder.toString());
    }

    public static void main(String[] args) throws Exception {
        HttpsClientExample httpsClientExample = new HttpsClientExample();
        httpsClientExample.example_javaxNetSSL();
        httpsClientExample.example_apacheHttpClient();
    }
}
