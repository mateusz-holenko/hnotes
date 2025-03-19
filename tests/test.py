#!/bin/env python
import fire

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait

def testit(headless=False):
    options = Options()
    if headless:
        options.add_argument('--headless=new')
    driver = webdriver.Chrome(options=options)

    driver.get('http://localhost:4200')
    assert driver.title == 'HNotes'

    driver.get('http://localhost:4200/notes')
    # should be redirected to '/login'
    assert driver.current_url == 'http://localhost:4200/login'

    form_handle = driver.find_element(By.XPATH, "//input[@formcontrolname='handle']")
    form_handle.send_keys('admin')
    form_password = driver.find_element(By.XPATH, "//input[@formcontrolname='password']")
    form_password.send_keys('admin')
    form_submit = driver.find_element(By.XPATH, "//button[text()='Sign In']")
    form_submit.click()

    # this should redirect us to '/notes'
    WebDriverWait(driver, 5).until(lambda d: d.current_url == 'http://localhost:4200/notes')

if __name__ == '__main__':
    fire.Fire(testit)
