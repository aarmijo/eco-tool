package com.tecnalia.wicket;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import com.tecnalia.wicket.pages.ecotool.EcoToolApplication;

/**
 * Homepage rendering test using the WicketTester
 */
public class TestHomePage
{
	private WicketTester tester;

	@Before
	public void setUp()
	{
		tester = new WicketTester(new EcoToolApplication());
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		//start and render the test page
		tester.startPage(com.tecnalia.wicket.pages.ecotool.HomePage.class);

		//assert rendered page class
		tester.assertRenderedPage(com.tecnalia.wicket.pages.ecotool.HomePage.class);		
	}
}
