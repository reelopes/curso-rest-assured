package br.ce.wcaquino.rest;

import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

	@Test
	public void devoTrabalharComXML() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)

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
}
