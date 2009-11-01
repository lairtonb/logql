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

    $Id: SaveTableModel.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class SaveTableModel {
	public static final String XML = "xml";
	public static final String HTML = "html";
	public static final String CSV = "csv";
	NumberFormat intFormatter = NumberFormat.getIntegerInstance();
	NumberFormat doubleFormatter = NumberFormat.getNumberInstance();
	private DecimalFormat nf = new DecimalFormat("##.##");
	public String title;
	public File outFile;
	ResultSetTable model;
	private final String _Style = "<style type=\"text/css\">\n" + "th {\n"
			+ "	color:#555580;\n" + "	background-color:#E4EADF;\n"
			+ "	font:bold 7pt/12pt Verdana, Arial, Helvetica, sans-serif;\n"
			+ "	vertical-align:top;\n" + "	text-align:left;\n"
			+ "	padding:1px 5px 3px 5px;\n"
			+ "	border-right:1px solid #CCCCDD;\n" + "	white-space:nowrap;\n"
			+ "}\n" + "td {\n" + "	color:#000000;\n"
			+ "	font:8pt/12pt Verdana, Arial, Helvetica, sans-serif;\n"
			+ "	vertical-align:top;\n" + "	padding:3px 5px 3px 5px;\n"
			+ "	border-right:1px solid #CCCCDD;\n"
			+ "	border-top:1px solid #555580;\n" + "}\n" + "table {\n"
			+ "	border-top:2px solid #CCCCDD;\n" + "	border-right:none;\n"
			+ "	border-bottom:1px solid #555580;\n"
			+ "	border-left:1px solid #CCCCDD;\n" + "}\n" + "b {"
			+ "	font:bold 16pt Verdana, Arial, Helvetica, sans-serif;" + "}"
			+ "table tr:hover td {\n" + "	background-color:#EEEEEE;\n"
			+ "	border-bottom-color:#EEEEEE;\n" + "}\n" + "</style>\n";

	public void save(JFrame frame, ResultSetTable atm, final String save) throws IOException
	{
		model = atm;
		JFileChooser fc;

		String home = System.getProperty("user.home");
		File directory = (home != null && home.length() > 0) ? new File(home) : null;
		fc = new JFileChooser(directory);
		fc.addChoosableFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;
				int dotAt = f.getName().indexOf('.');
				String extension = dotAt < 0 ? null : f.getName().substring(dotAt + 1);
				return (save.equals(extension));
			}

			public String getDescription()
			{
				return save + " files (*." + save + ")";
			}
		});

		String first = "snapshot";
//TODO
//			first = atm.gets;
		title = first;

		String defaultName = first + "." + save;
		for (int i = 1; ; i++)
		{
			File file = new File(directory, defaultName);
			if (file.exists() == false)
			{
				fc.setSelectedFile(file);
				break;
			}
			defaultName = "first" + i + "." + save;
		}

		while (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
		{
			outFile = fc.getSelectedFile();
			String fileName=outFile.getName();
			if(fileName.indexOf(".")==-1){
				String path=outFile.getAbsolutePath();
				path=path.substring(0,path.indexOf(fileName));
				outFile=new File(path,fileName+"."+save);
			}
			if(outFile.exists()){
				int opt = JOptionPane.showOptionDialog(frame, outFile.getName()
						+ " already exists, overwrite?", "Error", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (opt != JOptionPane.YES_OPTION)
					continue;
			}
			
			if (save.equals(XML))
				saveXML();
			else if (save.equals(HTML))
				saveHTML();
			else if (save.equals(CSV))
				saveCSV();

			break;
		}

	}

 public void saveXML() throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		try
		{
			ArrayList<String> header = new ArrayList<String>();
			for (int i = 0; i < model.getColumnCount(); i++)
			{
                String str = model.getColumnName(i);
				str = str.replaceAll(" ", "-");
				str = str.replaceAll(",", "-");
				str = str.replaceAll("-+", "-");
				header.add(str);
			}
			out.write("<xmlExport title=\"" + title + "\" count=\"" + model.getRowCount() + "\">\n");
			for (int row = 0; row < model.getRowCount(); row++)
			{
				out.write("\t<row>\n");
				for (int col = 0; col < header.size(); col++)
				{
					out.write("\t\t<");
					out.write((String) header.get(col));
					out.write(">");
					Object o = model.getValueAt(row, col);
					if (o instanceof Double)
						out.write(doubleFormatter.format(o));
					else
					{
						String wr = o == null ? "" : o.toString();
						out.write(wr);
					}
					out.write("</");
					out.write((String) header.get(col));
					out.write(">\n");
				}
				out.write("\t</row>\n");
			}
			out.write("</xmlExport>");
		}
		finally
		{
			out.close();

		}
	}

	public void saveHTML() throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		try
		{
			out.write("<HTML><HEAD><TITLE>logQL report</TITLE>");
			out.write(_Style);
			out.write("</HEAD><BODY>");
			out.write("<TABLE border=\"0\" cellpadding=\"0\"");
			out.write(" cellspacing=\"0\" width=\"100%\"><TR>");
			for (int i = 0; i < model.getColumnCount(); i++)
				out.write("<TH>" + model.getColumnName(i) + "</TH>");
			out.write("</TR>");

			for (int row = 0; row < model.getRowCount(); row++)
			{
				out.write("<TR>");
				for (int col = 0; col < model.getColumnCount(); col++)
				{
					out.write("<TD>");
					Object o = model.getValueAt(row, col);
					if (o instanceof Double)
						out.write(nf.format(o));
					else
					{
						String wr = o == null ? "" : o.toString();
						out.write(wr);
					}	
						out.write("</TD>");
				}
				out.write("</TR>");
			}
			out.write("</TABLE></BODY></HTML>");
		}
		finally
		{
			out.close();
		}
	}

	public void saveCSV() throws IOException
	{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		try
		{
			for (int i = 0; i < model.getColumnCount(); i++)
			{
				String columnName = model.getColumnName(i);
				if (columnName.indexOf(",") != -1)
					columnName = "\"" + columnName + "\"";
				out.write(columnName + ",");
			}

			out.write("\n");
			for (int row = 0; row < model.getRowCount(); row++)
			{
				for (int col = 0; col < model.getColumnCount(); col++)
				{
					Object o = model.getValueAt(row, col);
					String wr = "";
					if (o == null)
					{
						wr = "\"\"";
					}
					else
					{
						if (o instanceof Double)
							wr = nf.format(o);
						else
							wr = o.toString();
						
						if (wr.indexOf(",") != -1)
						{
							wr.replaceAll("\"", "\"\"");
							wr = "\"" + wr + "\"";
						}
					}
					out.write(wr + ",");
				}
				out.write("\n");
			}
		}
		finally
		{
			out.close();
		}
	}
}
