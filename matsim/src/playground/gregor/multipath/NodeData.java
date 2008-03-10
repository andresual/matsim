/* *********************************************************************** *
 * project: org.matsim.*
 * NodeData.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package playground.gregor.multipath;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.matsim.gbl.Gbl;
import org.matsim.network.Node;
import org.matsim.utils.identifiers.IdI;
import org.matsim.utils.vis.routervis.RouterVis;

public class NodeData {

	private static final Logger log = Logger.getLogger(NodeData.class);
	
	private final static double BETA = 4;
	private final static double THETA = 1;

	private final Node matSimNode;
	private double cost = Double.MAX_VALUE;
	private double sortCost = Double.MAX_VALUE;

	private double time = 0;
	private int iterationID = Integer.MIN_VALUE;

	private double trace = 0;

	private HashSet<Node> fromMatSimNodes;
//
//	private List<NodeData> forwardNodes;
//	private ConcurrentLinkedQueue<NodeData> backwardNodes;

	private int inPaths = 0;

	private ConcurrentLinkedQueue<NodeData> shadowNodes;

	private ConcurrentLinkedQueue<NodeDataLink> forwardLinks;
	private ConcurrentLinkedQueue<NodeDataLink> backwardLinks;
	private NodeData currentShadow = null;
	private boolean isHead;

	
	
//	private double nodeProb = 0;

//	private double backLinkWeightsSum;

//	private double nodeProbDiff = 0;

	private  BeelineDifferenceTracer tracer;

	private boolean isShadow = false;

	public NodeData(Node n, final BeelineDifferenceTracer t) {
		this.matSimNode = n;
		this.fromMatSimNodes = new HashSet<Node>();
//		this.forwardNodes = new LinkedList<NodeData>();
//		this.backwardNodes = new ConcurrentLinkedQueue<NodeData>();
		this.shadowNodes = new ConcurrentLinkedQueue<NodeData>();
		this.backwardLinks = new ConcurrentLinkedQueue<NodeDataLink>();
		this.forwardLinks = new ConcurrentLinkedQueue<NodeDataLink>();
		this.inPaths = 0;
//		this.backLinkWeightsSum = 0;
		this.tracer = t;

	}

	public ConcurrentLinkedQueue<NodeData> getShadowNodes(){
		return this.shadowNodes;
	}

	public void addShadowNode(NodeData o){
		this.shadowNodes.add(o);
		this.currentShadow  = o;
	}

	public int getNumShadow(){
		return this.shadowNodes.size();
	}
	public NodeData getCurrShadow() {
		return this.currentShadow;
	}
	
	public void markAsShadow(){
		this.isShadow = true;
	}
	
	public boolean isShadow() {
		return this.isShadow;
	}


	public Node getMatsimNode(){
		return matSimNode;
	}

	public double getCost(){
		return this.cost;
	}

	public double getSortCost() {
		return this.sortCost;
	}

	public double getTime(){
		return this.time;
	}
	
	public double getTrace(){
		return this.trace;
	}

	public boolean isUTurn(Node n){
		return this.fromMatSimNodes.contains(n);
	}

	public IdI getId(){
		return matSimNode.getId();
	}

	public int getInPaths(){
		int inPaths = 0;
		for (NodeData shadow : this.shadowNodes){
			inPaths += shadow.inPaths;
		}
		
		return this.inPaths;
	}


	public boolean isVisited(int iterID) {
		return (iterID == this.iterationID);
	}
	public boolean isHead() {
		return this.isHead;
	}
	public void setHead(boolean flag){
		this.isHead = flag;
	}


//	public ConcurrentLinkedQueue<NodeData> getBackNodesData(){
//		return this.backwardNodes;
//	}



	public void resetVisited(final BeelineDifferenceTracer tracer) {
		this.fromMatSimNodes.clear();
//		this.backwardNodes.clear();
		this.shadowNodes.clear();
		this.backwardLinks.clear();
		this.forwardLinks.clear();
		this.inPaths = 0;
//		this.backLinkWeightsSum = 0;
		this.tracer = tracer;
		
		
		this.iterationID = Integer.MIN_VALUE;

		this.cost = Double.MAX_VALUE;

	}

//	public void addForwardNodeData(NodeData node){
//		this.forwardNodes.add(node);
//	}

	public ConcurrentLinkedQueue<NodeDataLink> getForwardLinks(){
		return this.forwardLinks;
	}

	public ConcurrentLinkedQueue<NodeDataLink> getBackwardLinks(){
		return this.backwardLinks;
	}
	

	public void visitInitNode(double startTime, int iterationID) {

			this.cost = 0;
			this.sortCost = 0;
			this.time = startTime;
			this.iterationID = iterationID;
			this.inPaths = 1;
			NodeDataLink blink = new NodeDataLink();
//			blink.linkWeight = 1;
			backwardLinks.add(blink);

	}

//	private void calcBackLinkProbs(NodeDataLink link){
//
//		double overlap = getOverlap(link.paths,link.fromNode.getCost(),link.linkCost);
//		double linkLikelihood = Math.exp(- THETA * (link.linkCost + overlap));
//		double tmp = 0;
//		for (NodeDataLink bl : link.fromNode.backLinks){
//			tmp += bl.linkWeight;
//		}
//		link.linkWeight = linkLikelihood * tmp;
//		this.backLinkWeightsSum += link.linkWeight;
//
//	}

//	private double getOverlap(int inPaths, double prevCost, double linkCost) {
//		return BETA * Math.log(inPaths) * linkCost / (linkCost + prevCost);
//	}
//
//	public void computeProbs() {
//
//		for (NodeDataLink link : this.backLinks){
//				link.fromNode.addNodeProb((link.linkWeight / this.backLinkWeightsSum) * this.nodeProb);
//			}
//
//	}

	public void setSortCost(double sortCost){
		this.sortCost = Math.max(this.sortCost,sortCost);
	}

//public void addNodeProb(double prob) {
//		if (this.nodeProb > 0){
//			for (NodeDataLink link : this.backLinks){
//				try {
//					link.fromNode.propagateNodeProb(this.nodeProb);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//			this.nodeProb -= this.nodeProbDiff;
//			this.nodeProbDiff = 0;
//		}
//
//		this.nodeProb += prob;
//
//	}

//	private void propagateNodeProb(double prob) {
//		this.nodeProbDiff = prob;
//}

//	//	TODO DEBUG
//	public double getNodeProb(Node node) {
//		for (NodeDataLink link : this.backLinks){
//			if (link.fromNode.getMatsimNode().getId() == node.getId()){
//				return link.fromNode.nodeProb;
//			}
//		}
//		return -1;
//
//	}

	public void decoupleNode(NodeData node){
		for (NodeDataLink link : this.backwardLinks){
			if (link.fromNode.getMatsimNode().getId() == node.getMatsimNode().getId()) {
				decoupleLink(link);
				return;
			}

		}
	}

	private void decoupleLink(NodeDataLink link){
//		this.backLinkWeightsSum -= link.linkWeight;
		this.inPaths -= link.paths;
//		this.backwardNodes.remove(link.fromNode);
		this.backwardLinks.remove(link);
		this.fromMatSimNodes.remove(link.fromNode.getMatsimNode());

	}

	public boolean visit(NodeData fromNodeData, double cost, double time, int iterationID, double trace) {

		
		checkTrace(trace,cost);
//		if (!checkTrace(trace,cost)){
//			NodeDataLink backLink = new NodeDataLink();
//			backLink.cost = cost;
//			backLink.linkCost = cost - fromNodeData.getCost();
//			backLink.fromNode = fromNodeData;
////			backLink.toNode = this;
//			backLink.trace = trace;
////			backLink.linkTime = time - fromNodeData.getTime();
//			fromNodeData.forwardLinks.add(backLink);
//			return false;
//		}
		fromNodeData.setHead(false);
		this.setHead(true);

		if (this.cost > cost){
			this.cost = cost;
			this.time = time;
			this.trace = trace;
			this.iterationID = iterationID;
		}

		this.inPaths += fromNodeData.getInPaths();

		this.fromMatSimNodes.add(fromNodeData.getMatsimNode());
//		this.backwardNodes.add(fromNodeData);
//		fromNodeData.addForwardNodeData(this);

		NodeDataLink backLink = new NodeDataLink();
		backLink.paths = fromNodeData.getInPaths();
		backLink.cost = cost;
		backLink.linkCost = cost - fromNodeData.getCost();
		backLink.fromNode = fromNodeData;
//		backLink.toNode = this;
		backLink.trace = trace;
//		backLink.linkTime = time - fromNodeData.getTime();
//		backLink.linkWeight = 0;
//		calcBackLinkProbs(backLink);
		this.backwardLinks.add(backLink);
		fromNodeData.forwardLinks.add(backLink);

		this.sortCost = this.cost;

		return true;
	}



//	public void createForwardLinks(NodeData toNodeData, double linkCost, double linkTime, double trace){
//		NodeDataLink forwardLink = new NodeDataLink();
//		forwardLink.linkCost = linkCost;
//		forwardLink.toNode = toNodeData;
//		forwardLink.linkTime = linkTime;
//		forwardLink.trace = trace;
//		this.forwardLinks.add(forwardLink);
//
//	}
	
	
	public NodeData drawNode(){
//		double selnum = this.backLinkWeightsSum * Gbl.random.nextDouble();
		for (NodeDataLink link : this.backwardLinks){
			log.fatal("drawNode() is not implemented yet ... !!!!");
			return link.fromNode;
//			selnum -= link.linkWeight;
//			if (selnum <= 0) {
////				cost += link.linkCost;
//				return link.fromNode;
//			}
		}
		return null;
	}	

	private void checkTrace(double trace, double cost) {
		for (NodeDataLink link : this.backwardLinks){
			if (!tracer.tracesDiffer(trace, link.trace)){
				decoupleLink(link);
			}
		}
//		return true;
	}

	class NodeDataLink{
//		public double linkWeight;
		NodeData toNode;
		NodeData fromNode;
		int paths;
		double cost;
		double linkCost;
		double linkTime;
		double trace;
	}

	public static class ComparatorNodeData implements Comparator<NodeData>, Serializable {

		private static final long serialVersionUID = 1L;

		private boolean checkIDs = false;

		protected Map<IdI, ? extends NodeData> nodeData;

		public ComparatorNodeData(Map<IdI, ? extends NodeData> nodeData) {
			this.nodeData = nodeData;
		}

		public int compare(NodeData n1, NodeData n2) {
			double c1 = getKey(n1); // if a node
			double c2 = getKey(n2);

			return compare(n1, c1, n2, c2);
		}

		private int compare(NodeData n1, double c1, NodeData n2, double c2) {
			if (c1 < c2) {
				return -1;
			} else if (c1 == c2) {
				if (this.checkIDs) {
					return n1.getMatsimNode().compareTo(n2.getMatsimNode());
				}
				return 0;
			} else {
				return +1;
			}
		}

		public void setCheckIDs(boolean flag) {
			this.checkIDs = flag;
		}

		public double getKey(NodeData node) {
			return node.getSortCost();
		}
	}












}
