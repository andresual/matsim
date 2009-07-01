/* *********************************************************************** *
 * project: org.matsim.*
 * PopProviderServer.java
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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.matsim.api.basic.v01.Id;
import org.matsim.core.api.experimental.population.Population;
import org.matsim.core.api.network.Network;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.events.Events;
import org.matsim.core.mobsim.queuesim.QueueNetwork;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.population.PersonImpl;
import org.matsim.vis.otfvis.data.OTFClientQuad;
import org.matsim.vis.otfvis.data.OTFServerQuad;
import org.matsim.vis.otfvis.interfaces.OTFDrawer;
import org.matsim.vis.otfvis.interfaces.OTFQuery;

public class PopProviderServer implements PopulationProvider {

	public static class QueryImpl implements OTFQuery {
		public void draw(OTFDrawer drawer) {		}
		public Type getType() {			return Type.OTHER;		}
		public boolean isAlive() {			return false;		}
		public void query(QueueNetwork net, Population population, Events events,				OTFServerQuad quad) {		}
		public void remove() {		}
		public void setId(String id) {		}
	}

	public static class QueryIdSet extends QueryImpl {
		private SortedSet<Integer> idSet;
		
		public void query(QueueNetwork net, Population population, Events events,
				OTFServerQuad quad) {
			int max = population.getPersons().size();
			Set<Id> ids = population.getPersons().keySet();
			idSet = new TreeSet<Integer>();
			for(Id id : ids){
				int i = Integer.parseInt(id.toString());
				idSet.add(i);
			}
		}
	}

	public static class QueryPerson  extends QueryImpl {
		private PersonImpl person;
		int id;
		
		public QueryPerson(int id){
			this.id = id;
		}
		public void query(QueueNetwork net, Population population, Events events,
				OTFServerQuad quad) {
			person  = population.getPersons().get(new IdImpl(id));
		}
	}

	public static class QueryNet  extends QueryImpl {
		private Network net;
		public void query(QueueNetwork net, Population population, Events events,
				OTFServerQuad quad) {
			this.net = net.getNetworkLayer();
		}
	}

	public static class QueryIds  extends QueryImpl {
		private Set<Id> iId =null;
		public boolean fromNode=true;
		public QueryIds(boolean fromNode){this.fromNode = fromNode;}
		public void query(QueueNetwork net, Population population, Events events,
				OTFServerQuad quad) {
			this.iId = fromNode ? net.getNetworkLayer().getNodes().keySet() : net.getNetworkLayer().getLinks().keySet();
			this.iId = new HashSet(this.iId);


		}
	}

	public static class QueryOb  extends QueryImpl {
		public Id iId;
		public Object ob;
		public boolean fromNode=true;
		public QueryOb(Id id, boolean fromNode){this.iId = id;  this.fromNode = fromNode;}
		public void query(QueueNetwork net, Population population, Events events,
				OTFServerQuad quad) {
			this.ob = fromNode ? net.getNetworkLayer().getNodes().get(iId) : net.getNetworkLayer().getLinks().get(iId);
			//System.out.println(ob.toString());
		}
	}

	OTFClientQuad clientQ;
	Network netti = null;
	
	
	public PopProviderServer(final OTFClientQuad clientQ) {
		this.clientQ = clientQ;
		
		if(netti == null) {
			Set<Id> nodeIds = ((QueryIds)(clientQ.doQuery(new QueryIds(true)))).iId;
			for(Id id : nodeIds) {
				clientQ.doQuery(new QueryOb(id, true));
			};
//			Set<Id> linkIds = ((QueryIds)(clientQ.doQuery(new QueryIds(false)))).iId;
//			for(Id idl : linkIds) {
//				clientQ.doQuery(new QueryOb(idl, false));
//			};
			netti = ((QueryNet)(clientQ.doQuery(new QueryNet()))).net;

			
//			Runnable loader = new Runnable() {
//				synchronized public void run() {
//					netti = ((QueryNet)(clientQ.doQuery(new QueryNet()))).net;
//					this.notifyAll();
//				};
//			};
//			
////			netti = ((QueryNet)(clientQ.doQuery(new QueryNet()))).net;
//			new Thread(null,loader, "netLoader", 25*1024*1024).start();
//			try {
//				synchronized (loader) {
//					loader.wait();	
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public SortedSet<Integer> getIdSet() {
		QueryIdSet qid = (QueryIdSet) clientQ.doQuery(new QueryIdSet());
		return qid.idSet;
	}

	public PersonImpl getPerson(int id) {
		QueryPerson qid = (QueryPerson) clientQ.doQuery(new QueryPerson(id));
		return qid.person;
	}

}

