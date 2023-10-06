package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.CustomerComponent;
import com.bigboxer23.solar_moon.DeviceComponent;
import com.squareup.moshi.Moshi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public interface IComponentRegistry {
	Moshi moshi = new Moshi.Builder().build();
	CustomerComponent customerComponent = new CustomerComponent();
	DeviceComponent deviceComponent = new DeviceComponent();

	Logger logger = LoggerFactory.getLogger(IComponentRegistry.class);
}
