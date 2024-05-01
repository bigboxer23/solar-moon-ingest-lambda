package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.payments.StripeSubscriptionComponent;
import com.bigboxer23.payments.SubscriptionPriceInfo;
import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.squareup.moshi.Types;
import com.stripe.exception.StripeException;
import java.io.IOException;
import java.util.List;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class GetSubscriptions extends MethodHandler {
	private final StripeSubscriptionComponent component = new StripeSubscriptionComponent();

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		String stripeCustomerId = getCustomerFromRequest(request)
				.map(Customer::getStripeCustomerId)
				.orElse(null);
		if (StringUtils.isEmpty(stripeCustomerId)) {
			logger.warn("GetSubscriptions: no stripe customer Id");
			return new LambdaResponse(BAD_REQUEST, "error retrieving subscriptions");
		}
		try {
			return new LambdaResponse(
					OK,
					moshi.adapter(Types.newParameterizedType(List.class, SubscriptionPriceInfo.class))
							.toJson(component.getActiveSubscriptionPriceInfo(stripeCustomerId)),
					APPLICATION_JSON_VALUE);
		} catch (StripeException e) {
			logger.warn("GetSubscriptions", e);
			return new LambdaResponse(BAD_REQUEST, "error retrieving subscriptions");
		}
	}
}
