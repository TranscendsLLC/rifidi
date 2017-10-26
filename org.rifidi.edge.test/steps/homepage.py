from behave import *


@given('we are on the homepage')
def step_impl(context):
    context.browser.get("http://localhost:8111/dashboard/#/")


@then('the header should be {header}')
def step_impl(context, header):
    title = context.browser.find_element_by_xpath("/html/body/div/nav/div/div/a").text
    assert title == header
    assert context.failed is False


