/*
    Copyright 2006 Manmohan Reddy

    This file is part of logQL.

    logQL is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    logQL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with logQL.  If not, see <http://www.gnu.org/licenses/>.

    $Id: LogCLI.java,v 1.2 2009/10/29 05:11:17 mreddy Exp $
*/
package com.logql.inter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import com.logql.ui.QueryFrame;
import com.logql.util.UtilMethods;

public class LogCLI {
	ResultWriter writer = new CSVWriter();
	Statement stmt = QConnection.createStatement();
	boolean detailedErrors;
	File output;

	public void desc() {
		try {
			String[] flds = QConnection.describe(stmt);
			for (String fm : flds) {
				System.out.println(fm);
			}
		} catch (SQLException se) {
			System.err.println("Error: "+se.getMessage());
		}
	}

	public void help(){
		System.out.println("describe");
		System.out.println("activate <key> <copy key to user home TRUE|FALSE>");
		System.out.println("set output <fileName|console>");
		System.out.println("set outputformat <HTML|CSV>");
		System.out.println("set errorDetails <on|off>");
		System.out.println("from <datafile> use <config>");
		System.out.println("<query>");
		System.out.println("exit");
	}

	public void processCmd(String cmd) {
		String lcmd = cmd.toLowerCase();
		if(lcmd == null || lcmd.trim().length()==0){
			//do nothing
		} else if (lcmd.startsWith("desc")) {
			desc();
		} else if(lcmd.equalsIgnoreCase("help")){
			help();
		} else if (lcmd.startsWith("set ")) {
			lcmd = lcmd.substring("set ".length());
			cmd = cmd.substring("set ".length());
			if (lcmd.startsWith("output ")) {
				cmd = cmd.substring("output ".length());

				setOuput(cmd);
			} else if (lcmd.startsWith("outputformat ")) {
				String opt = lcmd.substring("outputformat ".length()).trim();
				setWriter(opt);
			} else if (lcmd.startsWith("errordetails")) {
				String opt = lcmd.substring("errordetails ".length()).trim();
				detailedErrors = opt.equals("on");
				UtilMethods._ErrorDetails = detailedErrors;
				writer.setDetailedError(detailedErrors);
			}
		} else if (lcmd.startsWith("use ") || lcmd.startsWith("from ")) {
			try{
				stmt.executeQuery(cmd);
			}catch(SQLException se){
				System.err.println("Error: "+se.getMessage());
			}
		} else if (lcmd.startsWith("select") || lcmd.startsWith("grep")) {
			try {
				long start = System.currentTimeMillis();
				ResultSet op = stmt.executeQuery(cmd);
				long end = System.currentTimeMillis();
				if(op != null) {
					if (output == null)
						writer.write(op, System.out);
					else {
						FileOutputStream out = new FileOutputStream(output,
								true);
						writer.write(op, out);
						out.close();
					}
				}
				System.out.println("Time taken: "+ (end - start));
			} catch (IOException ie) {
				ie.printStackTrace();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} else {
			System.err.println("Unknown command");
		}
	}

	public void setWriter(String opt) {
		if (opt.equals("csv")) {
			writer = new CSVWriter();
		} else if (opt.equals("html")) {
			writer = new HTMLWriter();
		} else {
			System.err.print("Error: Unknown format: " + opt);
		}
		writer.setDetailedError(detailedErrors);
	}

	public void setOuput(String cmd) {
		if (cmd.equalsIgnoreCase("console")) {
			output = null;
		} else {
			output = new File(cmd);
			try {
				FileOutputStream out = new FileOutputStream(output);
				out.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

	public void processCli(String h[]){
		ArrayList<String> cmds = new ArrayList<String>(Arrays.asList(h));
		int i=0;
		for(;i<cmds.size(); i++){
			String lcmd = cmds.get(i).toLowerCase();
			if(lcmd.equals("select")||lcmd.equals("grep"))
				break;
			if(lcmd.equals("-o")){
				if(++i<cmds.size()){
					String lc = cmds.get(i).toLowerCase();
					setWriter(lc);
				}
			} else if(lcmd.equals("-f")){
				if(++i <cmds.size()){
					setOuput(cmds.get(i));
				}
			}
		}
		String lcmd = cmds.get(i).toLowerCase();
		if (lcmd.equals("select") || lcmd.equals("grep")) {
			StringBuffer sb = new StringBuffer();
			for(;i<cmds.size();i++){
				sb.append(cmds.get(i)).append(" ");
			}
			Statement stmt = QConnection.createStatement();

			try {
				ResultSet op = stmt.executeQuery(sb.toString());
				if (op != null) {
					if (output == null)
						writer.write(op, System.out);
					else {
						FileOutputStream out = new FileOutputStream(output,
								false);
						writer.write(op, out);
						out.close();
					}
				}
			} catch (IllegalArgumentException iae) {
				System.err.println("Error: "+iae.getMessage());
				System.exit(2);
			} catch (IOException ie) {
				ie.printStackTrace();
				System.exit(3);
			} catch (SQLException se) {
				se.printStackTrace();
				System.exit(4);
			}
		} else {
			System.err.println("Error: Invalid query");
			System.exit(1);
		}
	}

	public void processCommandsFromFile(String[] h) throws IOException {
		File f=new File(h[1]);
		BufferedReader in = new BufferedReader(new FileReader(f));
		String buff = null;
		while ((buff = in.readLine()) != null) {
			if (buff.trim().length() > 0) {
				Statement stmt = QConnection.createStatement();
				System.out.println("Executing: " + buff);
				try {
					stmt.executeQuery(buff);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		in.close();
	}

	public static void main(String h[]){
		if (h.length == 0) {
			QueryFrame qf = new QueryFrame();
			qf.init(null);
		} else {
			LogCLI lcli = new LogCLI();
			if (h[0].equalsIgnoreCase("-c")) {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				PrintStream out = System.out;
				while (true) {
					try {
						out.print("logQL>");
						String cmd = in.readLine();
						if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit"))
							System.exit(0);
						else
							lcli.processCmd(cmd);
					} catch (IllegalArgumentException ie) {
						System.err.println("Error: " + ie.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if(h[0].equalsIgnoreCase("-f")){
				try {
					lcli.processCommandsFromFile(h);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (h.length > 1) {
				lcli.processCli(h);
			} else {
				String[] usage = {"java -jar logQL.jar     use to launch UI",
						"java -jar logQL.jar -c     use to launch CLI",
						"java -jar logQL.jar -o [fileName] -f [html|csv]  <query>     use to run query"};
				System.out.println("Usage: ");
				for(String s:usage){
					System.out.println(s);
				}
			}
		}
	}
}
