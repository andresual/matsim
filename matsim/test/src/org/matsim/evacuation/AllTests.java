/* *********************************************************************** *
 * project: org.matsim.*
 * AllTests.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.evacuation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.matsim.evacuation");
		//$JUnit-BEGIN$
		suite.addTest(org.matsim.evacuation.base.AllTests.suite());
		suite.addTest(org.matsim.evacuation.riskaversion.AllTests.suite());
		suite.addTest(org.matsim.evacuation.run.AllTests.suite());
		suite.addTest(org.matsim.evacuation.socialcost.AllTests.suite());
		//$JUnit-END$
		return suite;
	}


}
