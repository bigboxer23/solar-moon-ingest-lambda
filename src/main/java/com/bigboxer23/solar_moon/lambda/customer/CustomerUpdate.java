package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.*;
import java.util.Optional;

/** */
public class CustomerUpdate extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		Optional.ofNullable(moshi.adapter(Customer.class).fromJson(request.getBody()))
				.ifPresent(customer -> {
					logger.info("customer: " + customer);
					customer.setCustomerId(getCustomerIdFromRequest(request));
					customerComponent.updateCustomer(customer);
				});
		// TODO: error handling
		return new LambdaResponse(OK, "", APPLICATION_JSON_VALUE);
	}
}
