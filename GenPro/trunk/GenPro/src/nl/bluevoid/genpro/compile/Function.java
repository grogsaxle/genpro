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

package nl.bluevoid.genpro.compile;

//Copyright (c) 2007 by David J. Biesack, All Rights Reserved.
// Author: David J. Biesack David.Biesack@sas.com
// Created on Nov 4, 2007
/**
 * This interface represents a simply mathematical function {@code y = f(x)} that maps double to doubles.
 */
public interface Function {
  /**
   * Compute a value {@code y=f(x)} of an dependent variable y from an independent variable x
   * 
   * @param x
   *          the input value
   * @return the result of a mathematical function f(x)
   */
  double f(double x);
}
