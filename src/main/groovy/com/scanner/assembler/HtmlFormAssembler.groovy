/**
 * This class retrieves the HTML FORMs controls from a webpage and supports the injection of code into the
 * INPUT fields of the FORMs
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.assembler

import com.scanner.model.AttackVector
import com.scanner.model.HtmlAttackForm

class HtmlFormAssembler {

  /**
   * @param page a Node returned by XML Parser on which GPath can be applied
   *
   */

  Node page

  HtmlFormAssembler(Node page) {
    this.page = page
  }

  /**
   * Initiates the retrieval and injection of GET FORMs
   *
   * @param attackVector as AttackVector
   *
   */
  def assembleGetForms(AttackVector attackVector) {
    assembleForms('get', attackVector)
  }

  /**
   * Initiates the retrieval and injection of POST FORMs
   *
   * @param attackVector as AttackVector
   *
   */
  def assemblePostForms(AttackVector attackVector) {
    assembleForms('post', attackVector)
  }

  /**
   * Attempts to find the FORM nodes in an HTML parsed as an XML
   * For each identified FORM node, the INPUT fields are identified as well.
   * An HtmlAttackForm type of object is constructed which will contain the map of "control name : value" pairs
   * where value is the injected code (attack vector)
   *
   * @param method as String
   * @param attackVector as AttackVector
   * @return htmlForms as List
   *
   */
  def assembleForms(String method, AttackVector attackVector) {
    def htmlForms = []
    //get all POST type of FORMs
    def nodeForms = page.depthFirst().FORM.findAll {it.'@method'?.toLowerCase() == method}
    //for each POST form identify the action link to which the request is made
    nodeForms.each {node ->
      //construct an object of type HtmlAttackForm and populate its field with
      //the method (POST/GET), action (URL to which to submit the FORM) and attack vector information
      def htmlForm = new HtmlAttackForm()
      htmlForm.method = method
      htmlForm.action = node.'@action'
      htmlForm.attackVector = attackVector
      //scan for TEXTAREA nodes that are under each FORM node
      node.'**'.TEXTAREA.each {textarea ->
        //make sure only the right INPUT types are chosen
        if (!['button', 'checkbox', 'image', 'radio', 'reset', 'submit'].contains(textarea.'@type')) {
          //get the names of the INPUT type fields so that a vector can be applied as their value
          htmlForm.inputs << textarea.'@name'
        }
      }
      //scan for all INPUT nodes that are under each FORM node
      node.'**'.INPUT.each {input ->
        //make sure only the right INPUT types are chosen
        if (!['button', 'checkbox', 'image', 'radio', 'reset', 'submit'].contains(input.'@type')) {
          //get the names of the INPUT type fields so that a vector can be applied as their value
          htmlForm.inputs << input.'@name'
        }
      }
      //build a list with all the HTML FORMs
      htmlForms << htmlForm
    }
    //return the list of FORMs
    return htmlForms
  }

}
