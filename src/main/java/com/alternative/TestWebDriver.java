package com.alternative;

import org.openqa.selenium.chrome.ChromeDriver;

public class TestWebDriver {

    private ChromeDriver driver;

    public TestWebDriver(ChromeDriver driver)
    {
        this.driver = driver;
    }

    public Attacker attack (String urlToAttack)
    {
        return new Attacker(driver, urlToAttack);
    }

    public ValidationReport executionReportFor(String vector)
    {
        return new ValidationReport(driver, vector);
    }

    public void shutdown()
    {
        driver.close();
    }
}
