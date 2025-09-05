from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options 
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


chrome_options = Options()

chrome_options.add_experimental_option("prefs", {
    "credentials_enable_service": False,
    "profile.password_manager_enabled": False,
    "profile.password_manager_leak_detection": False
})

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=chrome_options)

def test_start():
    driver.get("https://the-internet.herokuapp.com/login")
    driver.maximize_window()

def test_login_logout():
    driver.find_element(By.ID, "username").send_keys("tomsmith")
    driver.find_element(By.ID, "password").send_keys("SuperSecretPassword!")
    driver.find_element(By.XPATH, "//button[@type='submit']").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://the-internet.herokuapp.com/secure'))

    driver.find_element(By.CSS_SELECTOR, 'a.button.secondary.radius').click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://the-internet.herokuapp.com/login'))

    print('Teste de Login e Logout bem sucedido!')

def test_login_invalid():
    driver.find_element(By.ID, "username").send_keys("tomsmith")
    driver.find_element(By.ID, "password").send_keys("123")
    driver.find_element(By.XPATH, "//button[@type='submit']").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://the-internet.herokuapp.com/login'))
    assert driver.find_element(By.ID, "flash")

    print('Teste de Login inválido bem sucedido!')

try:
    test_start()
    test_login_logout()
    test_login_invalid()
finally:
    driver.quit()