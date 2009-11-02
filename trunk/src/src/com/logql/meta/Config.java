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

    $Id: Config.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.logql.meta.std.CSVMeta;
import com.logql.meta.std.SepMeta;
import com.logql.meta.std.StdMeta;
import com.logql.meta.xl.XLMeta;

public class Config {
	private static final String DEFAULT_CONFIG_FILE="config.xml"; 

	private static Logger log=Logger.getLogger(Config.class.getName());

	protected String defaultConfig;
	protected HashMap<String, LogMeta> metas=new HashMap<String, LogMeta>();
	private static Config conf;

	private Config(){}

	public static Config getInstance(){
		if(conf==null){
			conf=new Config();
			try{
				conf = load(DEFAULT_CONFIG_FILE);
			}catch(Exception ie){
//				log.log(Level.SEVERE,"Error loading default config",ie);
			}
		}
		return conf;
	}

	public static Config load(String filePath) throws IOException, SAXException{
		Config ret = new Config();
		File f=new File(filePath);
		if(!f.exists())
			throw new FileNotFoundException(f.toString());
		try{
			Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(f));
			Node root=doc.getChildNodes().item(0);
			if(!root.getNodeName().equals("logQLConfig"))
				throw new SAXException("Root logQLConfig not found");
			NodeList nl=root.getChildNodes();
			for(int i=0;i<nl.getLength();i++){
				Node nd=nl.item(i);
				if(nd.getNodeType()==Node.COMMENT_NODE||nd.getNodeType()==Node.TEXT_NODE)
					continue;
				
				LogMeta req = null;
				if (nd.getNodeName().equals("defaultConfig")) {
					ret.defaultConfig = nd.getAttributes().getNamedItem("name")
							.getNodeValue();
				} else if (nd.getNodeName().equals("stdConfig")) {
					req = new StdMeta();
				} else if (nd.getNodeName().equals("csvConfig")) {
					req = new CSVMeta();
				} else if (nd.getNodeName().equals("sepConfig")) {
					req = new SepMeta();
				} else if (nd.getNodeName().equals("xlConfig")) {
					req = new XLMeta();
				}
				if (req != null) {
					req.readConfig(nd);
					if (ret.metas.containsKey(req.name))
						throw new IllegalArgumentException(
								"Configuration name repeats: " + req.name);
					ret.metas.put(req.name, req);
				}
			}
			return ret;
		}catch(ParserConfigurationException pce){
			log.log(Level.SEVERE,"Error parsing file: "+filePath,pce);
		}
		return null;
	}
	public LogMeta getConfig() {
		if (defaultConfig == null && metas.size() == 1)
			return metas.values().iterator().next();
		return getConfig(defaultConfig);
	}

	public LogMeta getDefaultConfig() {
		if (defaultConfig != null && defaultConfig.length() > 0)
			return getConfig(defaultConfig);
		if (metas.size() == 1)
			return metas.values().iterator().next();
		return null;
	}

	public LogMeta getConfig(String name) {
		return metas.get(name);
	}
	
	public Collection<String> getConfigNames(){
		return metas.keySet();
	}
}
