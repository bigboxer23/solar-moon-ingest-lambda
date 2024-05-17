package com.bigboxer23.solar_moon.lambda.customer_creation;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.CognitoCommon;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import com.bigboxer23.utils.properties.PropertyUtils;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/** */
public class PostUserCreation extends AbstractRequestStreamHandler {

	private static final String queueUrl = PropertyUtils.getProperty("ftp.update.sqs.url");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		// NOOP b/c we just want to feed the raw data back
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		TransactionUtil.updateServiceCalled(getClass().getSimpleName());
		logger.info("post user creation start");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			String rawRequest = IOUtils.toString(reader);
			logger.debug(rawRequest);
			Optional.ofNullable(moshi.adapter(CognitoCommon.class).fromJson(rawRequest))
					.ifPresent(request -> {
						String email = request.getRequest().getUserAttributes().getEmail();
						if (customerComponent.findCustomerByEmail(email).isPresent()) {
							logger.warn(email + " found, not creating new user.");
							return;
						}
						try {
							logger.info("Creating stripe customer for " + email);
							Customer stripeCustomer = new StripeClient(PropertyUtils.getProperty("stripe.api.key"))
									.customers()
									.create(new CustomerCreateParams.Builder()
											.setEmail(email)
											.setName(request.getRequest()
													.getUserAttributes()
													.getName())
											.build());
							logger.info("new stripe customer id: " + stripeCustomer.getId());
							Optional<com.bigboxer23.solar_moon.data.Customer> customer = customerComponent.addCustomer(
									email,
									request.getRequest().getUserAttributes().getSub(),
									request.getRequest().getUserAttributes().getName(),
									stripeCustomer.getId());
							logger.info("post user creation");
							if (customer.isEmpty()) {
								logger.warn("Adding customer failed");
								return;
							}
							TransactionUtil.updateCustomerId(customer.get().getCustomerId());
							logger.warn(
									"adding subscription for " + customer.get().getCustomerId());
							subscriptionComponent.updateSubscription(
									customer.get().getCustomerId(), 0);
							updateFTPConfiguration();
						} catch (StripeException e) {
							logger.warn("Cannot create stripe user " + email, e);
						}
					});
			writer.write(rawRequest);
		} catch (Exception e) {
			logger.error("post user creation: ", e);
		}
		logger.info("post user creation end");
		after();
	}

	public static void updateFTPConfiguration() {
		logger.info("updating FTP configuration");
		try (SqsClient sqs = SqsClient.create()) {
			logger.debug("sending request for FTP config update to SQS");
			sqs.sendMessage(SendMessageRequest.builder()
					.queueUrl(queueUrl)
					.messageBody("update ftp config")
					.build());
			logger.info("FTP config update scheduled");
		}
	}
}
