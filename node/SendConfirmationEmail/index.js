export const handler = async (event, context, callback) => {
	// Identify why was this function invoked
	if(event.triggerSource === "CustomMessage_SignUp") {
		console.log(event);
		// Ensure that your message contains event.request.codeParameter. This is the placeholder for code that will be sent
		const { codeParameter } = event.request
		const { userName, region } = event
		const { clientId } = event.callerContext
		const { email } = event.request.userAttributes
		const url = process.env.CONFIRMATION_URL
		const link = `${url}?code=${codeParameter}&username=${userName}&clientId=${clientId}&region=${region}&email=${email}`
		const message = `<!doctype html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <title>Please verify your Solar Moon Analytics account</title>
  <style>
	  @media only screen and (max-width: 620px) {
		  table.body h1 {
			  font-size: 28px !important;
			  margin-bottom: 10px !important;
		  }

		  table.body p,
		  table.body ul,
		  table.body ol,
		  table.body td,
		  table.body span,
		  table.body a {
			  font-size: 16px !important;
		  }

		  table.body .wrapper,
		  table.body .article {
			  padding: 15px !important;
		  }

		  table.body .content {
			  padding: 0 !important;
		  }

		  table.body .container {
			  padding: 0 !important;
			  width: 100% !important;
		  }

		  table.body .main {
			  border-left-width: 0 !important;
			  border-radius: 0 !important;
			  border-right-width: 0 !important;
		  }

		  table.body .btn table {
			  width: 100% !important;
		  }

		  table.body .btn a {
			  width: 100% !important;
		  }
	  }
	  @media all {
		  .ExternalClass {
			  width: 100%;
		  }

		  body * {
			  color:#000000;
		  }

		  .ExternalClass,
		  .ExternalClass p,
		  .ExternalClass span,
		  .ExternalClass font,
		  .ExternalClass td,
		  .ExternalClass div {
			  line-height: 100%;
		  }

		  .apple-link a {
			  color: inherit !important;
			  font-family: inherit !important;
			  font-size: inherit !important;
			  font-weight: inherit !important;
			  line-height: inherit !important;
			  text-decoration: none !important;
		  }

		  #MessageViewBody a {
			  color: inherit;
			  text-decoration: none;
			  font-size: inherit;
			  font-family: inherit;
			  font-weight: inherit;
			  line-height: inherit;
		  }

		  .btn-primary table td:hover {
			  background-color: #34495e !important;
		  }

		  .btn-primary a:hover {
			  background-color: #34495e !important;
			  border-color: #34495e !important;
		  }
	  }
  </style>
</head>
<body
  style="
      background-color: #eef2f9;
      font-family: sans-serif;
      -webkit-font-smoothing: antialiased;
      font-size: 14px;
      line-height: 1.4;
      margin: 0;
      padding: 0;
      -ms-text-size-adjust: 100%;
      -webkit-text-size-adjust: 100%;
    "
>
<span
  class="preheader"
  style="
        color: transparent;
        display: none;
        height: 0;
        max-height: 0;
        max-width: 0;
        opacity: 0;
        overflow: hidden;
        mso-hide: all;
        visibility: hidden;
        width: 0;
      "
></span>
<table
  role="presentation"
  border="0"
  cellpadding="0"
  cellspacing="0"
  class="body"
  style="
        border-collapse: separate;
        mso-table-lspace: 0pt;
        mso-table-rspace: 0pt;
        background-color: #eef2f9;
        width: 100%;
      "
  width="100%"
  bgcolor="#eef2f9"
>
  <tr>
    <td
      class="container"
      style="
            font-family: sans-serif;
            font-size: 14px;
            vertical-align: top;
            display: block;
            max-width: 580px;
            padding: 10px;
            width: 580px;
            margin: 0 auto;
          "
      width="580"
      valign="top"
    >
      <div
        class="content"
        style="
              box-sizing: border-box;
              display: block;
              margin: 0 10px;
              max-width: 580px;
              padding: 10px;
            "
      >
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td align="center">
              <a
                href="https://solarmoonanalytics.com"
                target="_blank"
                style="
                                                            box-sizing: border-box;
                                                            cursor: pointer;
                                                            display: inline-block;
                                                            margin: 0;
                                                            padding: 12px 25px;
                                                            text-decoration: none;
                                                          "
              >
                <img
                  style="height: 75px; width: 75px"
                  src="https://solarmoonanalytics.s3.us-west-2.amazonaws.com/logo.png"
                />
              </a>
            </td>
          </tr>
        </table>
      </div>
      <div
        class="content"
        style="
              box-sizing: border-box;
              display: block;
              margin: 0 10px;
              max-width: 580px;
              padding: 10px;
            "
      >
        <table
          role="presentation"
          class="main"
          bgcolor="#FFFFFF"
          style="
                border-collapse: separate;
                mso-table-lspace: 0pt;
                mso-table-rspace: 0pt;
                background-color: #FFFFFF;
                border: none;
                border-radius: 3px;
                width: 100%;
              "
          width="100%"
        >
          <tr>
            <td
              class="wrapper"
              style="
                    font-family: sans-serif;
                    font-size: 14px;
                    vertical-align: top;
                    box-sizing: border-box;
                    padding: 20px;
                  "
              valign="top"
            >
              <table
                role="presentation"
                border="0"
                cellpadding="0"
                cellspacing="0"
                style="
                      border-collapse: separate;
                      mso-table-lspace: 0pt;
                      mso-table-rspace: 0pt;
                      width: 100%;
                      padding-bottom:15px;
                    "
                width="100%"
              >
                <tr>
                  <td
                    style="
                          font-family: sans-serif;
                          font-size: 14px;
                          vertical-align: top;
                        "
                    valign="top"
                  >
                    <p
                      style="
                            font-family: sans-serif;
                            font-size: 14px;
                            font-weight: normal;
                            margin: 0;
                            margin-bottom: 30px;
                            text-align: center;
                          "
                    >
                      Please click below to confirm your email address.
                    </p>
                  </td>
                </tr>
                <tr><td><table
                  role="presentation"
                  border="0"
                  cellpadding="0"
                  cellspacing="0"
                  class="btn btn-primary"
                  style="
                            border-collapse: separate;
                            mso-table-lspace: 0pt;
                            mso-table-rspace: 0pt;
                            box-sizing: border-box;
                            width: 100%;
                          "
                  width="100%"
                >
                  <tbody>
                  <tr>
                    <td
                      align="center"
                      style="
                                  font-family: sans-serif;
                                  font-size: 14px;
                                  vertical-align: top;
                                  padding-bottom: 25px;
                                "
                      valign="top"
                    >
                      <table
                        role="presentation"
                        border="0"
                        cellpadding="0"
                        cellspacing="0"
                        style="
                                    border-collapse: separate;
                                    mso-table-lspace: 0pt;
                                    mso-table-rspace: 0pt;
                                    width: auto;
                                  "
                      >
                        <tbody>
                        <tr>
                          <td
                            style="
                                          font-family: sans-serif;
                                          font-size: 14px;
                                          vertical-align: top;
                                          border-radius: 5px;
                                          text-align: center;
                                          background-color: #5178c2;
                                        "
                            valign="top"
                            align="center"
                            bgcolor="#5178c2"
                          >
                            <a
                              href="${link}"
                              target="_blank"
                              style="
                                            border: solid 1px #5178c2;
                                            border-radius: 15px;
                                            box-sizing: border-box;
                                            cursor: pointer;
                                            display: inline-block;
                                            font-size: 14px;
                                            font-weight: bold;
                                            margin: 0;
                                            padding: 12px 25px;
                                            text-decoration: none;
                                            background-color: #5178c2;
                                            border-color: #5178c2;
                                            color: #FFFFFF;
                                          "
                            >Confirm Email Address</a
                            >
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table></td></tr>
              </table>
            </td>
          </tr>

        </table>

        <div
          class="footer"
          style="
                clear: both;
                margin-top: 10px;
                text-align: center;
                width: 100%;
              "
        >
          <table
            role="presentation"
            border="0"
            cellpadding="0"
            cellspacing="0"
            style="
                  border-collapse: separate;
                  mso-table-lspace: 0pt;
                  mso-table-rspace: 0pt;
                  width: 100%;
                "
            width="100%"
          >
            <tr>
              <td
                class="content-block"
                style="
                      font-family: sans-serif;
                      vertical-align: top;
                      padding-bottom: 10px;
                      padding-top: 10px;
                      color: #999999;
                      font-size: 12px;
                      text-align: center;
                    "
                valign="top"
                align="center"
              >
                    <span
                      class="apple-link"
                      style="
                        color: #999999;
                        font-size: 12px;
                        text-align: center;
                      "
                    >Copyright © 2024 Solar Moon Analytics, LLC</span
                    >
              </td>
            </tr>
          </table>
        </div>
      </div>
    </td>
  </tr>
</table>
</body>
</html>
`
		event.response.emailSubject = "Please confirm your Solar Moon Analytics account";
		event.response.emailMessage = message;
	}
	// Return to Amazon Cognito
	callback(null, event);
};
