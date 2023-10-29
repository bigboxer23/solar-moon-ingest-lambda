#!/usr/bin/env bash
cd target
rm -rf java
rm stripe-layer.zip
mkdir java
mkdir java/lib
cp ~/.m2/repository/com/stripe/stripe-java/24.0.0/stripe-java-24.0.0.jar java/lib
cp ~/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar java/lib
zip -r stripe-layer.zip java
aws s3 cp stripe-layer.zip s3://solarmoonanalytics/lambda/
aws lambda publish-layer-version --layer-name stripe-layer --compatible-architectures arm64 --compatible-runtimes java17 --region us-west-2 --content S3Bucket=solarmoonanalytics,S3Key=lambda/stripe-layer.zip --output text