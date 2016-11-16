package com.alternative;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Attacker {

    private String vector;
    private String url;
    private WebDriver driver;

    public Attacker(WebDriver driver, String urlToAttack) {
        this.driver = driver;
        this.url = urlToAttack;
    }

    public Attacker using(String attackVector) {
        this.vector = attackVector;
        return this;
    }

    public void run() {

        List<WebElement> allInputs;
        List<WebElement> allForms;

        driver.get(url);

        allForms = driver.findElements(By.xpath("//form"));
        for (WebElement form : allForms) {
            allInputs = form.findElements(By.xpath(".//input"));
            allInputs.addAll(form.findElements(By.xpath(".//textarea")));
            for (WebElement input : allInputs) {

                    if (input.isDisplayed() && input.isEnabled() &&
                        (input.getAttribute("type").equals("text") || input.getTagName().equals("textarea"))) {
                    input.sendKeys(vector);
                }
            }

            form.submit();
            break;
        }
    }
}
