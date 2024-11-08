package br.ce.wcaquino.rest;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {
	@Test
	public void deveAcessarSWAPI(){
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
		;
	}

	@Test
	public void deveObterClima(){
		given()
			.log().all()
			.queryParam("lat", "-3.73")
			.queryParam("lon", "-38.52")
			.queryParam("appid", "39271fb9f884db1fb12f9870aed0632c")
			.queryParam("units", "metric")
		.when()
			.get("https://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Fortaleza"))
			.body("coord.lon", is(-38.5218f))
			.body("main.temp", greaterThan(25f))
		;
	}

	@Test
	public void naoDeveAcessarSemSenha(){
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica(){
		given()
			.log().all()
		.when()
			.get("https://admin:senha@restapi.wcaquino.me/basicauth") // metodo alternativo usando URL apenas
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica2(){
		given()
			.log().all()
				.auth().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoBasicaChallenge(){
		given()
			.log().all()
			.auth().preemptive().basic("admin", "senha") // Dessa forma o client monta a request com o header 'Authorization=Basic xxx'
		.when()
			.get("https://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoComToken(){
		Map<String, String> login = new HashMap<String,String>();

		login.put("email", "ree.lopes@teste.com");
		login.put("senha", "teste.com");

		// login na api
		String token = given()
			.log().all()
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
				.extract().path("token")
		;

		// obter contas
		given()
			.log().all()
			.header("Authorization", "JWT " + token)
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", hasItem("Conta de teste"))
		;
	}

	@Test
	public void deveAcessarAplicacaoWeb(){
		//login
		String cookie = given()
			.log().all()
			.formParam("email", "ree.lopes@teste.com")
			.formParam("senha", "teste.com")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.extract().header("set-cookie")
		;

		// obter cookie do header
		cookie = cookie.split("=")[1].split(";")[0];

		// obter contas
		String body = given()
			.log().all()
			.cookie("connect.sid", cookie)
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
			.extract().body().asString();
		;

		// extrair uma conta
		System.out.println("------------------");
		XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
		System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
	}
}
