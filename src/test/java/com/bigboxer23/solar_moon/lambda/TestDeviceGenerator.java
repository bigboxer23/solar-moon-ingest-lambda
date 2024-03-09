package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.TestUtils;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.OpenSearchQueries;
import com.bigboxer23.solar_moon.web.TransactionUtil;
import com.bigboxer23.utils.properties.PropertyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
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
		TransactionUtil.updateServiceCalled(getClass().getSimpleName());
		if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(srcCustomerId)) {
			logger.warn("define customer id and source customer id to mock data");
			after();
			return;
		}
		List<Device> src = deviceComponent.getDevicesForCustomerId(srcCustomerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		Arrays.stream(customerId.split(",")).filter(c -> !c.isBlank()).forEach(c -> mockCustomer(c, src));
		after();
	}

	private void mockCustomer(String customerId, List<Device> srcDevices) {
		TransactionUtil.updateCustomerId(customerId);
		logger.info("Mocking device content for " + customerId);
		List<Device> mock = deviceComponent.getDevicesForCustomerId(customerId).stream()
				.filter(d -> !d.isVirtual())
				.toList();
		for (int ai = 0; ai < mock.size(); ai++) {
			Device device = mock.get(ai);
			TransactionUtil.addDeviceId(device.getId());
			if ("NO_MOCK".equalsIgnoreCase(device.getMock())) {
				logger.info("not mocking ");
				continue;
			}
			Device srcDevice = findSourceDevice(ai, srcDevices, device);
			logger.info("adding "
					+ getDeviceName(device)
					+ ":"
					+ device.getSiteId()
					+ " from "
					+ getDeviceName(srcDevice)
					+ ":"
					+ srcDevice.getSiteId());
			try {
				DeviceData srcDeviceData = OSComponent.getLastDeviceEntry(
						srcDevice.getName(), OpenSearchQueries.getDeviceIdQuery(srcDevice.getId()));
				logger.debug("from "
						+ Optional.ofNullable(srcDeviceData)
								.map(DeviceData::getDeviceId)
								.orElse(""));
				if (srcDeviceData != null) {
					LocalDateTime ldt =
							LocalDateTime.ofInstant(srcDeviceData.getDate().toInstant(), ZoneId.systemDefault());
					DeviceData deviceData = OSComponent.getLastDeviceEntry(
							srcDevice.getName(), OpenSearchQueries.getDeviceIdQuery(device.getId()));
					if (deviceData != null
							&& deviceData.getDate().getTime()
									== Date.from(ldt.plusMinutes(15)
													.atZone(ZoneId.systemDefault())
													.toInstant())
											.getTime()) {
						logger.warn("Duplicate last data, not rewriting " + device.getDisplayName());
					} else {
						obviousIngestComponent.handleDeviceBody(
								TestUtils.getDeviceXML(
										device.getDeviceName(),
										Date.from(ldt.plusMinutes(15)
												.atZone(ZoneId.systemDefault())
												.toInstant()),
										srcDeviceData.getAverageCurrent(),
										srcDeviceData.getAverageVoltage(),
										srcDeviceData.getPowerFactor(),
										srcDeviceData.getTotalEnergyConsumed(),
										srcDeviceData.getTotalRealPower()),
								customerId);
					}
				}
			} catch (Exception e) {
				logger.warn("error processing device " + getDeviceName(device), e);
			}
		}
	}

	private Device findSourceDevice(int index, List<Device> srcDevices, Device mockDevice) {
		return srcDevices.stream()
				.filter(d -> getDeviceName(d).equals(getDeviceName(mockDevice))
						|| (!StringUtils.isEmpty(mockDevice.getMock())
								&& getDeviceName(d).equals(mockDevice.getMock())))
				.findAny()
				.orElseGet(() -> {
					Device d = srcDevices.get(index % srcDevices.size());
					mockDevice.setMock(getDeviceName(d));
					logger.warn("Setting up " + getDeviceName(mockDevice) + " as mock of " + getDeviceName(d));
					deviceComponent.updateDevice(mockDevice);
					return d;
				});
	}

	private String getDeviceName(Device device) {
		return Optional.ofNullable(device.getName()).orElse(device.getDeviceName());
	}
}
