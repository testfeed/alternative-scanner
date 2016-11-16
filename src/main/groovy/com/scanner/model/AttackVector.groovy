/**
 * This class builds the vector object;
 * adds attributes to the vector object like
 * *the injected element
 * *the attribute of that element that will be injected
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.model

class AttackVector implements Cloneable {

  /*
  * @param vector     a string for containing the javascript code to be injected
  * @param element    a string for containing the element name in a parsed HTML
  * @param attribute  a string for containing the attribute name in a parsed HTML
  */

  String vector
  String element
  String attribute

  AttackVector(String vector, String element, String attribute) {

    this.vector = vector
    this.element = element
    this.attribute = attribute

  }

  String toString() {
    vector
  }

}
