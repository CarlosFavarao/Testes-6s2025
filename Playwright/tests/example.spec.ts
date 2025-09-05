import { test, expect } from '@playwright/test';

//Sauce Demo tests
test('login-and-logout-Sauce', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');

  await page.getByPlaceholder('Username').fill('standard_user');
  await page.getByPlaceholder('Password').fill('secret_sauce');
  await page.getByRole('button', {name: 'Login'}).click();

  await expect(page).toHaveURL('https://www.saucedemo.com/inventory.html');

  await page.getByRole('button', {name: 'Open Menu'}).click();
  await page.getByRole('link', {name: 'Logout'}).click();

  await expect(page).toHaveURL('https://www.saucedemo.com/');
});

test('login-denied-Sauce', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');

  await page.getByPlaceholder('Username').fill('standard_user');
  await page.getByPlaceholder('Password').fill('123');
  await page.getByRole('button', {name: 'Login'}).click();

  await expect(page).toHaveURL('https://www.saucedemo.com/');

  await expect(page.locator('[data-test="error"]')).toBeVisible();
});


//Heroku App tests
test('login-and-logout-Heroku', async ({ page }) => {
  await page.goto('https://the-internet.herokuapp.com/login');

  await page.getByLabel('Username').fill('tomsmith');
  await page.getByLabel('Password').fill('SuperSecretPassword!');
  await page.getByRole('button', {name: 'Login'}).click();
  
  await expect(page).toHaveURL('https://the-internet.herokuapp.com/secure');

  await page.getByRole('link', {name: 'Logout'}).click();

  await expect(page).toHaveURL('https://the-internet.herokuapp.com/login');
});

test('login-denied-Heroku', async ({ page }) => {
  await page.goto('https://the-internet.herokuapp.com/login');

  await page.getByLabel('Username').fill('tomsmith');
  await page.getByLabel('Password').fill('123');
  await page.getByRole('button', {name: 'Login'}).click();

  await expect(page).toHaveURL('https://the-internet.herokuapp.com/login');

  const errorMessage = page.locator('#flash.flash.error');
  await expect(errorMessage).toBeVisible();
});

//Practice test
test('login-and-logout-Practice', async ({ page }) => {
  await page.goto('https://practicetestautomation.com/practice-test-login/');

  await page.getByLabel('Username').fill('student');
  await page.getByLabel('Password').fill('Password123');
  await page.getByRole('button', {name: 'Submit'}).click();

  await expect(page).toHaveURL('https://practicetestautomation.com/logged-in-successfully/');
  
  const successMessage = page.locator('.post-title');
  await expect(successMessage).toHaveText('Logged In Successfully'); // tentar fazer nos outros

  await page.getByRole('link', {name: 'Log out'}).click();

  await expect(page).toHaveURL('https://practicetestautomation.com/practice-test-login/');
});

test('login-denied-Practice', async ({ page }) => {
  await page.goto('https://practicetestautomation.com/practice-test-login/');

  await page.getByLabel('Username').fill('student');
  await page.getByLabel('Password').fill('123');
  await page.getByRole('button', {name: 'Submit'}).click();

  await expect(page).toHaveURL('https://practicetestautomation.com/practice-test-login/');

  const errorMessage = page.locator('#error');
  await expect(errorMessage).toBeVisible();
});

//orangeHRM tests
test('login-and-logout-OrangeHRM', async ({ page }) => {
  await page.goto('https://opensource-demo.orangehrmlive.com/');

  await page.getByPlaceholder('Username').fill('Admin');
  await page.getByPlaceholder('Password').fill('admin123');

  await page.getByRole('button', {name: 'Login'}).click();

  await expect(page).toHaveURL('https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index')

  await page.locator('.oxd-userdropdown-img').click();
  await page.getByRole('menuitem', { name: 'Logout' }).click();

  await expect(page).toHaveURL('https://opensource-demo.orangehrmlive.com/web/index.php/auth/login');
});

test('login-denied-OrangeHRM', async ({ page }) => {
  await page.goto('https://opensource-demo.orangehrmlive.com/');
  
  await page.getByPlaceholder('Username').fill('Admin');
  await page.getByPlaceholder('Password').fill('123');

  await page.getByRole('button', {name: 'Login'}).click();

  await expect(page).toHaveURL('https://opensource-demo.orangehrmlive.com/web/index.php/auth/login');
  await page.getByRole('alert', { name: 'Invalid credentials' });
});
