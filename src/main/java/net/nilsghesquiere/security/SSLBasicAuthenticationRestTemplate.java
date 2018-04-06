package net.nilsghesquiere.security;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import net.nilsghesquiere.logging.LoggingRequestInterceptor;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

public class SSLBasicAuthenticationRestTemplate extends RestTemplate {
	public SSLBasicAuthenticationRestTemplate(String username, String password, Boolean debugHTTP) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		super();
		//SSL 
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			.loadTrustMaterial(null, acceptingTrustStrategy)
			.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
			
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(csf)
				.build();
			
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		
		//Logging
		if (debugHTTP){
			BufferingClientHttpRequestFactory bufferingRequestFactory= new BufferingClientHttpRequestFactory(requestFactory);
			this.setRequestFactory(bufferingRequestFactory);
			this.getInterceptors().add(new LoggingRequestInterceptor());
		} else {
			this.setRequestFactory(requestFactory);
		}

		
		//authentication
		this.getInterceptors().add(new BasicAuthorizationInterceptor(username,password));

		
	}
}
