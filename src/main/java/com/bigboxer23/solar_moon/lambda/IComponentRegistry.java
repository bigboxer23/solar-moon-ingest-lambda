package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.CustomerComponent;
import com.bigboxer23.solar_moon.DeviceComponent;
import com.squareup.moshi.Moshi;

/** */
public interface IComponentRegistry {
	Moshi moshi = new Moshi.Builder().build();
	CustomerComponent customerComponent = new CustomerComponent();
	DeviceComponent deviceComponent = new DeviceComponent();
}
