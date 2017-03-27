package com.ds.ws.health.util;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Component;

/**
 * Custom Trust Store manager to avoid certificate chain verification
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 */
@Component
final class CustomTrustStoreManager {

    private static class TrustAllHostNameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String hostname, SSLSession session) {
	    return true;
	}

    }

    @PostConstruct
    private void registerCustomTrustStoreManager() {
	// Create a trust manager that does not validate certificate chains
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
	    @Override
	    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	    }

	    @Override
	    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	    }

	    @Override
	    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	    }
	} };

	// Install the all-trusting trust manager
	try {
	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    HttpsURLConnection.setDefaultHostnameVerifier(new TrustAllHostNameVerifier());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
