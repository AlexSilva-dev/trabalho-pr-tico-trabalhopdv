import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

#É necessário estar rodando o programa na porta 8080

@pytest.fixture(scope="module")
def driver():
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
    yield driver
    driver.quit()

def test_sem_infos(driver):
    driver.get("http://localhost:8080/login")
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    error_message = driver.find_element(By.XPATH, "/html/body/div/div/div/div[1]/span").text
    assert error_message == "Usuário ou senha inválidos"

def test_somente_user(driver):
    driver.get("http://localhost:8080/login")
    username_input = driver.find_element(By.ID, "user")
    username_input.send_keys("gerente")
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    error_message = driver.find_element(By.XPATH, "/html/body/div/div/div/div[1]/span").text
    assert error_message == "Usuário ou senha inválidos"

def test_senha_errada(driver):
    driver.get("http://localhost:8080/login")
    time.sleep(2)
    username_input = driver.find_element(By.ID, "user")
    password_input = driver.find_element(By.ID, "password")
    username_input.send_keys("gerente")
    password_input.send_keys("456")
    time.sleep(1)
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    time.sleep(2)
    error_message = driver.find_element(By.XPATH, "/html/body/div/div/div/div[1]/span").text
    assert error_message == "Usuário ou senha inválidos"

def test_login_Aceito(driver):
        driver.get("http://localhost:8080/login")
        time.sleep(2)
        username_input = driver.find_element(By.ID, "user")
        password_input = driver.find_element(By.ID, "password")
        username_input.send_keys("gerente")
        password_input.send_keys("123")
        time.sleep(1)
        login_button = driver.find_element(By.ID, "btn-login")
        login_button.click()
        success_message = driver.find_element(By.XPATH,"/html/body/section[3]/div/div/div/div[1]/div/div/p[1]").text
        time.sleep(2)
        assert success_message == "Pedidos em Aberto"


def test_cadastro_produto_vazio(driver):
    driver.get("http://localhost:8080/login")
    time.sleep(2)
    username_input = driver.find_element(By.ID, "user")
    password_input = driver.find_element(By.ID, "password")
    username_input.send_keys("gerente")
    password_input.send_keys("123")
    time.sleep(1)
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    time.sleep(2)
    driver.get("http://localhost:8080/produto/form")
    time.sleep(2)
    login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
    login_button.click()
    time.sleep(2)
    error_message = driver.find_element(By.XPATH, ' // *[ @ id = "descricao-error"]').text
    assert error_message == "Este campo é requerido."

def test_cadastro_sem_preco(driver):
        driver.get("http://localhost:8080/login")
        time.sleep(2)
        username_input = driver.find_element(By.ID, "user")
        password_input = driver.find_element(By.ID, "password")
        username_input.send_keys("gerente")
        password_input.send_keys("123")
        time.sleep(1)
        login_button = driver.find_element(By.ID, "btn-login")
        login_button.click()
        time.sleep(2)
        driver.get("http://localhost:8080/produto/form")
        time.sleep(2)
        login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
        login_button.click()
        time.sleep(2)
        name_input = driver.find_element(By.ID, "descricao")
        name_input.send_keys("Teste2")
        login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
        login_button.click()
        error_message = driver.find_element(By.ID, 'valorVenda-error').text
        assert error_message == "Este campo é requerido."


def test_cadastro_completo(driver):
    driver.get("http://localhost:8080/login")
    time.sleep(2)
    username_input = driver.find_element(By.ID, "user")
    password_input = driver.find_element(By.ID, "password")
    username_input.send_keys("gerente")
    password_input.send_keys("123")
    time.sleep(1)
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    time.sleep(2)
    driver.get("http://localhost:8080/produto/form")
    time.sleep(2)
    login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
    login_button.click()
    time.sleep(2)
    name_input = driver.find_element(By.ID, "descricao")
    custo_input = driver.find_element(By.ID, "valorCusto")
    venda_input = driver.find_element(By.ID, "valorVenda")
    data_validade_input = driver.find_element(By.ID, "validade")
    name_input.send_keys("Teste4")
    custo_input.send_keys("5,00")
    venda_input.send_keys("7,50")
    data_validade_input.send_keys('31/12/2024')
    time.sleep(1)
    login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
    login_button.click()
    error_message = driver.find_element(By.XPATH, '/html/body/section[2]/div/div/div[1]/div/span').text
    time.sleep(1)
    assert error_message == "Produdo cadastrado com sucesso"

def test_cadastro_validade_expirada(driver):
        driver.get("http://localhost:8080/login")
        time.sleep(2)
        username_input = driver.find_element(By.ID, "user")
        password_input = driver.find_element(By.ID, "password")
        username_input.send_keys("gerente")
        password_input.send_keys("123")
        time.sleep(1)
        login_button = driver.find_element(By.ID, "btn-login")
        login_button.click()
        time.sleep(2)
        driver.get("http://localhost:8080/produto/form")
        time.sleep(2)
        login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
        login_button.click()
        time.sleep(2)
        name_input = driver.find_element(By.ID, "descricao")
        custo_input = driver.find_element(By.ID, "valorCusto")
        venda_input = driver.find_element(By.ID, "valorVenda")
        data_validade_input = driver.find_element(By.ID, "validade")
        name_input.send_keys("Teste5")
        custo_input.send_keys("5,00")
        venda_input.send_keys("7,50")
        data_validade_input.send_keys('01/01/2020')
        time.sleep(1)
        login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
        login_button.click()
        error_message = driver.find_element(By.XPATH, '/html/body/section[2]/div/div/div[1]/div/span').text
        time.sleep(1)
        assert error_message == "Produdo cadastrado com sucesso"

def test_cadastro_valor_negativo(driver):
    driver.get("http://localhost:8080/login")
    time.sleep(2)
    username_input = driver.find_element(By.ID, "user")
    password_input = driver.find_element(By.ID, "password")
    username_input.send_keys("gerente")
    password_input.send_keys("123")
    time.sleep(1)
    login_button = driver.find_element(By.ID, "btn-login")
    login_button.click()
    time.sleep(2)
    driver.get("http://localhost:8080/produto/form")
    time.sleep(2)
    login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
    login_button.click()
    time.sleep(2)
    name_input = driver.find_element(By.ID, "descricao")
    custo_input = driver.find_element(By.ID, "valorCusto")
    venda_input = driver.find_element(By.ID, "valorVenda")
    data_validade_input = driver.find_element(By.ID, "validade")
    name_input.send_keys("Teste5")
    custo_input.send_keys("5,00")
    venda_input.send_keys("-10,00")
    data_validade_input.send_keys('10/10/2024')
    time.sleep(2)
    login_button = driver.find_element(By.XPATH, '//*[@id="form_produto"]/input[2]')
    login_button.click()
    error_message = driver.find_element(By.XPATH, '/html/body/section[2]/div/div/div[1]/div/span').text
    time.sleep(2)
    assert error_message == "Produdo cadastrado com sucesso"
