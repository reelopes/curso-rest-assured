package br.ce.wcaquino.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.path.xml.element.Node;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

	public static RequestSpecification reqSpec;
	public static ResponseSpecification resSpec;

	@BeforeClass
	public static void setup(){
		baseURI = "https://restapi.wcaquino.me";
		//port = 443;
		//basePath = "v2";

		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpec = reqBuilder.build();

		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		resSpec = resBuilder.build();

		requestSpecification = reqSpec;
		responseSpecification = resSpec;
	}

	@Test
	public void devoTrabalharComXML() {

		given()
//			.spec(reqSpec)
			//.log().all()
		.when()
			.get("/usersXML/3")
		.then()
//			.spec(resSpec)
//			.statusCode(200)

			// Definindo nó raiz
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3")) // para recuperar propriedade, usar @

			// Sobrescrevendo nó raiz
			.rootPath("user.filhos")
			.body("name.size()", is(2))

			// Removendo um nível do nó raiz
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))

			// Adicionando um nível do nó raiz
			.appendRootPath("filhos")
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho","Zezinho"))
		;
	}

	@Test
	public void devoFazerPesquisasAvancadasComXML() {
		given()
//			.spec(reqSpec)
		.when()
			.get("/usersXML")
		.then()
//			.spec(resSpec)
//			.statusCode(200)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2)) // Retorno dentro de {} é um NodeImpl e por isso precisa de casting para comparação.
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d ))
			.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}

	@Test
	public void devoFazerPesquisasAvancadasComXMLEJava() {
		ArrayList<Node> nomes = given()
		.when()
			.get("/usersXML")
		.then()
//			.statusCode(200)
//			.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
			.extract().path("users.user.name.findAll{it.toString().contains('n')}");

		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}

	@Test
	public void devoFazerPesquisasAvancadasComXPath() {
		given()
		.when()
			.get("/usersXML")
		.then()
//			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3")))
			.body(hasXPath("/users/user[@id = 1]")) // verifica se esse cara existe
			.body(hasXPath("//user[@id = 2]")) // busca no primeiro nivel e depois no segundo nivel
			.body(hasXPath("//name[text()='Luizinho']/../../name", is("Ana Julia")))
			.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString( "Luizinho"))))
			.body(hasXPath("//name", is("João da Silva")))
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina"))) // modo alternativo
		;
	}

}
