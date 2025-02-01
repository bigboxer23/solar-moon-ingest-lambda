package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.customer_creation.PostUserCreation;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.utils.properties.PropertyUtils;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;

/** */
@Slf4j
public class CustomerDelete extends MethodHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		Customer customer = customerComponent
				.findCustomerByCustomerId(getCustomerIdFromRequest(request))
				.orElse(null);
		if (customer == null) {
			return new LambdaResponse(BAD_REQUEST, "Cannot find customer", APPLICATION_JSON_VALUE);
		}
		log.info(customer.getCustomerId() + " requested deletion.");
		try {
			removeStripeUser(customer);
		} catch (StripeException e) {
			log.warn("customerDelete:", e);
		}
		removeCognitoUser(customer);
		customerComponent.deleteCustomerByCustomerId(customer.getCustomerId());
		PostUserCreation.updateFTPConfiguration();
		return new LambdaResponse(OK, "Deleted user", APPLICATION_JSON_VALUE);
	}

	protected void removeCognitoUser(Customer customer) {
		try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
				.region(Region.of(PropertyUtils.getProperty("aws.region")))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build()) {
			cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
					.userPoolId(PropertyUtils.getProperty("cognito.user.pool.id"))
					.username(customer.getCustomerId())
					.build());
		}
		log.info(customer.getCustomerId() + " removed from cognito.");
	}

	protected void removeStripeUser(Customer customer) throws StripeException {
		if (customer.getStripeCustomerId() == null) {
			log.warn(customer.getCustomerId() + " can't be deleted from stripe, no id");
			return;
		}
		new StripeClient(PropertyUtils.getProperty("stripe.api.key"))
				.customers()
				.delete(customer.getStripeCustomerId());
		log.info(customer.getCustomerId() + " : " + customer.getStripeCustomerId() + " deleted from stripe");
	}

	@Override
	public boolean isPricingRedirectEnabled(LambdaRequest request) {
		return false;
	}
}
