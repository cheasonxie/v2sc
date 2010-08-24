/*******************************************************************************
 * Copyright (c) 2004, 2006 KOBAYASHI Tadashi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    KOBAYASHI Tadashi - initial API and implementation
 *******************************************************************************/
package parser;

public interface IParser
{
    static public final String EXT_VHDL = "vhd";
    static public final String EXT_VERILOG = "v";    

	public static final int OUT_OF_MODULE = 0;
	public static final int IN_MODULE = 1;
	public static final int IN_STATEMENT = 2;
	//These are tags used to set create tasks inside of comments. The first token is
	//considered to have high priority. The rest are normal priorities
	public static final String[] taskCommentTokens={"FIXME","TODO","FIXME:","TODO:"};

	public void parse() throws ParserException;
	public int getContext();
}

