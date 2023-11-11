#!/usr/bin/env bash
version=2.21.19
cd target
rm -rf java
rm sqs-layer.zip
mkdir java
mkdir java/lib
cp ~/.m2/repository/software/amazon/awssdk/sqs/$version/sqs-$version.jar java/lib
zip -r sqs-layer.zip java
aws s3 cp sqs-layer.zip s3://solarmoonanalytics/lambda/
aws lambda publish-layer-version --layer-name sqs-layer --compatible-architectures arm64 --compatible-runtimes java17 --description $version --region us-west-2 --content S3Bucket=solarmoonanalytics,S3Key=lambda/sqs-layer.zip --output text