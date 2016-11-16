# alternative-scanner

Download the latest chromedriver which is compatible with your Chrome version from http://chromedriver.storage.googleapis.com/index.html

Place chromedriver inside the alternative-scanner dir

Make chromdriver executable

`chmod +x chromedriver`

To run the Alternative Scanner use the following from the command line

`./gradlew run -PappArgs="['https://<<website to scan>>']"`
