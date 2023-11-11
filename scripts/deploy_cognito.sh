#!/usr/bin/env bash
version=2.21.10
cd target
rm -rf java
rm cognito-layer.zip
mkdir java
mkdir java/lib
cp ~/.m2/repository/software/amazon/awssdk/cognitoidentityprovider/$version/cognitoidentityprovider-$version.jar java/lib
zip -r cognito-layer.zip java
aws s3 cp cognito-layer.zip s3://solarmoonanalytics/lambda/
aws lambda publish-layer-version --layer-name cognito-layer --compatible-architectures arm64 --compatible-runtimes java17 --description $version --region us-west-2 --content S3Bucket=solarmoonanalytics,S3Key=lambda/cognito-layer.zip --output text