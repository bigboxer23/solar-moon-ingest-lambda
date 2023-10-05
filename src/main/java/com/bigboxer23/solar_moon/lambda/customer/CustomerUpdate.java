package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.*;
import java.util.Optional;

/** */
public class CustomerUpdate extends CustomerGet {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		Optional.ofNullable(moshi.adapter(Customer.class).fromJson(request.getBody()))
				.ifPresent(customer -> {
					customer.setCustomerId(getCustomerIdFromRequest(request));
					customerComponent.updateCustomer(customer);
				});
		return super.handleLambdaRequest(request);
	}
}
