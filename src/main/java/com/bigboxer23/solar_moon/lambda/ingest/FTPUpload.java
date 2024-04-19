package com.bigboxer23.solar_moon.lambda.ingest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.bigboxer23.solar_moon.IComponentRegistry;
import com.bigboxer23.solar_moon.data.Customer;
import com.bigboxer23.solar_moon.lambda.AbstractLambdaHandler;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.input.BOMInputStream;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class FTPUpload extends AbstractLambdaHandler implements RequestHandler<S3Event, String> {

	@Override
	public String handleRequest(S3Event event, Context context) {
		TransactionUtil.updateServiceCalled(getClass().getSimpleName());
		S3EventNotification.S3EventNotificationRecord record =
				event.getRecords().getFirst();
		String bucket = record.getS3().getBucket().getName();
		String key = record.getS3().getObject().getUrlDecodedKey();
		logger.debug("s3 event triggered " + bucket + " " + key);

		// Get customer id from access key path, delete file and return if invalid
		Optional<Customer> customer = getCustomerFromPath(key);
		if (customer.isEmpty()) {
			logger.error("bad key, deleting and returning " + key);
			delete(bucket, key);
			return null;
		}
		String fileName = key.replace(customer.get().getAccessKey() + "/XML/", "");
		TransactionUtil.updateCustomerId(customer.get().getCustomerId());
		if (key.endsWith("/")) {
			logger.warn("folder creation " + fileName);
			return null;
		}
		if (!key.toLowerCase().endsWith(".zip")) {
			logger.error("not zip file, deleting and returning " + fileName);
			delete(bucket, key);
			return null;
		}

		try {
			logger.info("zip: " + fileName);
			String xmlContent = getContent(fetchZipBytes(bucket, key));
			if (StringUtils.isEmpty(xmlContent)) {
				logger.error("unable to get xml content from " + fileName);
				return null;
			}
			IComponentRegistry.smaIngestComponent.ingestXMLFile(
					xmlContent, customer.get().getCustomerId());
		} catch (Exception e) {
			logger.error("handleRequest " + key, e);
			after();
			return null;
		}
		TransactionUtil.addDeviceId(null, null);
		logger.info("import completed, deleting " + fileName);
		delete(bucket, key);
		after();
		return null;
	}

	protected String getContent(byte[] zipFile) {
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				String entryName = zipEntry.getName();
				if (entryName.contains("0000.xml")
						|| entryName.contains("1500.xml")
						|| entryName.contains("3000.xml")
						|| entryName.contains("4500.xml")) {

					try (ByteArrayOutputStream os = new ByteArrayOutputStream();
							BOMInputStream stream =
									BOMInputStream.builder().setInputStream(zis).get()) {
						int length;
						byte[] buffer = new byte[1024];
						while ((length = stream.read(buffer)) > 0) {
							os.write(buffer, 0, length);
						}
						return os.toString(StandardCharsets.UTF_8);
					}
				}

				zipEntry = zis.getNextEntry();
			}
		} catch (IOException e) {
			logger.error("getContent", e);
		}
		return null;
	}

	private byte[] fetchZipBytes(String bucket, String key) throws IOException {
		ResponseBytes<GetObjectResponse> objectBytes = IComponentRegistry.smaIngestComponent
				.getS3Client()
				.getObjectAsBytes(
						GetObjectRequest.builder().key(key).bucket(bucket).build());
		return objectBytes.asByteArray();
	}

	private void delete(String bucket, String key) {
		IComponentRegistry.smaIngestComponent
				.getS3Client()
				.deleteObject(
						DeleteObjectRequest.builder().bucket(bucket).key(key).build());
	}

	protected Optional<Customer> getCustomerFromPath(String s3Key) {
		if (StringUtils.isEmpty(s3Key) || !s3Key.contains("/")) {
			return Optional.empty();
		}
		String accessKey = s3Key.substring(0, s3Key.indexOf("/"));
		return IComponentRegistry.customerComponent.findCustomerIdByAccessKey(accessKey);
	}
}
