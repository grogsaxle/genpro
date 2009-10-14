/*
 * This file is part of GenPro, Reflective Object Oriented Genetic Programming.
 *
 * GenPro offers a dual license model containing the GPL (GNU General Public License) version 2  
 * as well as a commercial license.
 *
 * For licensing information please see the file license.txt included with GenPro
 * or have a look at the top of class nl.bluevoid.genpro.cell.Cell which representatively
 * includes the GenPro license policy applicable for any file delivered with GenPro.
 */

package nl.bluevoid.genpro.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XpathEngine {

	private final XPath xpath = XPathFactory.newInstance().newXPath();

	/**
	 * Evaluate an xpath expression on a document.
	 * 
	 * @param document
	 *            The document to examine.
	 * @param path
	 *            The path to use.
	 * @return The value in the expression.
	 * @throws XPathExpressionException 
	 * @throws ServletException
	 *             When there was an error parsing the document.
	 */
	public String eval(final Document document, final String path) throws XPathExpressionException {
		return (String) xpath.evaluate(path, document, XPathConstants.STRING);
	}

	/**
	 * Evaluate an xpath expression on a document.
	 * 
	 * @param document
	 *            The document to examine.
	 * @param path
	 *            The path to use.
	 * @return The value in the expression.
	 * @throws XPathExpressionException 
	 * @throws NumberFormatException 
	 * @throws ServletException
	 *             When there was an error parsing the document.
	 */
	public int evalInt(final Document document, final String path) throws NumberFormatException, XPathExpressionException {
		return Integer.parseInt((String) xpath.evaluate(path, document,
				XPathConstants.STRING));
	}

	
	public String[] evalStringArray(final Document document, final String path) throws XPathExpressionException
	{
		ArrayList<String> strings=new ArrayList<String>();
		NodeList o =(NodeList)xpath.evaluate(path, document,
				XPathConstants.NODESET);
		for (int i = 0; i < o.getLength(); i++) {
			strings.add(o.item(i).getTextContent());
		}
		return strings.toArray(new String[0]);
		//System.out.println("evalStringArray"+o.getClass().getName());
		//return null;
	}
	/**
	 * Evaluate an xpath expression on a document.
	 * 
	 * @param document
	 *            The document to examine.
	 * @param path
	 *            The path to use.
	 * @return The value in the expression.
	 * @throws XPathExpressionException 
	 * @throws MalformedURLException 
	 * @throws ServletException
	 *             When there was an error parsing the document.
	 */
	public URL evalURL(final Document document, final String path) throws MalformedURLException, XPathExpressionException {
		return new URL((String) xpath.evaluate(path, document,
				XPathConstants.STRING));
	}

	/**
	 * Evaluate an xpath expression on a document.
	 * 
	 * @param document
	 *            The document to examine.
	 * @param path
	 *            The path to use.
	 * @return The value in the expression.
	 * @throws XPathExpressionException 
	 * @throws ServletException
	 *             When there was an error parsing the document.
	 */
	public boolean evalBool(final Document document, final String path) throws XPathExpressionException {
		final String bool = ((String) xpath.evaluate(path, document,
				XPathConstants.STRING)).toLowerCase();

		return bool.equals("true") || bool.equals("yes") || bool.equals("on")
				|| bool.equals("1");
	}
}
