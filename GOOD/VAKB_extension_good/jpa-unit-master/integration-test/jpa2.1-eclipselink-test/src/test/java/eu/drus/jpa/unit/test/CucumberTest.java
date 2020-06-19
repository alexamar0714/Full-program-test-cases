package eu.drus.jpa.unit.test;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = false, format = {
        "pretty", "html:target/site/cucumber-pretty", "json:target/cucumber.json"
}, tags = {
        "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f58bb59c929b9a8790">[email protected]</a>"
}, features = "classpath:bdd-features", glue = "classpath:eu.drus.jpa.unit.test.cucumber.glue")
public class CucumberTest {}
