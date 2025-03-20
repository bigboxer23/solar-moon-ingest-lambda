package com.bigboxer23.solar_moon.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.TestUtils;
import com.bigboxer23.solar_moon.data.Device;
import com.bigboxer23.solar_moon.data.DeviceData;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import com.bigboxer23.solar_moon.search.OpenSearchQueries;
import com.bigboxer23.solar_moon.util.TimeConstants;
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
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.utils.StringUtils;

/** */
@Slf4j
public class TestDeviceGenerator extends AbstractRequestStreamHandler {

	private static final String customerId = PropertyUtils.getProperty("customer_id");

	private static final String srcCustomerId = PropertyUtils.getProperty("customer_id_source");

	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		safeHandleRequest(() -> {
			if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(srcCustomerId)) {
				log.warn("define customer id and source customer id to mock data");
				return;
			}
			List<Device> src = deviceComponent.getDevicesForCustomerId(srcCustomerId).stream()
					.filter(d -> !d.isVirtual())
					.toList();
			Arrays.stream(customerId.split(",")).filter(c -> !c.isBlank()).forEach(c -> mockCustomer(c, src));
		});
	}

	private void mockCustomer(String customerId, List<Device> srcDevices) {
		TransactionUtil.updateCustomerId(customerId);
		log.info("Mocking device content for " + customerId);
		List<Device> mock = deviceComponent.getDevicesForCustomerId(customerId).stream()
				.filter(d -> !d.isVirtual())
				.filter(d -> !d.isDisabled())
				.toList();
		for (int ai = 0; ai < mock.size(); ai++) {
			Device device = mock.get(ai);
			TransactionUtil.addDeviceId(device.getId(), device.getSiteId());
			if ("NO_MOCK".equalsIgnoreCase(device.getMock())) {
				log.debug("not mocking ");
				continue;
			}
			Device srcDevice = findSourceDevice(ai, srcDevices, device);
			log.info("mocking "
					+ device.getDisplayName()
					+ ":"
					+ device.getSiteId()
					+ " from "
					+ srcDevice.getDisplayName()
					+ ":"
					+ srcDevice.getSiteId());
			try {
				DeviceData srcDeviceData = OSComponent.getLastDeviceEntry(
						srcDevice.getName(), OpenSearchQueries.getDeviceIdQuery(srcDevice.getId()));
				log.debug("from "
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
						log.warn("Duplicate last data, not rewriting " + device.getDisplayName());
					} else {
						obviousIngestComponent.handleDeviceBody(
								TestUtils.getDeviceXML(
										device.getDeviceName(),
										Date.from(ldt.plusMinutes(15)
												.atZone(ZoneId.systemDefault())
												.toInstant()),
										Math.max(0, srcDeviceData.getAverageCurrent()),
										Math.max(0, srcDeviceData.getAverageVoltage()),
										Math.max(0, srcDeviceData.getPowerFactor()),
										getTotalEnergyConsumed(srcDeviceData),
										Math.max(0, srcDeviceData.getTotalRealPower())),
								customerId);
					}
				}
			} catch (Exception e) {
				log.warn("error processing device " + device.getDisplayName(), e);
			}
		}
	}

	/**
	 * Handle case where most recent previous value has stale data. If stale, fetch a max from OS
	 *
	 * @param srcDeviceData
	 * @return
	 */
	protected float getTotalEnergyConsumed(DeviceData srcDeviceData) {
		return srcDeviceData.getTotalEnergyConsumed() > 0
				? srcDeviceData.getTotalEnergyConsumed()
				: OSComponent.getMaxTotalEnergyConsumed(
						srcDeviceData.getCustomerId(), srcDeviceData.getDeviceId(), TimeConstants.DAY);
	}

	private Device findSourceDevice(int index, List<Device> srcDevices, Device mockDevice) {
		return srcDevices.stream()
				.filter(d -> d.getDisplayName().equals(mockDevice.getDisplayName())
						|| (!StringUtils.isEmpty(mockDevice.getMock())
								&& d.getDisplayName().equals(mockDevice.getMock())))
				.findAny()
				.orElseGet(() -> {
					Device d = srcDevices.get(index % srcDevices.size());
					mockDevice.setMock(d.getDisplayName());
					log.warn("Setting up " + mockDevice.getDisplayName() + " as mock of " + d.getDisplayName());
					deviceComponent.updateDevice(mockDevice);
					return d;
				});
	}
}
