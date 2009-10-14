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

/*
Copyright (C) 2005 Eric Marion

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
import java.util.ArrayList;

public abstract class BaseFormatter {
    protected ArrayList tokens;
    protected int tokennum;

    public abstract String getName();
    public abstract String format( String code );

    public String toString() {
    	return getName();
    }

    protected boolean curIs( String t ) {
        if( tokennum < 0 || tokennum >= tokens.size() )
            return false;

        return tokens.get( tokennum ).equals( t );
    }

    protected boolean prevIs( String t ) {
        if( tokennum - 1 < 0 || tokennum - 1 >= tokens.size() )
            return false;

        return tokens.get( tokennum - 1 ).equals( t );
    }

    protected boolean nextIs( String t ) {
        if( tokennum + 1 < 0 || tokennum + 1 >= tokens.size() )
            return false;

        return tokens.get( tokennum + 1 ).equals( t );
    }

    protected void tokenize( String code ) {
        tokens = new ArrayList();
        String current = "";
        boolean instring = false;
        int prev;

        for( int count = 0; count < code.length(); count++ ) {
            char letter = code.charAt( count );

            if( letter == '"' ) {
                instring = !instring;

                if( instring ) {
                    if( current.trim().length() != 0 )
                        tokens.add( current );

                    current = "";
                } else {
                    tokens.add( "\"" + current + "\"" );
                    current = "";
                }
            }

            if( !instring && letter != '"' ) {
                switch( letter ) {
                    case '{':
                    case '}':
                    case '(':
                    case ')':
                    case '[':
                    case ']':
                    case '.':
                    case ';':
                    case ',':
                    case '*':
                    case '/':
                    case ':':
                        if( current.trim().length() != 0 )
                            tokens.add( current );

                        if( letter == '/' && count < code.length() - 1 ) {
                            if( code.charAt( count + 1 ) == '/' ) {
                                prev = count;

                                for( ; count < code.length(); count++ )
                                    if( code.charAt( count ) == '\n' )
                                        break;

                                tokens.add( code.substring( prev, count ).trim() );
                                current = "";

                                break;
                            } else if( code.charAt( count + 1 ) == '*' ) {
                                prev = count;

                                for( ; count < code.length() - 1; count++ )
                                    if( code.charAt( count ) == '*' && code.charAt( count + 1 ) == '/' )
                                        break;

                                count += 2;

                                tokens.add( code.substring( prev, count ).trim() );
                                current = "";

                                break;
                            }
                        }

                        tokens.add( "" + letter );
                        current = "";

                        break;
                    case '<':
                    case '>':
                    case '!':
                    case '=':
                    case '+':
                    case '-':
                    case '&':
                    case '|':
                    case '^':
                        int postlevel = 1;
                        char next = 'x', tri = 'x', fourth = 'x';

                        if( count < code.length() - 1 ) {
                            next = code.charAt( count + 1 );

                            if( next == '=' && ( letter == '<' || letter == '>' || letter == '=' ||
                                                 letter == '+' || letter == '-' || letter == '!' ) )
                                postlevel = 2;

                            if( next == letter && ( letter == '&' || letter == '|' || letter == '^' ||
                                                    letter == '+' || letter == '-' || letter == '<' || letter == '>' ) )
                                postlevel = 2;
                        }

                        if( letter == '>' && next == '>' && count < code.length() - 2 ) {
                            tri = code.charAt( count + 2 );

                            if( letter == tri )
                                postlevel = 3;
                        }

                        if( ( letter == '<' || letter == '>' ) && letter == next && count < code.length() - 2 ) {
                            tri = code.charAt( count + 2 );

                            if( tri == '=' )
                                postlevel = 3;
                        }

                        if( letter == '>' && postlevel == 3 && count < code.length() - 3 ) {
                            fourth = code.charAt( count + 3 );

                            if( letter == next && letter == tri && fourth == '=' )
                                postlevel = 4;
                        }

                        if( current.trim().length() != 0 )
                            tokens.add( current );

                        if( postlevel == 2 ) {
                            tokens.add( "" + letter + next );
                        } else if( postlevel == 3 ) {
                            tokens.add( "" + letter + next + tri );
                        } else if( postlevel == 4 ) {
                            tokens.add( "" + letter + next + tri + fourth );
                        } else {
                            tokens.add( "" + letter );
                        }
                        count += postlevel - 1;
                        current = "";

                        break;
                    case '\'':
                        prev = count;

                        if( count < code.length() - 1 && code.charAt( count + 1 ) == '\\' )
                            count += 3;
                        else
                            count += 2;

                        if( current.trim().length() != 0 )
                            tokens.add( current );

                        tokens.add( code.substring( prev, count + 1 ).trim() );
                        current = "";

                        break;
                    case ' ':
                    case '\t':
                    case '\n':
                        if( current.trim().length() != 0 )
                            tokens.add( current );

                        current = "";
                        break;
                    default:
                        current += letter;
                }
            } else {
                if( letter == '\\' && count < code.length() - 1 ) {
                    current += "\\" + code.charAt( count + 1 );
                    count++;
                } else if( letter != '"' ) {
                    current += letter;
                }
            }
        }
    }
}
