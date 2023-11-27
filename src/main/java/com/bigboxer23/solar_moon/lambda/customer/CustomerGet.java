package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.*;

/** */
public class CustomerGet extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return new LambdaResponse(
				OK,
				moshi.adapter(Customer.class)
						.toJson(getCustomerFromRequest(request).orElse(null)),
				APPLICATION_JSON_VALUE);
	}
}
