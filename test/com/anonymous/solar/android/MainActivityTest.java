package com.anonymous.solar.android;

import com.anonymous.solar.android.*;
import com.anonymous.solar.shared.*;
import com.anonymous.*;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
	
	/**
	 * Basic test to ensure the testing harness works.
	 * @throws Exception
	 */
	@Test
    public void shouldHaveHappySmiles() throws Exception {
        String hello = new MainActivity().getResources().getString(R.string.app_name);
        assertThat(hello, equalTo("Solar Calculator for Android"));
    }
	
}
