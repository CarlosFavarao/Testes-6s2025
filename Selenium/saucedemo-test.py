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

def test_login_logout():
    driver.get("https://www.saucedemo.com/")
    driver.maximize_window()

    driver.find_element(By.ID, "user-name").send_keys("standard_user")
    driver.find_element(By.ID, "password").send_keys("secret_sauce")
    driver.find_element(By.ID, "login-button").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://www.saucedemo.com/inventory.html'))

    driver.find_element(By.ID, 'react-burger-menu-btn').click()

    WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.ID, 'logout_sidebar_link'))
    ).click()
    
    WebDriverWait(driver, 10).until(EC.url_to_be('https://www.saucedemo.com/'))
    print('Teste de Login e Logout bem sucedido!')

def test_login_invalid():
    driver.get("https://www.saucedemo.com/")
    driver.maximize_window()

    driver.find_element(By.ID, "user-name").send_keys("user")
    driver.find_element(By.ID, "password").send_keys("123")
    driver.find_element(By.ID, "login-button").click()

    error_message = WebDriverWait(driver, 5).until(
        EC.visibility_of_element_located((By.CSS_SELECTOR, 'h3[data-test="error"]'))
    ).text

    assert error_message == "Epic sadface: Username and password do not match any user in this service"
    assert driver.current_url == 'https://www.saucedemo.com/'

    print('Teste de Login inválido bem sucedido!')

try:
    test_login_logout()
    test_login_invalid()
finally:
    driver.quit()