package com.bigboxer23.solar_moon.lambda.cleanup;

import com.amazonaws.services.lambda.runtime.Context;
import com.bigboxer23.solar_moon.lambda.AbstractRequestStreamHandler;
import com.bigboxer23.solar_moon.lambda.data.LambdaRequest;
import com.bigboxer23.solar_moon.lambda.data.LambdaResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Lambda method to run on a schedule and cleanup any empty/stale folders */
public class ScheduledSMAS3Cleanup extends AbstractRequestStreamHandler {
	@Override
	public LambdaResponse handleLambdaRequest(LambdaRequest request) throws IOException {
		return null;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
		safeHandleRequest(smaCleanupComponent::cleanupEmptyFTPS3Folders);
	}
}
