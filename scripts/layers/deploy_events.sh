#!/usr/bin/env bash
version=3.11.3
cd target
rm -rf java
rm events-layer.zip
mkdir java
mkdir java/lib

cp ~/.m2/repository/com/amazonaws/aws-lambda-java-events/$version/aws-lambda-java-events-$version.jar  java/lib
cp ~/.m2/repository/joda-time/joda-time/2.10.8/joda-time-2.10.8.jar java/lib
zip -r events-layer.zip java
aws s3 cp events-layer.zip s3://solarmoonanalytics/lambda/
aws lambda publish-layer-version --layer-name events-layer --compatible-architectures arm64 --compatible-runtimes java17 --description $version --region us-west-2 --content S3Bucket=solarmoonanalytics,S3Key=lambda/events-layer.zip --output text