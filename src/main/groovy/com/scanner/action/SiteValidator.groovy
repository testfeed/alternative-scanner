/**
 * This class coordinates all the actions to support the delivery of attacks and obtaining metrics
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.action

import com.scanner.model.AttackXmlParser
import com.scanner.assembler.HtmlFormAssembler
import com.scanner.model.AttackVector

class SiteValidator {

  /**
   * @param rootUrl a string representing the website on which the application will run
   * @param attackType a string for what type of attack to be carried out (POST/GET/both)
   *
   */

  String rootUrl
  String attackType

  SiteValidator(String rootUrl, String attackType) {
    this.rootUrl = rootUrl
    this.attackType = attackType
  }

  /**
   * Initiates the following:
   *  crawling
   *  loading of attack vectors
   *  retrieving of HTML FORMs
   *  injection of code into the FORMs
   *  server requests containing injected FORMs
   *  statistics on the attack
   *
   */
  def scanAndValidate() {
    //initialize crawler with the URL provided by user
    def myCrawler = new Crawler(rootUrl)
    //crawl the URL
    myCrawler.crawl()
    //select the XML file to be used as repository of attack vectors
    def xmlPath = 'resources/attackVector.xml'
    //load the attack vectors
    def attackVectors = new AttackXmlParser(xmlPath).attackVectors
    Statistics.fw.append('URL, Efficiency,\n')

    //for each of the processed paged from the crawl process
    myCrawler.processed.each {pageToAttack ->
      //get its content as a XML Paser Node object
      def page = myCrawler.getContent(pageToAttack)
      //get the FORMs from that page
      def formAssembler = new HtmlFormAssembler(page)
      //initialize the request object
      def requestFactory = new RequestFactory(pageToAttack)

      //notify user of page the attack is being performed on
      println 'Performing attack on page ' + pageToAttack + '...'

      //initialize the Statistics class
      Statistics.init(pageToAttack)
      //log the messages to the user
      Statistics.toLog += 'Performing attack on page ' + pageToAttack + '...\n'
      //provide a count to be used in uniquely identifying an attack vector; it is needed in case
      //that page already contains the "CrossSiteScripting" string from a previous attack
      int count = 0
      //for each of the attack vectors
      attackVectors.each {vector ->
        //clone the attack vector as it is required the collection of vectors remains intact for future attacks
        AttackVector vectorClone = (AttackVector) vector.clone()
        //increment the count so that each vector receives a different integer in its tail
        count++
        //substitute the actual attack vector "CrossSiteScripting" string with the same string but with the
        //unique identified added at the end.
        vectorClone.vector = vector.vector.replaceAll('CrossSiteScripting', 'CrossSiteScripting' + count)
        //switch between the different attack options available to the user
        switch (attackType.toLowerCase()) {
          case ['get']: doGetAttack(requestFactory, formAssembler, vectorClone, count); break
          case ['post']: doPostAttack(requestFactory, formAssembler, vectorClone, count); break
          case ['both']: doGetAttack(requestFactory, formAssembler, vectorClone, count)
            doPostAttack(requestFactory, formAssembler, vectorClone, count); break
        }
      }
      //print the efficiency of the vector collection for each page
      Statistics.printEfficiency()
    }
    //close the file in each the metrics are saved
    Statistics.fw.close()
    Statistics.log.close()
  }

  /**
   * Starts an attack of type POST (i.e. using injected POST forms)
   *
   * @param requestFactory as RequestFactory
   * @param formAssembler as HtmlFormAssembler
   * @param vectorClone as AttackVector
   * @param count as int
   *
   */
  def doPostAttack(requestFactory, formAssembler, vectorClone, count) {
    //injects the POST FORMs with an attack vector
    def postForms = formAssembler.assemblePostForms(vectorClone)
    //for each injected FORM deliver the attack
    postForms.each {
      htmlForm ->
      requestFactory.requestPOSTAttack(htmlForm, count)
    }
  }

  /**
   * Starts an attack of type GET (i.e. using injected GET forms)
   *
   * @param requestFactory as RequestFactory
   * @param formAssembler as HtmlFormAssembler
   * @param vectorClone as AttackVector
   * @param count as int
   *
   */
  def doGetAttack(requestFactory, formAssembler, vectorClone, count) {
    //injects the GET FORMs with an attack vector
    def getForms = formAssembler.assembleGetForms(vectorClone)
    //for each injected FORM deliver the attack
    getForms.each {htmlForm ->
      requestFactory.requestGETAttack(htmlForm, count)
    }
  }

}
