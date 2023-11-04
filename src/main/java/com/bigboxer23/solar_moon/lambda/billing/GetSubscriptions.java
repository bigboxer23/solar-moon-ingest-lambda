package com.bigboxer23.solar_moon.lambda.billing;

import com.bigboxer23.payments.StripeSubscriptionComponent;
import com.bigboxer23.payments.SubscriptionPriceInfo;
import com.bigboxer23.solar_moon.lambda.MethodHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.squareup.moshi.Types;
import com.stripe.exception.StripeException;
import java.io.IOException;
import java.util.List;

/** */
public class GetSubscriptions extends MethodHandler {
	private final StripeSubscriptionComponent component = new StripeSubscriptionComponent();

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		try {
			List<SubscriptionPriceInfo> info =
					component
							.getActiveSubscriptions(
									getCustomerFromRequest(request).getStripeCustomerId())
							.stream()
							.map(subscription -> new SubscriptionPriceInfo(
									subscription.getItems().getData().get(0).getQuantity(),
									subscription
											.getItems()
											.getData()
											.get(0)
											.getPlan()
											.getInterval()))
							.toList();
			return new LambdaResponse(
					OK,
					moshi.adapter(Types.newParameterizedType(List.class, SubscriptionPriceInfo.class))
							.toJson(info),
					APPLICATION_JSON_VALUE);
		} catch (StripeException e) {
			logger.warn("GetSubscriptions", e);
			return new LambdaResponse(BAD_REQUEST, "error retrieving subscriptions");
		}
	}
}
