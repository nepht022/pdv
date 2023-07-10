package net.originmobi.pdv.sistema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;

import com.beust.jcommander.internal.Console;

import ch.qos.logback.core.joran.action.Action;

@SpringBootTest(classes = ProdutosTest.class)
public class ProdutosTest {
	ChromeDriver driver;
	private int qtProdutos=0;
	private String produtoNome="Abobora";
	private String produtoPreco="4,80";
	
	@BeforeAll
	public static void configuraDriver() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Pichau\\Desktop\\Selenium\\chromedriver.exe");
	}
	
	@BeforeEach
	public void createDriverELogin() {
		driver = new ChromeDriver();
		driver.get("http://localhost:8080/login");
	}
	
	@Test
	public void TestaLogin() {
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();
		
		assertEquals("http://localhost:8080/", driver.getCurrentUrl());
		//Verifica se a url é a correta
		assertEquals("PDV", driver.getTitle());
		//Verifica se o titulo é correto
		driver.close();
	}
	
	@Test
	public void TestaLogoff() {
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();
		driver.findElement(By.xpath("/html/body/div[2]/div/form/button")).click();
		
		assertEquals("PDV - Login", driver.getTitle());
		//Verifica se o titulo é o correto
		driver.close();
	}
	
	@Test
	public void TestaListaProdutos() {
		//  --Login--
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();
		
		driver.navigate().to("http://localhost:8080/produto");
        List<WebElement> text = driver.findElements(By.className("glyphicon"));
        qtProdutos = text.size()-1;
        
		assertTrue(qtProdutos!=0);//Verifica se a lista de produtos nao ta vazia
		assertEquals(12, qtProdutos);//Verifica se na lista contem uma quantidade especifica
		
		WebElement codigo = driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr[3]/td[1]"));
		WebElement nome = driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr[3]/td[2]"));
		WebElement preco = driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr[3]/td[3]"));
		
		assertNotNull(codigo.getText());//Verifica se o produto tem codigo, ou seja, existe
		assertEquals("Maça", nome.getText());//Verifica se o nome do produto é o que deveria
		String valorPreco = preco.getText().replace("R$", "").replaceAll("[.\"0, ]", "");
		assertTrue(Integer.parseInt(valorPreco)>2 && Integer.parseInt(valorPreco)<10);
		//Verifica se o preco ta dentro de um padrao
		driver.close();
	}
	
	@Test
	public void TestaBuscaProdutos() {
		//  --Login--
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();
		
		driver.navigate().to("http://localhost:8080/produto");
		
		driver.findElement(By.id("descricao")).sendKeys("Maça");
		driver.findElement(By.xpath("/html/body/section[2]/div/div/div[2]/div/form/div/div/button")).click();
		
		WebElement nome = driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr/td[2]"));
		List<WebElement> text = driver.findElements(By.className("glyphicon"));
		qtProdutos = text.size()-1;
		
		assertTrue(qtProdutos>0);//Verifica se tem algum produto do buscado
		assertEquals(1, qtProdutos);//Verifica se somente 1
		assertEquals("Maça", nome.getText());//Verifica o nome do produto buscado
		driver.close();
	}
	
	@Test
	public void TestaEditaProduto() {
		//  --Login--
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();
		
		driver.navigate().to("http://localhost:8080/produto");
		
		driver.findElement(By.id("descricao")).sendKeys("Maça");
		driver.findElement(By.xpath("/html/body/section[2]/div/div/div[2]/div/form/div/div/button")).click();
		driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr/td[7]/a")).click();
		
		WebElement cod = driver.findElement(By.name("codigo"));
		WebElement desc = driver.findElement(By.name("descricao"));
		assertEquals("3", cod.getAttribute("value"));//Verifica o id do produto editado
		assertEquals("Maça", desc.getAttribute("value"));//Verifica o nome do produto editado	
		
		WebElement price = driver.findElement(By.id("valorVenda"));
		price.clear();
		driver.findElement(By.id("valorVenda")).sendKeys("9,00");
		
		WebElement newPrice = driver.findElement(By.id("valorVenda"));
		assertEquals("R$ 9,00", newPrice.getAttribute("value"));//Verifica se o preço atualizado é o esperado
		
		WebElement ativo = driver.findElement(By.id("ativo"));
		Select selecao = new Select(ativo);
		selecao.selectByValue("INATIVO");
		
		WebElement campo = driver.findElement(By.id("ativo")); 
		assertTrue(!(campo.getText().isEmpty()));//Verifica se o campo de texto nao esta vazio

		driver.findElement(By.xpath("//*[@id=\"form_produto\"]/input[2]")).click();
		driver.findElement(By.xpath("//*[@id=\"btn-padrao\"]/a")).click();
		
		WebElement newPreco = driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr/td[3]"));
		assertEquals("R$ 9,00", newPreco.getText());//Volta para a pagina de lista de produtos e verifica se o preco foi atualizado
		driver.close();
	}

	@Test
	public void TestaAddProduto(){
		//  --Login--
		driver.findElement(By.name("username")).sendKeys("gerente");
		driver.findElement(By.name("password")).sendKeys("123");
		driver.findElement(By.xpath("//*[@id=\"btn-login\"]")).click();	
		
		driver.navigate().to("http://localhost:8080/produto");
		driver.findElement(By.xpath("//*[@id=\"btn-padrao\"]/a")).click();
				
		WebElement cod = driver.findElement(By.id("codigo"));
		assertEquals("true", cod.getAttribute("disabled"));//Verifica se o codigo ta desabilitado para editar
		assertEquals("", cod.getAttribute("value"));//Verifica se o codigo esta vazio, ou seja sera auto incrementado
		
		driver.findElement(By.id("descricao")).sendKeys(produtoNome);
		driver.findElement(By.id("valorVenda")).sendKeys(produtoPreco);
		
		WebElement modbc = driver.findElement(By.id("modbc"));
        Select select = new Select(modbc);
        select.selectByValue("3");
        
		driver.findElement(By.name("enviar")).click();
		qtProdutos++;
		assertNotNull(qtProdutos);//Verifica se a quantidade de produtos nao é nula
		
		driver.close();
	}
}
