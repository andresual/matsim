/* *********************************************************************** *
 * project: org.matsim.*
 * PopProviderFile.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.david.otfvis.prefuse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.matsim.api.basic.v01.Id;
import org.matsim.core.api.experimental.population.Population;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.population.PersonImpl;
import org.matsim.core.population.PopulationImpl;
import org.matsim.vis.otfvis.server.OTFQuadFileHandler;


public class PopProviderFile implements PopulationProvider {

	String filename;
	Population population;
	NetworkLayer network;
	
	public PopProviderFile(String filename) {
		this.filename = filename;
		BufferedInputStream fin;
		try {
			File sourceZipFile = new File(filename);
			// Open Zip file for reading
			ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			ZipEntry infoEntry = zipFile.getEntry("net+population.bin");
			BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(infoEntry),5000000);
			ObjectInputStream inFile = new OTFQuadFileHandler.Reader.OTFObjectInputStream(is);

			network = (NetworkLayer)inFile.readObject();
			population = (PopulationImpl)inFile.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SortedSet<Integer> getIdSet() {
		int max = population.getPersons().size();
		Set<Id> ids = population.getPersons().keySet();
		SortedSet<Integer> idSet = new TreeSet<Integer>();
		for(Id id : ids){
			int i = Integer.parseInt(id.toString());
			idSet.add(i);
		}
		max = idSet.last();
		return idSet;
	}

	public PersonImpl getPerson(int id) {
		PersonImpl p = population.getPersons().get(new IdImpl(id));
		return p;
	}

}

