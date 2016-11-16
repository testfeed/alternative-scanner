/**
 * This class parses the XML file from where all attack vectors are sourced
 * It then populates the attack vector object with the necessary information for carrying out an attack
 *
 * @author: Adrian Rapan
 *
 */
package com.scanner.model

class AttackXmlParser {

  /*
  * @param xmlFile        a Node representing the parsed XML file
  * @param attackVectors  a list of all attack vectors
  */

  Node xmlFile
  def attackVectors = []

  AttackXmlParser(String xmlPath) {

    this.xmlFile = new XmlParser().parse(xmlPath)
    //traverse the given XML and extract information on what element, attribute to infect and with what code
    this.xmlFile.attack.each {vector ->
      def attackVector = new AttackVector(vector.code.text(), vector.element.text(), vector.attribute.text())
      //build the list of vector objects
      attackVectors << attackVector
    }
  }

}
