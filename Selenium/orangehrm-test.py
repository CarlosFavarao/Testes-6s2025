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
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")
    driver.maximize_window()

def test_login_logout():
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.NAME, "username")))

    driver.find_element(By.NAME, "username").send_keys("Admin")
    driver.find_element(By.NAME, "password").send_keys("admin123")
    driver.find_element(By.XPATH, "//button[@type='submit']").click()

    WebDriverWait(driver, 10).until(EC.url_to_be("https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index"))
    assert driver.current_url == "https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index"

    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.CSS_SELECTOR, "p.oxd-userdropdown-name"))).click()
    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH, "//a[text()='Logout']"))).click()

    WebDriverWait(driver, 10).until(EC.url_to_be("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"))
    assert driver.current_url == "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"

    print("Teste de Login e Logout bem sucedido!")

def test_login_invalid():
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login")

    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.NAME, "username")))

    driver.find_element(By.NAME, "username").send_keys("Admin")
    driver.find_element(By.NAME, "password").send_keys("123")
    driver.find_element(By.XPATH, "//button[@type='submit']").click()

    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, "//p[contains(@class, 'oxd-alert-content')]")))
    assert driver.current_url == "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"

    print("Teste de Login inválido bem sucedido!")

try:
    test_start()
    test_login_logout()
    test_login_invalid()
finally:
    driver.quit()
