package com.barclays.solveit.ws.health.util;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Custom Trust Store manager to avoid certificate chain verification
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
final class CustomTrustStoreManager {
	
	@PostConstruct
	private void initCerts() {
		System.setProperty("javax.net.ssl.keyStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
		System.setProperty("javax.net.ssl.keyStore","C:/certstore/auto-bulk-complaint-loader-ylvt.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "bulk01");
		System.setProperty("javax.net.ssl.trustStore","C:/certstore/auto-bulk-complaint-loader-ylvt.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "bulk01");
		System.setProperty("javax.net.debug", "true");

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						return true;
					}
				});

	}

}
