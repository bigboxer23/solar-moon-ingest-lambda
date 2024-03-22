'use strict';
var AWS = require('aws-sdk');
AWS.config.setPromisesDependency(require('bluebird'));
var CognitoIdentityServiceProvider = new AWS.CognitoIdentityServiceProvider({
	apiVersion: '2019-11-07',
	region: process.env.REGION
});
exports.handler = (req, context, callback) => {
	console.log(req);
	const confirmationCode = req.queryStringParameters.code;
	const username = req.queryStringParameters.username;
	const clientId = req.queryStringParameters.clientId;
	let params = {
		ClientId: clientId,
		ConfirmationCode: confirmationCode,
		Username: username
	};
	//Validating the user
	let confirmSignUp = CognitoIdentityServiceProvider.confirmSignUp(params).promise();
	confirmSignUp.then(
			(data) => {
				const response = {
					"statusCode": 301,
					"headers": {
						"location": "https://solarmoonanalytics.com/verified"
					},
					"body": "",
					"isBase64Encoded": false
				};
				callback(null, response);
			}
	).catch(
			(error) => {
				console.log(error.message);
				const response = {
					"statusCode": 301,
					"headers": {
						"location": "https://solarmoonanalytics.com/verified"
					},
					"body": "",
					"isBase64Encoded": false
				};

				return callback(null, response);
			}
	)
};
