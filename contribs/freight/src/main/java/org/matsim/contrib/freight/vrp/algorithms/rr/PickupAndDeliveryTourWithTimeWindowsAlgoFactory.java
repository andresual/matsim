package org.matsim.contrib.freight.vrp.algorithms.rr;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.matsim.contrib.freight.vrp.algorithms.rr.recreation.BestInsertion;
import org.matsim.contrib.freight.vrp.algorithms.rr.ruin.AvgBeelineDistanceBetweenJobs;
import org.matsim.contrib.freight.vrp.algorithms.rr.ruin.RadialRuin;
import org.matsim.contrib.freight.vrp.algorithms.rr.ruin.RandomRuin;
import org.matsim.contrib.freight.vrp.algorithms.rr.thresholdFunctions.SchrimpfsRRThresholdFunction;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.PickupAndDeliveryTourFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.RRTourAgentFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.TourCostAndTWProcessor;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.TourFactory;
import org.matsim.contrib.freight.vrp.basics.TourPlan;
import org.matsim.contrib.freight.vrp.basics.VehicleRoutingProblem;


public class PickupAndDeliveryTourWithTimeWindowsAlgoFactory implements RuinAndRecreateFactory{

	private static Logger logger = Logger.getLogger(PickupAndDeliveryTourWithTimeWindowsAlgoFactory.class);
	
	private Collection<RuinAndRecreateListener> ruinAndRecreationListeners = new ArrayList<RuinAndRecreateListener>();

	private int warmUp = 10;
	
	private int iterations = 50;
	
	public PickupAndDeliveryTourWithTimeWindowsAlgoFactory(int warmup, int iterations) {
		this.warmUp = warmup;
		this.iterations = iterations;
	}

	public PickupAndDeliveryTourWithTimeWindowsAlgoFactory() {
		super();
	}

	public void addRuinAndRecreateListener(RuinAndRecreateListener l){
		ruinAndRecreationListeners.add(l);
	}
	

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	@Override
	public RuinAndRecreate createAlgorithm(VehicleRoutingProblem vrp, RRSolution initialSolution) {
		TourCostAndTWProcessor tourCostProcessor = new TourCostAndTWProcessor(vrp.getCosts());
		TourFactory tourFactory = new PickupAndDeliveryTourFactory(vrp.getCosts(), vrp.getGlobalConstraints(), tourCostProcessor);
		RRTourAgentFactory tourAgentFactory = new RRTourAgentFactory(tourCostProcessor,tourFactory, vrp.getCosts().getCostParams());
		RuinAndRecreate ruinAndRecreateAlgo = new RuinAndRecreate(vrp, initialSolution, iterations);
		ruinAndRecreateAlgo.setWarmUpIterations(warmUp);
		ruinAndRecreateAlgo.setTourAgentFactory(tourAgentFactory);
		ruinAndRecreateAlgo.setRuinStrategyManager(new RuinStrategyManager());
		
		BestInsertion recreationStrategy = new BestInsertion();
		ruinAndRecreateAlgo.setRecreationStrategy(recreationStrategy);
		
		RadialRuin radialRuin = new RadialRuin(vrp, new AvgBeelineDistanceBetweenJobs(vrp.getLocations()));
		radialRuin.setFractionOfAllNodes(0.3);
		
		RandomRuin randomRuin = new RandomRuin(vrp);
		randomRuin.setFractionOfAllNodes2beRuined(0.5);
		
		ruinAndRecreateAlgo.getRuinStrategyManager().addStrategy(radialRuin, 0.5);
		ruinAndRecreateAlgo.getRuinStrategyManager().addStrategy(randomRuin, 0.5);
		ruinAndRecreateAlgo.setThresholdFunction(new SchrimpfsRRThresholdFunction(0.1));
		
		for(RuinAndRecreateListener l : ruinAndRecreationListeners){
			ruinAndRecreateAlgo.getListeners().add(l);
		}
		
		return ruinAndRecreateAlgo;
	}
	
	@Override
	public void setWarmUp(int nOfWarmUpIterations) {
		this.warmUp = nOfWarmUpIterations;
		
	}

	@Override
	public RuinAndRecreate createAlgorithm(VehicleRoutingProblem vrp,
			TourPlan initialSolution) {
		// TODO Auto-generated method stub
		return null;
	}

}
