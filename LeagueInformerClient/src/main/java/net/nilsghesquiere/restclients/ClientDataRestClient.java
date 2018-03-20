package net.nilsghesquiere.restclients;

import java.util.Collections;

import net.nilsghesquiere.util.wrappers.ClientDataMap;
import net.nilsghesquiere.util.wrappers.ClientDataWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class ClientDataRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataRestClient.class);
	private final String URI_CLIENTS;
	private RestTemplate restTemplate = new RestTemplate();
	
	public ClientDataRestClient(String uriServer) {
		this.URI_CLIENTS = uriServer +"/api/clients";
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//Temp including these two properties to ignore content and link in json 
		//TODO: find way to delete content and link
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		converter.setObjectMapper(mapper);
		restTemplate.getMessageConverters().add(0,converter);
	}
	
	//TODO: catch IOExceptions(probably) for when the server doesn't respond to a rest call(ALL REST REQUESTS) (see below)
	
	public boolean sendClientData(Long userid, ClientDataMap map){
		boolean result = true;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<ClientDataMap> request = new HttpEntity<>(map, headers);
		try{ 
			HttpEntity<ClientDataWrapper> response = restTemplate.exchange(URI_CLIENTS + "/user/" + userid,  HttpMethod.POST,request, ClientDataWrapper.class);
			ClientDataWrapper clientDataWrapper = response.getBody();
			if (!clientDataWrapper.getError().equals((""))){
				result = false;
				LOGGER.error("Failure updating Client data on the server: " + clientDataWrapper.getError());
			} else {
				LOGGER.debug("Client data updated: " + clientDataWrapper.getMap());
			}
			return result;
		} catch (ResourceAccessException e){
			LOGGER.warn("Failure sending ClientData to server");
			LOGGER.debug(e.getMessage());
			return false;
		}
	}
}
