package com.alternative;
import org.openqa.selenium.*;

public class ValidationReport {

    private WebDriver driver;
    private String vector;
    public int numberOfInjections = 0;
    public int numberOfSuccesses = 0;
    public int numberOfFailures = 0;

    public ValidationReport(WebDriver driver, String attackVector) {
        this.driver = driver;
        this.vector = attackVector;
    }

    public void waitFor() {
        if (hasPayloadTriggeredWhenEventFired()) {
            System.out.println("Attack succeeded for vector: " + vector + " on page " + driver.getCurrentUrl());
        }
    }

    private boolean hasPayloadTriggeredWhenEventFired() {
        try {
            Object payloadToExecute = ((JavascriptExecutor) driver).executeScript("return (document.evaluate(\"//@*[contains(.,'CrossSiteScriptingAcademia')]\", document, null, XPathResult.STRING_TYPE, null)).stringValue");
            ((JavascriptExecutor) driver).executeScript(payloadToExecute.toString());
            return hasPayloadTriggeredThroughAlert();
        } catch (WebDriverException wde) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    private boolean hasPayloadTriggeredThroughAlert() {
        Boolean success = false;
        try {
            Alert alertXss = driver.switchTo().alert();
            if (alertXss.getText().contains("CrossSiteScriptingAcademia"))
            {
                success = true;
                while (!alertXss.getText().isEmpty())
                {
                    //when it does manage to dismiss the alert it will throw a WevDriverException
                    alertXss.dismiss();
                }
                return true;
            } else {
                while (!alertXss.getText().isEmpty())
                {
                    //when it does manage to dismiss the alert it will throw a WevDriverException
                    alertXss.dismiss();
                }
                return false;
            }
        } catch (WebDriverException wde) {
            return success;
        } catch (NullPointerException npe) {
            return false;
        }
    }
}
