package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import java.io.IOException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;

/** */
public class CustomerDelete extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		Customer customer = customerComponent.findCustomerByCustomerId(getCustomerIdFromRequest(request));
		logger.info(customer.getCustomerId() + " requested deletion.");
		try {
			removeStripeUser(customer);
		} catch (StripeException e) {
			logger.warn("customerDelete:", e);
			return new LambdaResponse(BAD_REQUEST, "Unable to delete account", APPLICATION_JSON_VALUE);
		}
		removeCognitoUser(customer);
		subscriptionComponent.deleteSubscription(customer.getCustomerId());
		customerComponent.deleteCustomerByCustomerId(customer.getCustomerId());
		return new LambdaResponse(OK, "Deleted user", APPLICATION_JSON_VALUE);
	}

	private void removeCognitoUser(Customer customer) {
		try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
				.region(Region.of(PropertyUtils.getProperty("aws.region")))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build()) {
			cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
					.userPoolId(PropertyUtils.getProperty("cognito.user.pool.id"))
					.username(customer.getCustomerId())
					.build());
		}
		logger.info(customer.getCustomerId() + " removed from cognito.");
	}

	private void removeStripeUser(Customer customer) throws StripeException {
		new StripeClient(PropertyUtils.getProperty("stripe.api.key"))
				.customers()
				.delete(customer.getStripeCustomerId());
		logger.info(customer.getCustomerId() + " : " + customer.getStripeCustomerId() + " deleted from stripe");
	}
}
