import {
	ConfirmSignUpCommand,
	CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";

export const handler = async (event) => {

	const client = new CognitoIdentityProviderClient({});
	const command = new ConfirmSignUpCommand({
		ClientId: event.queryStringParameters.clientId,
		ConfirmationCode: event.queryStringParameters.code,
		Username: event.queryStringParameters.username
	});
	try {
		const ret = await client.send(command);
		console.log('ret: ' + JSON.stringify(ret));
		if (ret["$metadata"].httpStatusCode === 200) {
			return {
				"statusCode": 301,
				"headers": {
					"location": "https://solarmoonanalytics.com/verified"
				},
				"body": "",
				"isBase64Encoded": false
			};
		}
	} catch( e) {
		console.log('exception: ' + e);
		console.log(JSON.stringify(e));
	}
	return {
		"statusCode": 301,
		"headers": {
			"location": "https://solarmoonanalytics.com/accountActivationError"
		},
		"body": "",
		"isBase64Encoded": false
	};
};
