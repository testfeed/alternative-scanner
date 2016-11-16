/**
 * This class is responsible for aggregating the metrics of the application
 * It picks up on how successful a collection (all the vector in the XML file) of vectors was on a each page
 * If computes the efficiency for assessing how successful the attack vectors have been on a page.
 *
 * @author: Adrian Rapan
 *
 */


package com.scanner.action


class Statistics {

  /*
  * @param currentPageToAttack    a string containing the page for which the attack took place
  * @param count                  an int to count how many "200 OK" attempts have been done
  * @param successful             an int to keep count of the successful attacks
  * @param toSave                 a string to concatenate all results
  * @param fw                     a file in which to save the metrics
  */

  static currentPageToAttack
  static int count
  static int successful
  static String toSave, toLog
  static fw = new FileWriter("ScanValidate.csv")
  static log = new FileWriter("LogScanValidate.txt")

  /**
   * Computes the efficiency as a percentage of successful vectors from the collection of all vectors for
   * which a response was returned.
   * For example it might happen that the server instead of sending an 200 OK response it sends 404 Not Found
   *
   */
  static def getEfficiency() {
    if (count != 0) {
      def efficiency = (successful / count) * 100
    }
  }

  /**
   * Initializes the necessary class fields for computing the vector collection efficiency
   *
   * @param pageToAttack as String
   *
   */
  static def init(pageToAttack) {
    currentPageToAttack = pageToAttack
    count = 0
    successful = 0
    toSave = ''
    toLog = ''
  }

  /**
   * Informs the user of the efficiency of attacks and also if no forms to attack exist on the page
   *
   */
  static def printEfficiency() {
    //no requests to the server have been done because no HTML FORMs have been detected
    if (count == 0) {
      println 'Page ' + currentPageToAttack + ' does not have POST/GET forms to validate'
      toLog += 'Page ' + currentPageToAttack + ' does not have POST/GET forms to validate \n'
    } else {
      println 'On page ' + currentPageToAttack + ' the efficiency of the attack is ' + getEfficiency() + ' percent'
      toSave += currentPageToAttack + ',' + getEfficiency() + ',' + '\n'
      toLog +=  '\nEfficiency of the attack vector collection on page' +
              currentPageToAttack + ' is ' + getEfficiency() + '\n\n'
    }
    //save the metrics to file
    saveFile(toSave)
    logFile(toLog)
  }

  /**
   * Saves the metrics to file
   *
   * @param toSave as String
   *
   */
  static def saveFile(String toSave) throws IOException {
    try {
      //append the information to file; the file gets wiped clean after each run of the application
      fw.append(toSave)
    } catch (e) {
      println 'File could not be changed'
    }
  }

  static def logFile(String toLog) throws IOException {
    try {
      //append the information to file; the file gets wiped clean after each run of the application
      log.append(toLog)
    } catch (e) {
      println 'File could not be changed'
    }
  }
}
