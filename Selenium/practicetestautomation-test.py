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
    driver.get("https://practicetestautomation.com/practice-test-login/")
    driver.maximize_window()

def test_login_logout():
    driver.find_element(By.ID, "username").send_keys("student")
    driver.find_element(By.ID, "password").send_keys("Password123")
    driver.find_element(By.ID, "submit").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://practicetestautomation.com/logged-in-successfully/'))

    driver.find_element(By.LINK_TEXT, "Log out").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://practicetestautomation.com/practice-test-login/'))

    print('Teste de Login e Logout bem sucedido!')

def test_login_invalid():
    driver.find_element(By.ID, "username").send_keys("student")
    driver.find_element(By.ID, "password").send_keys("123")
    driver.find_element(By.ID, "submit").click()

    assert WebDriverWait(driver, 10).until(EC.url_to_be('https://practicetestautomation.com/practice-test-login/'))
    assert WebDriverWait((driver.find_element(By.ID, "error").is_displayed()), 10)

    print('Teste de Login inválido bem sucedido!')

try:
    test_start()
    test_login_logout()
    test_login_invalid()
finally:
    driver.quit()