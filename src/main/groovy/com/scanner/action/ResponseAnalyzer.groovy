/**
 * This class is responsible for analysing the response from the serve
 * It attempts to identify if an attack has succeeded by scanning the HTML stream and
 * trying to retrieve the code that has been injected in the initial request
 * It does this by traversing the parsed HTML response, and looking for the "element [+ attribute]" node
 * and checking its value.
 * If the unique "CrossSiteScripting" + a constant value is found then the attack is deemed
 * successful
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.action

import com.scanner.model.AttackVector

class ResponseAnalyzer {

  /*
  * @param reader     a XMLSlurper NodeChild representing the server response to the injected request
  * @param toLookFor  an int for uniquely identifying an attack vector in the attacked page
  */

  def reader
  def toLookFor

  ResponseAnalyzer(reader, toLookFor) {
    this.reader = reader
    this.toLookFor = toLookFor
  }

  /**
   * Validates the response received from the server once the attack has taken place
   * Based on the information available on the vector object:
   * element
   * attribute
   * it tries to apply a GPath customised for that specific element and attribute
   * If the unique "CrossSiteScripting" string is found on the HTML response then the attack is successful
   * Two types of checks are done
   * one that does not look for the attribute of an element as the vector object does not have the information
   * another one that does look for the attribute
   * In both cases the successful field from the Statistics class is incremented
   *
   * @param htmlForm as HtmlAttackForm
   * @param toLookFor as int
   *
   */
  def validateResponse(AttackVector attackVector) {
    //increments the counter for the number of attacks for which a response was actually received;
    //if the request fails all together than when the efficiency of the attacks are calculated, than failed attacks
    //will not get counted
    Statistics.count++
    //read in the information needed for GPath traversal
    def element = attackVector.element
    def attribute = attackVector.attribute
    def code = attackVector.vector

    //if there is no attribute in the vector object
    if (attribute.isEmpty()) {
      //apply the GPath expression over the "reader" object (represents the server response parsed by XMLSlurper)
      if (reader.'**'.findAll {
        //toLookFor acts as an unique identifier in the attack collections so that if more than one vector gets
        //permanently embedded in the HTML page then other attack will not pick up on the "success" of that attack
        it.name().toLowerCase() == element && it.text().contains('CrossSiteScripting' + toLookFor)
      }.size() >= 1) {
        //let the user know that the attack succeeded and increment the success rate of the attack vector collection
        println 'Attack was successful with vector ' + code
        Statistics.toLog += 'Attack was successful with vector ' + code + '\n'
        Statistics.successful++
      }
    } else {
      //same as before only that this time the vector object posses attribute information
      if (reader.'**'.findAll {
        it.name().toLowerCase() == element && it.attributes().find {it.value.contains('CrossSiteScripting' + toLookFor)}
      }.size() >= 1) {
        println 'Attack was successful with vector ' + code
        Statistics.toLog += 'Attack was successful with vector ' + code + '\n'
        Statistics.successful++
      }
    }
  }
}
