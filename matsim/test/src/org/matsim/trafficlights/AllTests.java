/* *********************************************************************** *
 * project: org.matsim.*
 * KmlNetworkWriter.java
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
package org.matsim.trafficlights;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author dgrether
 *
 */
public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite("Test for org.matsim.trafficlights");
		//$JUnit-BEGIN$
		suite.addTest(org.matsim.trafficlights.data.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
