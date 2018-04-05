package net.nilsghesquiere.managerclients;



import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import net.nilsghesquiere.entities.GlobalVariable;
import net.nilsghesquiere.util.wrappers.GlobalVariableSingleWrapper;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.hobsoft.spring.resttemplatelogger.LoggingCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;



public class GlobalVariableManagerRESTClient implements GlobalVariableManagerClient{
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariableManagerRESTClient.class);
	private final String URI_GLOBALVARIABLES;
	private RestTemplate restTemplate;
	
	public GlobalVariableManagerRESTClient(String uriServer, String username, String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		this.URI_GLOBALVARIABLES = uriServer +"/api/vars";

		//Logging 
		restTemplate = new RestTemplateBuilder()
				.customizers(new LoggingCustomizer())
				.build();

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
		
		restTemplate.setRequestFactory(requestFactory);
		
		//authentication
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username,password));
		
	}
	public GlobalVariable getGlobalVariableByName(String name){
		try{
			GlobalVariableSingleWrapper jsonResponse = restTemplate.getForObject(URI_GLOBALVARIABLES + "/" + name, GlobalVariableSingleWrapper.class);
			GlobalVariable globalVariable = jsonResponse.getMap().get("data");
			return globalVariable;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure getting global variable from the server");
			LOGGER.debug(e.getMessage());
			return null;
		}
	}
	
}
