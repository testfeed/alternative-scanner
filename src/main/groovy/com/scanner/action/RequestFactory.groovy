/**
 * This class is responsible for all requests to the server
 * Building a request is facilitated by HTTPBuilder's REST Client
 * For building a request the following are needed:
 * *the link to which the request is to be made
 * *the body of the request composed of the injected controls on a form
 * *the content type
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.action

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import com.scanner.model.HtmlAttackForm
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

class RequestFactory {

  /*
  * @param page           a string with the current page for which the attack takes place
  * @param httpBuilder    an HTTPBuilder object for creating the RESTClient
  */

  String page
  HTTPBuilder httpBuilder

  RequestFactory(String page) {
    this.page = page
  }

  /**
   * Creates and attempts to send a request of type GET to the server
   * Populates the body of the request using "control name : value" pairs
   * Retrieves the response and performs different actions for success and failure
   *
   * @param htmlForm as HtmlAttackForm
   * @param toLookFor as int
   *
   */
  def requestGETAttack(HtmlAttackForm htmlForm, int toLookFor) {
    try {
      //go forward only if HTML forms have been found
      if (htmlForm.inputs.size() != 0) {
        //build the url towards which the request is being made
        def getUrl = new URL(page.toURL(), htmlForm.action).toString()
        //build the RESTClient
        httpBuilder = new RESTClient(getUrl)
        //build the GET request
        httpBuilder.request(GET, HTML) {
          //set the request content type
          requestContentType = URLENC
          //builds the body of the request; "control name : values" pairs where values is the injected code
          uri.query = htmlForm.inputsAsMap()
          //if the request succeeds (i.e. response code < 399) parse the response and asses attack efficiency
          response.success = {resp, reader ->
            //send the "reader" (response body) for analysis; include also the attack vector identifier
            def responseAnalyzer = new ResponseAnalyzer(reader, toLookFor)
            //send the attack vector to be identified in the server response
            responseAnalyzer.validateResponse(htmlForm.attackVector)
          }
          //if the request has failed notify user
          response.failure = {resp ->
            println "Failed processing request for page " + page + " having form action " + httpBuilder.defaultURI
            Statistics.toLog += "Failed processing request for page " + page + " having form action " + httpBuilder.defaultURI + '\n'
          }
        }
      }
    } catch (e) {}
  }

  /**
   * Creates and attempts to send a request of type POST to the server
   * Populates the body of the request using "control name : value" pairs
   * Retrieves the response and performs different actions for success and failure
   *
   * @param htmlForm as HtmlAttackForm
   * @param toLookFor as int
   *
   */
  void requestPOSTAttack(HtmlAttackForm htmlForm, int toLookFor) {
    try {
      //go forward only if HTML forms have been found
      if (htmlForm.inputs.size() != 0) {
        //build the url towards which the request is being made
        def postUrl = new URL(page.toURL(), htmlForm.action).toString()
        //build the RESTClient
        httpBuilder = new RESTClient(postUrl)
        httpBuilder.request(POST, HTML) {
          //set the request content type
          requestContentType = URLENC
          //builds the body of the request; "control name : values" pairs where values is the injected code
          body = htmlForm.inputsAsMap()

          //if the request succeeds (i.e. response code < 399) parse the response and asses attack efficiency
          response.success = { resp, reader ->
            //send the "reader" (response body) for analysis; include also the attack vector identifier
            def responseAnalyzer = new ResponseAnalyzer(reader, toLookFor)
            //send the attack vector to be identified in the server response
            responseAnalyzer.validateResponse(htmlForm.attackVector)
          }
          //if the request has failed notify user and log the message
          response.failure = {resp ->
            println "Failed processing request for page " + page + " having form action " + httpBuilder.defaultURI
            Statistics.toLog += "Failed processing request for page " + page + " having form action " + httpBuilder.defaultURI + '\n'
          }
        }
      }
    } catch (e) { }
  }
}