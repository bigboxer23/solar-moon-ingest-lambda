#!/usr/bin/env bash
mvn package -DskipTests
aws lambda update-function-code --function-name quickDeviceCheck --region us-west-2 --s3-bucket solarmoonanalytics --s3-key lambda/solar-moon-ingest-lambda-0.1.jar --output text