package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");

		assertEquals("Ola Mundo!", response.getBody().asString());
		assertEquals(200, response.statusCode());
		assertEquals("O status code deveria ser 200", 200, response.statusCode());
		assertEquals(200, response.statusCode());

		ValidatableResponse validacao = response.then();

		validacao.statusCode(200);
	}
	
	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	
		get("http://restapi.wcaquino.me/ola").then().statusCode(200);
		
		given()	//Pré Condições
		.when()	//Ação
			.get("http://restapi.wcaquino.me/ola")
		.then()	//Assertivas
			.statusCode(200);
	}

	@Test
	public void devoConhecerMatchersHamcrest(){
		assertThat("Maria", is("Maria"));
		assertThat(128, is(128));
		assertThat(128, isA(Integer.class));
		assertThat(128d, isA(Double.class));
		assertThat(128d, greaterThan(120d));
		assertThat(128d, lessThan(130d));

		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, hasSize(5));
		assertThat(impares, contains(1,3,5,7,9));
		assertThat(impares, containsInAnyOrder(1,3,5,9,7));
		assertThat(impares, hasItem(1));
		assertThat(impares, hasItems(3,5));

		assertThat("Maria", is(not("João")));
		assertThat("Maria", not("João"));
		assertThat("Maria", anyOf(is("Maria"), is("Joaquim")));
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));
	}

	@Test
	public void devoValidarBody(){
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(Matchers.is("Ola Mundo!"))
			.body(Matchers.containsString("Mundo"))
			.body(is(not(nullValue())));
	}

}
