from behave import *


@given('we are on the homepage')
def step_impl(context):
    context.browser.get("http://localhost:8111/dashboard/#/")


@then('the header should be {header}')
def step_impl(context, header):
    title = context.browser.find_element_by_xpath("/html/body/div/nav/div/div/a").text
    assert title == header
    assert context.failed is False


@when('i open the server')
def step_impl(context):
    context.browser.find_element_by_xpath("/html/body/div/div[2]/div/div[1]/div/div[2]/div/ul[12]/li/div/ul[12]/li/span").click()
    assert 1 == 1


@when('I change the server display name to {name}')
def step_impl(context, name):
    print("name is {}".format(name))
    input = context.browser.find_element_by_xpath("//*[@id='displayName']")
    input.clear()
    input.send_keys(name)


@when('I click save')
def step_impl(context):
    context.browser.find_element_by_xpath("/html/body/div/div[2]/div/div[2]/div[2]/div[2]/div/div/form/input").click()


@then('the server name should be {name}')
def step_impl(context, name):
    server = context.browser.find_element_by_xpath("/html/body/div/div[2]/div/div[1]/div/div[2]/div/ul[12]/li/div/ul[12]/li/span").text
    print("name is {}".format(name))
    print("server is {}".format(server))
    assert name == server
