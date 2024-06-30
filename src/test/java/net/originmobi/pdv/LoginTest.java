package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class LoginTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Configurar o caminho do geckodriver
        System.setProperty("webdriver.gecko.driver", "D:\\Download\\geckodriver\\geckodriver.exe");
        
        // Configurar o FirefoxOptions se necessário
        FirefoxOptions options = new FirefoxOptions();
        
        // Inicializar o driver do Firefox
        driver = new FirefoxDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginWithoutCredentials() {
        // Abrir a página de login
        driver.get("http://localhost:8080");

        // Encontrar e clicar no botão de login sem preencher as informações
        WebElement loginButton = driver.findElement(By.id("loginButton")); // Substitua pelo id correto do botão
        loginButton.click();

        // Esperar e encontrar a mensagem de erro
        WebElement errorMessage = driver.findElement(By.id("errorMessage")); // Substitua pelo id correto da mensagem de erro

        // Verificar a mensagem de erro
        String expectedErrorMessage = "Por favor, preencha o campo de login.";
        assertEquals(expectedErrorMessage, errorMessage.getText(), "A mensagem de erro deve corresponder.");
    }
}
