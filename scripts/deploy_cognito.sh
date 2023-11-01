#!/usr/bin/env bash
cd target
rm -rf java
rm cognito-layer.zip
mkdir java
mkdir java/lib
cp ~/.m2/repository/software/amazon/awssdk/cognitoidentityprovider/2.21.10/cognitoidentityprovider-2.21.10.jar java/lib
zip -r cognito-layer.zip java
aws s3 cp cognito-layer.zip s3://solarmoonanalytics/lambda/
aws lambda publish-layer-version --layer-name cognito-layer --compatible-architectures arm64 --compatible-runtimes java17 --region us-west-2 --content S3Bucket=solarmoonanalytics,S3Key=lambda/cognito-layer.zip --output text