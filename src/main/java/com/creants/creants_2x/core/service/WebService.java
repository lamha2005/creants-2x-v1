package com.creants.creants_2x.core.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class WebService {

	private static final String KEY = "1|WqRVclir6nj4pk3PPxDCzqPTXl3J";
	private static final String GRAPH_API = "http://api.creants.net:9393/internal/";
	private static WebService instance;


	public static WebService getInstance() {
		if (instance == null) {
			instance = new WebService();
		}
		return instance;
	}


	private WebService() {
	}


	public String verify(String token) {
		WebResource webResource = Client.create().resource(GRAPH_API + "verify");

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("key", KEY);
		formData.add("token", token);

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, formData);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		return response.getEntity(String.class);
	}


	public String getUser(String uid, String key) {
		WebResource webResource = Client.create().resource(GRAPH_API + "user");
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("uid", uid);

		ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).header("key", KEY).post(ClientResponse.class,
				formData);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		return response.getEntity(String.class);
	}


	public static void main(String[] args) {
		String user = WebService.getInstance().verify("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImlkIjoiMzIzIiwiZXhwIjoxNTA2MzExOTc2LCJhcHBfaWQiOiIyIiwidHRsIjo4NjQwMDAwMDB9.fkpS0atvhvHu_OAA-V6ZQqxsZ1Ekgs5-E3W7ANGi89U");
		System.out.println(user.toString());
	}
}
