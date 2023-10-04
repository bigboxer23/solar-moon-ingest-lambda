package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.*;

/** */
public class CustomerGet extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String customerJSON = moshi.adapter(Customer.class)
				.toJson(customerComponent.findCustomerByCustomerId(
						request.getRequestContext().getAuthorizer().getClaims().getSub()));
		// TODO: error handling
		return new LambdaResponse(OK, customerJSON, APPLICATION_JSON_VALUE);
	}
}
