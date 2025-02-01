package com.bigboxer23.solar_moon.lambda.customer;

import com.bigboxer23.solar_moon.IComponentRegistry;
import com.bigboxer23.solar_moon.data.Customer;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

/** */
@Slf4j
public class TestCustomerDelete implements IComponentRegistry {

	public void deleteCustomer() {
		CustomerDelete customerDelete = new CustomerDelete();
		Customer customer = customerComponent.findCustomerByCustomerId("xxxx").orElse(null);
		if (customer == null) {
			return;
		}
		log.info(customer.getCustomerId() + " requested deletion.");
		try {
			customerDelete.removeStripeUser(customer);
		} catch (StripeException e) {
			log.warn("customerDelete:", e);
		}
		try {
			customerDelete.removeCognitoUser(customer);
		} catch (UserNotFoundException e) {
		}
		customerComponent.deleteCustomerByCustomerId(customer.getCustomerId());
	}
}
