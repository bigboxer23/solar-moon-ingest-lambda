package com.bigboxer23.solar_moon.lambda.ingest;

import static org.junit.jupiter.api.Assertions.*;

import com.bigboxer23.solar_moon.IComponentRegistry;
import com.bigboxer23.solar_moon.TestConstants;
import com.bigboxer23.solar_moon.data.Customer;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/** */
public class TestFTPUpload implements IComponentRegistry, TestConstants {
	private final FTPUpload ftpUpload = new FTPUpload();

	@Test
	public void getContent() {
		ResponseBytes<GetObjectResponse> objectBytes = ftpUpload
				.getS3Client()
				.getObjectAsBytes(GetObjectRequest.builder()
						.key("test/204500.zip")
						.bucket("solarmoonanalytics")
						.build());
		assertNotNull(ftpUpload.getContent(objectBytes.asByteArray()));
	}

	@Test
	public void getCustomerFromPath() {
		Customer customer =
				customerComponent.findCustomerByCustomerId(CUSTOMER_ID).orElse(null);
		assertNotNull(customer);

		assertFalse(ftpUpload.getCustomerFromPath(null).isPresent());
		assertFalse(ftpUpload.getCustomerFromPath("").isPresent());
		assertFalse(ftpUpload.getCustomerFromPath("null").isPresent());
		assertFalse(ftpUpload.getCustomerFromPath(customer.getAccessKey()).isPresent());
		assertFalse(ftpUpload
				.getCustomerFromPath("/" + customer.getAccessKey() + "/")
				.isPresent());
		assertTrue(ftpUpload.getCustomerFromPath(customer.getAccessKey() + "/").isPresent());
		assertTrue(ftpUpload
				.getCustomerFromPath(customer.getAccessKey() + "/blah/blah")
				.isPresent());
		assertFalse(ftpUpload
				.getCustomerFromPath(customer.getAccessKey() + "invalid/blah/blah")
				.isPresent());
	}
}
