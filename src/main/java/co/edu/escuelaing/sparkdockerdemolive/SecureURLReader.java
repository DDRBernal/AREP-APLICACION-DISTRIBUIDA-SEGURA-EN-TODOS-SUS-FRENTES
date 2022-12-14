package co.edu.escuelaing.sparkdockerdemolive;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SecureURLReader {

    /**
     * This method create the keys with the password for the certificates
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     */
    public static void createKey() throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException, KeyManagementException {
        // Create a file and a password representation
        File trustStoreFile = new File("keystores/ecikeystore.p12");
        char[] trustStorePassword = "12345678".toCharArray();
        // Load the trust store, the default type is "pkcs12", the alternative is "jks"
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);
        // Get the singleton instance of the TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Itit the TrustManagerFactory using the truststore object
        tmf.init(trustStore);
        //Set the default global SSLContext so all the connections will use it
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
        // We can now read this URL
        readURL("https://localhost:5000/hello");
        // This one can't be read because the Java default truststore has been
        // changed.
//        readURL("https://www.google.com");
    }

    /**
     * This method read an URL and show the response, if is not possible to connect will throws an error
     * @param url
     * @throws IOException
     */
    private static void readURL(String url) throws IOException {
        try {
            URL siteURL = new URL(url);
            URLConnection urlConnection = siteURL.openConnection();
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                if (headerName != null) {
                    System.out.print(headerName + ":");
                }
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    System.out.print(value);
                }
                System.out.println("");
            }
            System.out.println("-------message-body------");
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
