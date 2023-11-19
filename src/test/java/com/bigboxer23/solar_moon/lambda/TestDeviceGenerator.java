package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.TestUtils;
import com.bigboxer23.solar_moon.TimeUtils;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.lambda.utils.PropertyUtils;
import com.bigboxer23.solar_moon.open_search.OpenSearchQueries;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.utils.StringUtils;

/** */
public class TestDeviceGenerator extends AbstractRequestStreamHandler {

	private static final String customerId = PropertyUtils.getProperty("customer_id");

	private static final String srcCustomerId = PropertyUtils.getProperty("customer_id_source");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(srcCustomerId)) {
			logger.warn("define customer id and source customer id to mock data");
			after();
			return;
		}
		List<Device> src = deviceComponent.getDevices(srcCustomerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		Arrays.stream(customerId.split(",")).filter(c -> !c.isBlank()).forEach(c -> {
			mockCustomer(c, src);
		});
		after();
	}

	private void mockCustomer(String customerId, List<Device> srcDevices) {
		logger.info("Mocking device content for " + customerId);
		TransactionUtil.updateCustomerId(customerId);
		List<Device> mock = deviceComponent.getDevices(customerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		for (int ai = 0; ai < mock.size(); ai++) {
			Device device = mock.get(ai);
			TransactionUtil.addDeviceId(device.getId());
			Device srcDevice = findSourceDevice(ai, srcDevices, device);
			logger.info("adding " + getDeviceName(device) + " with " + getDeviceName(srcDevice));
			try {
				DeviceData srcDeviceData = OSComponent.getLastDeviceEntry(
						srcDevice.getName(), OpenSearchQueries.getDeviceIdQuery(srcDevice.getId()));
				if (srcDeviceData != null) {
					generationComponent.handleDeviceBody(
							TestUtils.getDeviceXML(
									device.getDeviceName(),
									TimeUtils.get15mRoundedDate(),
									srcDeviceData.getAverageCurrent(),
									srcDeviceData.getAverageVoltage(),
									srcDeviceData.getPowerFactor(),
									srcDeviceData.getTotalEnergyConsumed(),
									srcDeviceData.getTotalRealPower()),
							customerId);
				}
			} catch (Exception e) {
				logger.warn("error processing device " + getDeviceName(device), e);
			}
		}
	}

	private Device findSourceDevice(int index, List<Device> srcDevices, Device mockDevice) {
		return srcDevices.stream()
				.filter(d -> getDeviceName(d).equals(getDeviceName(mockDevice)))
				.findAny()
				.orElseGet(() -> srcDevices.get(index % srcDevices.size()));
	}

	private String getDeviceName(Device device) {
		return Optional.ofNullable(device.getName()).orElse(device.getDeviceName());
	}
}
