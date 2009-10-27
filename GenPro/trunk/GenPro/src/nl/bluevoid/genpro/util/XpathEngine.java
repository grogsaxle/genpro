/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
/**
 * @author Rob van der Veer
 * @since 1.0
 */
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
