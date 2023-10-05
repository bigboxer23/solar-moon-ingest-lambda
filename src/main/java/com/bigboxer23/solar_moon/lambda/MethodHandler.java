package com.bigboxer23.solar_moon.lambda;

import com.bigboxer23.solar_moon.CustomerComponent;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.Transaction;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public abstract class MethodHandler implements MediaTypes, HttpStatus {
	protected static final Moshi moshi = new Moshi.Builder().build();

	protected static final Logger logger = LoggerFactory.getLogger(AbstractRequestStreamHandler.class);
	protected static final CustomerComponent customerComponent = new CustomerComponent();

	@Transaction
	public abstract LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException;

	public String getCustomerIdFromRequest(LambdaRequest request) {
		return request.getRequestContext().getAuthorizer().getClaims().getUsername();
	}
}
