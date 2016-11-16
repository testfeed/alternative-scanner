/**
 * This class collects all the "control name : value" pairs that will be sent to the server
 * The value part of the pair represents the injected attack vector
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.model

class HtmlAttackForm {

  /*
  * @param method         a string for the type of FORM; GET or POST
  * @param action         a string for the FORM link; this is where the injected controls are sent
  * @param attackVector   a string for the javascript code being injected
  * @param inputs         a list of inputs extracted from FORMs
  */

  def method = ''
  def action = ''
  def attackVector
  def inputs = []

  /**
   * Populates the map of "control name: values"
   * The values are the injected code
   *
   * @returns inputsMap
   *
   */
  def inputsAsMap() {
    def inputsMap = [:]

    inputs.each {input ->
      inputsMap[input] = attackVector.vector
    }
    inputsMap
  }
}
