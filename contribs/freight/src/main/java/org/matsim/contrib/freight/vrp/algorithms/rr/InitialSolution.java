/* *********************************************************************** *
 * project: org.matsim.*
 * IniSolution.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package org.matsim.contrib.freight.vrp.algorithms.rr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.contrib.freight.vrp.algorithms.rr.recreation.BestInsertion;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.PickupAndDeliveryTourFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.RRTourAgent;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.RRTourAgentFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.TourCostAndTWProcessor;
import org.matsim.contrib.freight.vrp.algorithms.rr.tourAgents.TourStatusProcessor;
import org.matsim.contrib.freight.vrp.basics.InitialSolutionFactory;
import org.matsim.contrib.freight.vrp.basics.Job;
import org.matsim.contrib.freight.vrp.basics.Tour;
import org.matsim.contrib.freight.vrp.basics.Vehicle;
import org.matsim.contrib.freight.vrp.basics.VehicleRoutingProblem;
import org.matsim.contrib.freight.vrp.basics.VrpTourBuilder;

public class InitialSolution implements InitialSolutionFactory {

	private static Logger logger = Logger.getLogger(InitialSolution.class);
	
	@Override
	public RRSolution createInitialSolution(VehicleRoutingProblem vrp) {
		logger.info("create initial solution.");
		RRSolution solution = createEmptySolution(vrp);
		BestInsertion bestInsertion = new BestInsertion();
		bestInsertion.run(solution, getUnassignedJobs(vrp), Double.MAX_VALUE);
		return solution;
	}

	private List<Job> getUnassignedJobs(VehicleRoutingProblem vrp) {
		List<Job> jobs = new ArrayList<Job>(vrp.getJobs().values());
		return jobs;
	}

	private RRSolution createEmptySolution(VehicleRoutingProblem vrp) {
		Collection<RRTourAgent> emptyTours = new ArrayList<RRTourAgent>();
		TourStatusProcessor statusProcessor = new TourCostAndTWProcessor(vrp.getCosts());
		RRTourAgentFactory tourAgentFactory = new RRTourAgentFactory(statusProcessor, new PickupAndDeliveryTourFactory(vrp.getCosts(), 
				vrp.getGlobalConstraints(), statusProcessor), vrp.getCosts().getCostParams());
		for (Vehicle vehicle : vrp.getVehicles()) { 
			RRTourAgent tourAgent = createTourAgent(vehicle, vehicle.getLocationId(), tourAgentFactory);
			tourAgent.noFixedCosts = true;
			emptyTours.add(tourAgent);
		}
		return new RRSolution(emptyTours);
	}


	private RRTourAgent createTourAgent(Vehicle vehicle, String vehicleLocationId, RRTourAgentFactory tourAgentFactory) {
		VrpTourBuilder tourBuilder = new VrpTourBuilder();
		tourBuilder.scheduleStart(vehicleLocationId, vehicle.getEarliestDeparture(), Double.MAX_VALUE);
		tourBuilder.scheduleEnd(vehicleLocationId, 0.0, vehicle.getLatestArrival());
		Tour tour = tourBuilder.build();
		return tourAgentFactory.createTourAgent(tour, vehicle);
	}
	
}
