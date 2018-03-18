from selenium import webdriver


def before_all(context):
    context.browser = webdriver.Chrome()


def after_all(context):
    context.browser.quit()

