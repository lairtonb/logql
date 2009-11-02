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

    $Id: QueryFrame.java,v 1.3 2009/11/01 01:39:06 mreddy Exp $
*/
package com.logql.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.xml.sax.SAXException;

import com.logql.meta.Config;

public class QueryFrame extends JFrame {
	public static final long serialVersionUID = 1;
	public static final String DefaultConfigFile = "./samples/meta.xml";

	JTextField status;
	QueryTab qtab;
	JDialog desc;
	int tabCount = 1;
	
	public QueryFrame(){
		super("logQL");

		String[] cpath = { "./lib/jfreechart.jar",
				"./lib/poi-3.5-FINAL-20090928.jar",
				"./lib/poi-scratchpad-3.5-FINAL-20090928.jar",
				"./lib/poi-ooxml-3.5-FINAL-20090928.jar",
				"./lib/xmlbeans-2.3.0.jar",
				"./lib/ooxml-schemas-1.0.jar",
				"./lib/dom4j-1.6.1.jar",
				"./lib/geronimo-stax-api_1.0_spec-1.0.jar"};
		try {
			for (String lib : cpath) {
				File lpath = new File(lib);
				if (lpath.exists())
					addURL(lpath.toURI().toURL());
				else
					throw new NullPointerException("Missing file: "
							+ lpath.toString());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Unable to load libraries:"
					+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(3);
		}
	}

	private static final Class[] parameters = new Class[]{URL.class};

	public static void addURL(URL u) throws Exception {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		Method method = sysclass.getDeclaredMethod("addURL", parameters);
		method.setAccessible(true);
		method.invoke(sysloader, new Object[] { u });
	}

	public void init(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		status = new JTextField();
		status.setEditable(false);

		setIconImage(QueryTab.getImage("/images/logo_white.gif").getImage());

		getContentPane().setLayout(new BorderLayout());
		setSize(500, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JMenuBar mbar = new JMenuBar();
		JMenu fmenu = new JMenu("File");
		fmenu.setMnemonic('F');
		mbar.add(fmenu);

		JMenu oitem = new JMenu("Open");
		oitem.setMnemonic('O');
		ActionListener openAction = new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				open(ae.getActionCommand());
			}
		};
		JMenuItem eopen = new JMenuItem("Excel");
		eopen.setMnemonic('E');
		eopen.addActionListener(openAction);
		oitem.add(eopen);
		JMenuItem gopen = new JMenuItem("Custom");
		gopen.setMnemonic('u');
		gopen.addActionListener(openAction);
		oitem.add(gopen);
		JMenuItem copen = new JMenuItem("CSV");
		copen.setMnemonic('C');
		copen.addActionListener(openAction);
		oitem.add(copen);
		JMenuItem sopen = new JMenuItem("Delimited");
		sopen.setMnemonic('D');
		sopen.addActionListener(openAction);
		oitem.add(sopen);
		loadSampleConfig(oitem);
		fmenu.add(oitem);

		ActionListener saveAction = new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
				SaveTableModel stm = new SaveTableModel();
				stm.save(QueryFrame.this, qtab.getModel(), ae.getActionCommand());
				}catch(IOException ie){
					JOptionPane.showMessageDialog(QueryFrame.this, ie.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		JMenu save = new JMenu("Save As");
		save.setMnemonic('S');
		JMenuItem csave = new JMenuItem("CSV");
		csave.setMnemonic('C');
		csave.setActionCommand(SaveTableModel.CSV);
		csave.addActionListener(saveAction);
		save.add(csave);
		
		JMenuItem hsave = new JMenuItem("HTML");
		hsave.setMnemonic('H');
		hsave.setActionCommand(SaveTableModel.HTML);
		hsave.addActionListener(saveAction);
		save.add(hsave);
		
		fmenu.add(save);
		fmenu.addSeparator();
		
		JMenuItem desc = new JMenuItem("Describe");
		desc.setMnemonic('D');
		desc.setToolTipText("Show all columns");
		desc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				describe();
			}
		});
		fmenu.add(desc);
		fmenu.addSeparator();
		
		JCheckBoxMenuItem err = new JCheckBoxMenuItem("Error Lines");
		err.setMnemonic('E');
		err.setToolTipText("Show all the lines that were not read due to errors in the status bar");
		fmenu.add(err);
		fmenu.addSeparator();

		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic('x');
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});
		fmenu.add(exit);

		JMenu chartMenu = new JMenu("Chart");
		chartMenu.setMnemonic('C');
        ActionListener chartListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                qtab.chart(ae.getActionCommand());
            }
        };

        JMenuItem pie = new JMenuItem("Pie");
        pie.setMnemonic('P');
        pie.addActionListener(chartListener);
        chartMenu.add(pie);
        JMenuItem column = new JMenuItem("Column");
        column.setMnemonic('C');
        column.addActionListener(chartListener);
        chartMenu.add(column);
        JMenuItem bar = new JMenuItem("Bar");
        bar.setMnemonic('B');
        bar.addActionListener(chartListener);
        chartMenu.add(bar);
        JMenuItem area = new JMenuItem("Area");
        area.setMnemonic('A');
        area.addActionListener(chartListener);
        chartMenu.add(area);
        JMenuItem line = new JMenuItem("Line");
        line.setMnemonic('L');
        line.addActionListener(chartListener);
        chartMenu.add(line);

        mbar.add(chartMenu);

        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        JMenuItem quick = new JMenuItem("Quick Start");
        quick.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
        		helpQuickStart();
        	}
        });
        help.add(quick);

        JMenuItem meta = new JMenuItem("Meta Files");
        meta.setMnemonic('M');
        meta.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
        		helpMeta();
        	}
        });
        help.add(meta);

        JMenuItem query = new JMenuItem("Queries");
        query.setMnemonic('Q');
        query.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
        		helpQuery();
        	}
        });
        help.add(query);

        help.addSeparator();

		JMenuItem about = new JMenuItem("About");
		about.setMnemonic('A');
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				AboutDialog adiag=new AboutDialog(QueryFrame.this);
				adiag.setLocationRelativeTo(QueryFrame.this);
				adiag.setVisible(true);
			}
		});
		help.add(about);

        mbar.add(help);
		setJMenuBar(mbar);

		qtab = new QueryTab(this);
		qtab.init(getSize());
		qtab.setStatusBar(status);
		qtab.setDetailedError(err);
		getContentPane().add(qtab,BorderLayout.CENTER);
		getContentPane().add(status,BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	protected void loadSampleConfig(final JMenu fileMenu){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				fileMenu.addSeparator();
				File f = new File(DefaultConfigFile);
				if(f.exists()){
					try {
						Collection<String> configs = Config.load(DefaultConfigFile).getConfigNames();
						for(String name:configs){
							JMenuItem citem = new JMenuItem(name);
							citem.setActionCommand(name+"@"+DefaultConfigFile);
							fileMenu.add(citem);
							citem.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent ae){
									chooseCustom(ae.getActionCommand());
								}
							});
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void chooseCustom(String config){
		JFileChooser choose = new JFileChooser();
		choose.setMultiSelectionEnabled(true);
		
		if(choose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			File[] files = choose.getSelectedFiles();
			if (files.length > 0) {
				StringBuffer from = new StringBuffer();
				from.append("FROM ");
				from.append(files[0]);
				for(int i = 1; i < files.length;i++){
					from.append(",").append(files[i]);
				}
				from.append(" USE ").append(config);
				try {
					qtab.fireQuery(from.toString());
					describe();
					status.setText(from.toString());
					status.setCaretPosition(0);
				} catch (SQLException se) {
					JOptionPane.showMessageDialog(this, se.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public void helpQuery() {
		helpUrl("/docs/QueryManual.html", "Query help");
	}

	public void helpMeta() {
		helpUrl("/docs/MetaManual.html", "Meta help");
	}

	public void helpUrl(String url, String title) {
		URL loc = getClass().getResource(url);
		if (loc == null) {
			JOptionPane.showMessageDialog(this, "Invalid/corrupt package",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JEditorPane epane = new JEditorPane();
		epane.setEditable(false);
		try {
			epane.setPage(loc);
			showDialog(epane, title);
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(this, "Invalid/corrupt package",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void helpQuickStart(){
		URL loc = getClass().getResource("/docs/QuickStart.txt");
		if(loc == null){
			JOptionPane.showMessageDialog(this, "Invalid/corrupt package","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
			JTextArea area = new JTextArea();
			BufferedReader in =new BufferedReader(new InputStreamReader(loc.openStream()));
			String buff;
			while((buff=in.readLine())!=null){
				area.append(buff);
				area.append("\n");
			}
			in.close();
			area.setCaretPosition(0);
			showDialog(area, "Quick Start");
		}catch(IOException ie){
			JOptionPane.showMessageDialog(this, "Invalid/corrupt package","Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	public void showDialog(JComponent comp, String title){
		final JDialog jd = new JDialog(this,title);
		jd.getContentPane().setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(comp);
		jd.getContentPane().add(scroll,BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				jd.dispose();
			}
		});
		south.add(close);
		jd.getContentPane().add(south, BorderLayout.SOUTH);
		Point p = new Point(getLocation());
		p.x+=50;
		p.y+=50;
		jd.setLocation(p);
		jd.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		jd.setSize(600,500);
		jd.setVisible(true);
	}

	public void open(String actionCmd){
		OpenInterface ointerface = null;
		if (actionCmd.equals("Custom")) {
			ointerface = new OpenGeneralDialog(this);
		} else if (actionCmd.equals("CSV")) {
			ointerface = new OpenCSVDialog(this);
		} else if (actionCmd.equals("Delimited")) {
			ointerface = new OpenSepDialog(this);
		} else if (actionCmd.equals("Excel")) {
			ointerface = new OpenExcelDialog(this);
		}

		if(ointerface == null)
			return;

		ointerface.setLocationRelativeTo(this);
		ointerface.setVisible(true);
		while(ointerface.okClicked()){
			String from = ointerface.getFromClause();
			try{
				qtab.fireQuery(from);
				describe();
				status.setText(from);
				status.setCaretPosition(0);
				break;
			}catch(SQLException se){
				JOptionPane.showMessageDialog(this, se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				ointerface.setVisible(true);
				se.printStackTrace();
			}
		}
		ointerface.dispose();
	}

	public void describe(){
		if(desc != null && desc.isVisible()){
			desc.dispose();
		}
		String[] arr= null;
		try{
			arr = qtab.describe();
		}catch(SQLException se){
			JOptionPane.showMessageDialog(this, se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(arr == null)
			return;
		desc = new JDialog(this,"Describe",false);
		desc.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		desc.getContentPane().setLayout(new BorderLayout());
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		for(String r1:arr){
			String name = r1.substring(0,r1.indexOf("\t"));
			String type = r1.substring(r1.indexOf("\t")+1);
			Vector<String> row = new Vector<String>();
			row.add(name);
			row.add(type);
			rows.add(row);
		}
		Vector<String> header = new Vector<String>();
		header.add("Field name");
		header.add("Type");
		JTable tab = new JTable(rows, header);
		JScrollPane scrolls = new JScrollPane(tab);
		desc.getContentPane().add(scrolls,BorderLayout.CENTER);

		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton("Close");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				desc.dispose();
			}
		});
		pan.add(ok);
		desc.getContentPane().add(pan,BorderLayout.SOUTH);
		desc.setSize(200,300);
		desc.setLocationRelativeTo(this);
		desc.setVisible(true);
	}
}
