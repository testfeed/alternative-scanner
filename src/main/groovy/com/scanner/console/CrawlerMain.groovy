/**
 * This class represents the point of entrance to the application
 * It contains the user interaction and it is responsible passing on the user's answers
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.console

import com.scanner.action.SiteValidator

class CrawlerMain {

  /**
   * This is the main method that provides user interaction in the form of console messages
   *
   * @param args as String
   *
   */
  public static void main(String[] args) {
    //define a scanner to capture user response
    def scanner = new Scanner(System.in)
    //the user can only choose between three types of attacks: get, post or both
    def validResponses = ['get', 'post', 'both']
    //the user has to provide a well-formed URL (i.e. contains protocol and host)
    println 'Please provide a well-formed URL'
    // store the user input into a variable
    String urlResponse = scanner.nextLine()
    //while the user does not provide an answer the same question will be answered
    while (!isUrl(urlResponse)) {
      println 'Please provide a well-formed URL'
      urlResponse = scanner.nextLine()
    }
    //ask the user to specify type of attack
    println 'Would you like to perform a GET attack, a POST attack or both? '
    println 'Please enter GET, POST or BOTH'
    // store the user input into a variable
    def attackResponse = scanner.nextLine()
    Boolean validResponse = false
    // loop until the user inputs a correct type of attack
    while (!validResponse) {
      switch (attackResponse.toUpperCase()) {
        case ['GET']: validResponse = true; break;
        case ['POST']: validResponse = true; break;
        case ['BOTH']: validResponse = true; break;
        default:
          println 'GET, POST or BOTH will suffice'
          //ask the same question until a valid response is given
          print 'Your answer please: '
          attackResponse = scanner.nextLine()
      }
    }

    //the URL provided by the user will be stored as rootUrl
    def rootUrl = urlResponse
    //pass the rootUrl and type of attack over to siteValidation where all the action begins
    def siteValidator = new SiteValidator(rootUrl, attackResponse)
    //initiate the "scanning for vulnerabilities" process
    siteValidator.scanAndValidate()

  }

  def static isUrl(String urlResponse) {
    //attempt to create a URL object; if fail catch the exception and return false
    try {
      new URL(urlResponse)
      true
    } catch (e) {
      false
    }
  }


}
