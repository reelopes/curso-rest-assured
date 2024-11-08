package br.ce.wcaquino.rest;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"Jose\",\"age\":50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Jose"))
			.body("age", is(50))
		;
	}

	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"age\":50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
	}

	@Test
	public void deveSalvarUsuarioUsandoMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuário via map");
		params.put("age", 25);

		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(params)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via map"))
			.body("age", is(25))
		;
	}

	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		User user = new User("Usuário via objeto", 35);

		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via objeto"))
			.body("age", is(35))
		;
	}
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		User user = new User("Usuário deserializado", 35);

		User usuarioInserido = given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;

		Assert.assertEquals("Usuário deserializado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is((35)));
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
	}

	@Test
	public void deveSalvarUsuarioXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
		;
	}

	@Test
	public void deveSalvarUsuarioviaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40);

		given()
			.log().all()
			.contentType(ContentType.XML)
//			.contentType(ContentType.XML+";charset=UTF-8" ) // para funcionar com acento (e.g. usuário)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
		;
	}

	@Test
	public void deveDeserializarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 40);

		User usuarioInserido = given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;

//		System.out.print(usuarioInserido);
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
		Assert.assertThat(usuarioInserido.getAge(), is(40));
		Assert.assertThat(usuarioInserido.getSalary(), nullValue());
	}

	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"Usuario alterado\",\"age\":80}")
		.when()
			.put("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}

	@Test
	public void deveCustomizarURL() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"Usuario alterado\",\"age\":80}")
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}

	@Test
	public void deveCustomizarURL2() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\":\"Usuario alterado\",\"age\":80}")
				.pathParam("entidade", "users")
				.pathParam("userId", 1)
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}

	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}

	@Test
	public void naoDeveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400 )
			.body("error", is("Registro inexistente"))
		;
	}
}