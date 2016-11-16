/**
 * This class handles crawling of web pages;
 * It does so by traversing the html parsed as XML, with GPath
 * All HREFs are extracted and the process of crawling repeats itself until some criteria are met:
 * ***no more pages left to crawl
 * ***5 HTML FORMS have been found
 *
 * @author: Adrian Rapan
 *
 */

package com.scanner.action

public class Crawler {

  /*
  * @param rootUrl        a string representing the url given as input by the user
  * @param processed      list of unique processed links
  * @param toProcess      list of unique links to be processed
  * @param host           a string for the host of the starting url
  * @param pagePrefix     list of accepted protocols
  * @param unwantedPages  list of pages to be avoided; '#' symbolizes anchors on a page
  * @param formCount      an int for storing the number of forms found during crawling
  */

  def rootUrl = ""
  def processed = [] as Set
  def toProcess = [] as Set
  def host = rootUrl.toURL().host.replaceAll(/www./, '')
  def unwantedPages = ['=http://', '=www', 'mailto', 'javascript', '#']
  def formCount = 0

  def Crawler(String rootUrl) throws IOException {
    this.rootUrl = rootUrl
  }

  /**
   * Starts the crawling process by first crawling the url given by the user
   * The crawling process is stopped when the toProcess list is empty or an sufficient number of HTML forms has
   * been reached
   * Once a link has been crawled it gets removed from the toProcess list
   * Also if a "crawling" fails the involved link is removed from the toProcess and processed lists
   *
   */
  def crawl() throws IOException {
    this.crawl(rootUrl)
    //setting a limit on how much "crawling" is done per site: formCount < 5
    while (toProcess.size() > 0 && formCount < 5) {
      //safely remove a page that's been crawled from the toProcess list
      String crtLink = toProcess.iterator().next()
      toProcess.remove(crtLink)
      //attempt a crawl; if all fails erase the link's tracks
      try {
        println 'Crawling page ' + crtLink
        crawl(crtLink)
      } catch (e) {
        toProcess.remove(crtLink)
        processed.remove(crtLink)
      }
    }
  }

  /**
   * Starts the crawling process by first adding the url page to the processed list
   * It the initiate the extraction of links for future processing
   *
   * @param page as String
   *
   */
  def crawl(String page) throws IOException {
    processed << page
    extractLinks(page)
  }

  /**
   * Goes through an HTML stream and searches for all HREFs in that stream
   * Loops through all HREFs and asses the suitability of each individual HREF to be added to the toProcess list
   *
   * @param page as String
   *
   */
  def extractLinks(String page) throws IOException {
    //list of urls found on a page are stored without duplicates
    def urls = [] as Set
    //the page url is changed to an URL object
    def pageUrl = new URL(page)
    //the content of the page is retrieved as a Node for easy GPath traversal
    def content = getContent(page)
    //GPath for collecting all links on a page
    urls = content.depthFirst().A['@href']
    //threshold for stopping the crawling process
    if (containsForm(content, pageUrl)) {formCount++}
    //loop through all found links and asses if its suitable for future parsing
    urls.each {url ->
      //avoid redirect, an anchor on the page, a mailto, javascript links
      if (!unwantedPages.any {url?.contains(it)}
              //avoid null urls
              && url != null
              //avoid empty string urls
              && url.size() > 0) {
        addToProcess(new URL(pageUrl, url.toString()).toString())
      }
    }
  }

  /**
   * Retrieves the content of an url
   * Treats the HTML for missing tags
   * Parses the HTML as an XML
   *
   * @param url as String
   * @return page content as Node
   *
   */
  def getContent(String url) throws MalformedURLException,
          IOException {
    //XML the link for ease of traversal and tag + attribute retrieval
    new XmlParser(new org.cyberneko.html.parsers.SAXParser()).parse(url)
  }

  /**
   * Checks if an url is suitable to be parsed in the future i.e. to be added to toProcess list
   *
   * @param url as String
   *
   */
  def addToProcess(String url) {
    //is link a well-formed link?
    if (isUrl(url)
            //has link already been processed?
            && !isProcessed(url)
            //is link already in the toProcess list?
            && !toProcess.contains(url)
            //is url from the same domain as the root url?
            && (url.toURL()?.host?.contains(host))) {
      this.toProcess << url
    }
  }

  /**
   * Checks if an url has already been processed
   *
   * @param url as String
   * @return boolean
   *
   */
  def isProcessed(String url) {
    //if the list of processed pages already contains the url, return true
    processed.contains(url)
  }

  /**
   * Checks if an url string is a well-formed URL
   *
   * @param url as String
   * @return boolean
   *
   */
  def isUrl(String url) {
    //attempt to create a URL object; if fail catch the exception and return false
    try {
      new URL(url)
      true
    } catch (e) {
      false
    }
  }

  /**
   * Checks if a parsed page contains HTML FORM nodes
   *
   * @param pageContent as Node; page as URL
   * @return boolean
   *
   */
  def containsForm(Node pageContent, URL page) {
    //use GPath to look for HTML FORM nodes
    //if HTML FORMs exist return true
    pageContent.depthFirst().FORM.findAll {
      it.'@method'?.toLowerCase() == 'get' || it.'@method'?.toLowerCase() == 'post'
    }.empty
  }

}
