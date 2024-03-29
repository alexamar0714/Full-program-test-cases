package ca.uhn.fhir.rest.server.interceptor;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.RequestTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.RestfulServerUtils;
import ca.uhn.fhir.rest.server.RestfulServerUtils.ResponseEncoding;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.util.UrlUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

/*
 * #%L
 * HAPI FHIR - Server Framework
 * %%
 * Copyright (C) 2014 - 2018 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * This interceptor detects when a request is coming from a browser, and automatically returns a response with syntax
 * highlighted (coloured) HTML for the response instead of just returning raw XML/JSON.
 *
 * @since 1.0
 */
public class ResponseHighlighterInterceptor extends InterceptorAdapter {

	/**
	 * TODO: As of HAPI 1.6 (2016-06-10) this parameter has been replaced with simply
	 * requesting _format=json or xml so eventually this parameter should be removed
	 */
	public static final String PARAM_RAW = "_raw";
	public static final String PARAM_RAW_TRUE = "true";
	public static final String PARAM_TRUE = "true";
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ResponseHighlighterInterceptor.class);
	private static final String[] PARAM_FORMAT_VALUE_JSON = new String[]{Constants.FORMAT_JSON};
	private static final String[] PARAM_FORMAT_VALUE_XML = new String[]{Constants.FORMAT_XML};
	private boolean myShowRequestHeaders = false;
	private boolean myShowResponseHeaders = true;

	/**
	 * Constructor
	 */
	public ResponseHighlighterInterceptor() {
		super();
	}

	private String createLinkHref(Map<String, String[]> parameters, String formatValue) {
		StringBuilder rawB = new StringBuilder();
		for (String next : parameters.keySet()) {
			if (Constants.PARAM_FORMAT.equals(next)) {
				continue;
			}
			for (String nextValue : parameters.get(next)) {
				if (isBlank(nextValue)) {
					continue;
				}
				if (rawB.length() == 0) {
					rawB.append('?');
				} else {
					rawB.append('&');
				}
				rawB.append(UrlUtil.escapeUrlParam(next));
				rawB.append('=');
				rawB.append(UrlUtil.escapeUrlParam(nextValue));
			}
		}
		if (rawB.length() == 0) {
			rawB.append('?');
		} else {
			rawB.append('&');
		}
		rawB.append(Constants.PARAM_FORMAT).append('=').append(formatValue);

		String link = rawB.toString();
		return link;
	}

	private int format(String theResultBody, StringBuilder theTarget, EncodingEnum theEncodingEnum) {
		String str = StringEscapeUtils.escapeHtml4(theResultBody);
		if (str == null || theEncodingEnum == null) {
			theTarget.append(str);
			return 0;
		}

		theTarget.append("<div id=\"line1\">");

		boolean inValue = false;
		boolean inQuote = false;
		boolean inTag = false;
		int lineCount = 1;

		for (int i = 0; i < str.length(); i++) {
			char prevChar = (i > 0) ? str.charAt(i - 1) : ' ';
			char nextChar = str.charAt(i);
			char nextChar2 = (i + 1) < str.length() ? str.charAt(i + 1) : ' ';
			char nextChar3 = (i + 2) < str.length() ? str.charAt(i + 2) : ' ';
			char nextChar4 = (i + 3) < str.length() ? str.charAt(i + 3) : ' ';
			char nextChar5 = (i + 4) < str.length() ? str.charAt(i + 4) : ' ';
			char nextChar6 = (i + 5) < str.length() ? str.charAt(i + 5) : ' ';

			if (nextChar == '\n') {
				lineCount++;
				theTarget.append("</div><div id=\"line");
				theTarget.append(lineCount);
				theTarget.append("\" onclick=\"updateHighlightedLineTo('#L");
				theTarget.append(lineCount);
				theTarget.append("');\">");
				continue;
			}

			if (theEncodingEnum == EncodingEnum.JSON) {

				if (inQuote) {
					theTarget.append(nextChar);
					if (prevChar != '\\' && nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o' && nextChar5 == 't' && nextChar6 == ';') {
						theTarget.append("quot;</span>");
						i += 5;
						inQuote = false;
					} else if (nextChar == '\\' && nextChar2 == '"') {
						theTarget.append("quot;</span>");
						i += 5;
						inQuote = false;
					}
				} else {
					if (nextChar == ':') {
						inValue = true;
						theTarget.append(nextChar);
					} else if (nextChar == '[' || nextChar == '{') {
						theTarget.append("<span class='hlControl'>");
						theTarget.append(nextChar);
						theTarget.append("</span>");
						inValue = false;
					} else if (nextChar == '{' || nextChar == '}' || nextChar == ',') {
						theTarget.append("<span class='hlControl'>");
						theTarget.append(nextChar);
						theTarget.append("</span>");
						inValue = false;
					} else if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o' && nextChar5 == 't' && nextChar6 == ';') {
						if (inValue) {
							theTarget.append("<span class='hlQuot'>&quot;");
						} else {
							theTarget.append("<span class='hlTagName'>&quot;");
						}
						inQuote = true;
						i += 5;
					} else if (nextChar == ':') {
						theTarget.append("<span class='hlControl'>");
						theTarget.append(nextChar);
						theTarget.append("</span>");
						inValue = true;
					} else {
						theTarget.append(nextChar);
					}
				}

			} else {
				if (inQuote) {
					theTarget.append(nextChar);
					if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o' && nextChar5 == 't' && nextChar6 == ';') {
						theTarget.append("quot;</span>");
						i += 5;
						inQuote = false;
					}
				} else if (inTag) {
					if (nextChar == '&' && nextChar2 == 'g' && nextChar3 == 't' && nextChar4 == ';') {
						theTarget.append("</span><span class='hlControl'>&gt;</span>");
						inTag = false;
						i += 3;
					} else if (nextChar == ' ') {
						theTarget.append("</span><span class='hlAttr'>");
						theTarget.append(nextChar);
					} else if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o' && nextChar5 == 't' && nextChar6 == ';') {
						theTarget.append("<span class='hlQuot'>&quot;");
						inQuote = true;
						i += 5;
					} else {
						theTarget.append(nextChar);
					}
				} else {
					if (nextChar == '&' && nextChar2 == 'l' && nextChar3 == 't' && nextChar4 == ';') {
						theTarget.append("<span class='hlControl'>&lt;</span><span class='hlTagName'>");
						inTag = true;
						i += 3;
					} else {
						theTarget.append(nextChar);
					}
				}
			}
		}

		theTarget.append("</div>");
		return lineCount;
	}

	@Override
	public boolean handleException(RequestDetails theRequestDetails, BaseServerResponseException theException, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse)
		throws ServletException, IOException {
		/*
		 * It's not a browser...
		 */
		Set<String> accept = RestfulServerUtils.parseAcceptHeaderAndReturnHighestRankedOptions(theServletRequest);
		if (!accept.contains(Constants.CT_HTML)) {
			return super.handleException(theRequestDetails, theException, theServletRequest, theServletResponse);
		}

		/*
		 * It's an AJAX request, so no HTML
		 */
		String requestedWith = theServletRequest.getHeader("X-Requested-With");
		if (requestedWith != null) {
			return super.handleException(theRequestDetails, theException, theServletRequest, theServletResponse);
		}

		/*
		 * Not a GET
		 */
		if (theRequestDetails.getRequestType() != RequestTypeEnum.GET) {
			return super.handleException(theRequestDetails, theException, theServletRequest, theServletResponse);
		}

		if (theException.getOperationOutcome() == null) {
			return super.handleException(theRequestDetails, theException, theServletRequest, theServletResponse);
		}

		streamResponse(theRequestDetails, theServletResponse, theException.getOperationOutcome(), theServletRequest, theException.getStatusCode());

		return false;
	}

	/**
	 * If set to <code>true</code> (default is <code>false</code>) response will include the
	 * request headers
	 */
	public boolean isShowRequestHeaders() {
		return myShowRequestHeaders;
	}

	/**
	 * If set to <code>true</code> (default is <code>false</code>) response will include the
	 * request headers
	 *
	 * @return Returns a reference to this for easy method chaining
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ResponseHighlighterInterceptor setShowRequestHeaders(boolean theShowRequestHeaders) {
		myShowRequestHeaders = theShowRequestHeaders;
		return this;
	}

	/**
	 * If set to <code>true</code> (default is <code>true</code>) response will include the
	 * response headers
	 */
	public boolean isShowResponseHeaders() {
		return myShowResponseHeaders;
	}

	/**
	 * If set to <code>true</code> (default is <code>true</code>) response will include the
	 * response headers
	 *
	 * @return Returns a reference to this for easy method chaining
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ResponseHighlighterInterceptor setShowResponseHeaders(boolean theShowResponseHeaders) {
		myShowResponseHeaders = theShowResponseHeaders;
		return this;
	}

	@Override
	public boolean outgoingResponse(RequestDetails theRequestDetails, ResponseDetails theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse)
		throws AuthenticationException {

		/*
		 * Request for _raw
		 */
		String[] rawParamValues = theRequestDetails.getParameters().get(PARAM_RAW);
		if (rawParamValues != null && rawParamValues.length > 0 && rawParamValues[0].equals(PARAM_RAW_TRUE)) {
			ourLog.warn("Client is using non-standard/legacy  _raw parameter - Use _format=json or _format=xml instead, as this parmameter will be removed at some point");
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}

		boolean force = false;
		String[] formatParams = theRequestDetails.getParameters().get(Constants.PARAM_FORMAT);
		if (formatParams != null && formatParams.length > 0) {
			String formatParam = defaultString(formatParams[0]);
			int semiColonIdx = formatParam.indexOf(';');
			if (semiColonIdx != -1) {
				formatParam = formatParam.substring(0, semiColonIdx);
			}
			formatParam = trim(formatParam);

			if (Constants.FORMATS_HTML.contains(formatParam)) { // this is a set
				force = true;
			} else if (Constants.FORMATS_HTML_XML.equals(formatParam)) {
				force = true;
				theRequestDetails.addParameter(Constants.PARAM_FORMAT, PARAM_FORMAT_VALUE_XML);
			} else if (Constants.FORMATS_HTML_JSON.equals(formatParam)) {
				force = true;
				theRequestDetails.addParameter(Constants.PARAM_FORMAT, PARAM_FORMAT_VALUE_JSON);
			} else {
				return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
			}
		}

		/*
		 * It's not a browser...
		 */
		Set<String> highestRankedAcceptValues = RestfulServerUtils.parseAcceptHeaderAndReturnHighestRankedOptions(theServletRequest);
		if (!force && highestRankedAcceptValues.contains(Constants.CT_HTML) == false) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}

		/*
		 * It's an AJAX request, so no HTML
		 */
		if (!force && isNotBlank(theServletRequest.getHeader("X-Requested-With"))) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}
		/*
		 * If the request has an Origin header, it is probably an AJAX request
		 */
		if (!force && isNotBlank(theServletRequest.getHeader(Constants.HEADER_ORIGIN))) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}

		/*
		 * Not a GET
		 */
		if (!force && theRequestDetails.getRequestType() != RequestTypeEnum.GET) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}

		/*
		 * Not binary
		 */
		if (!force && "Binary".equals(theRequestDetails.getResourceName())) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}

		streamResponse(theRequestDetails, theServletResponse, theResponseObject.getResponseResource(), theServletRequest, 200);

		return false;
	}

	private void streamRequestHeaders(ServletRequest theServletRequest, StringBuilder b) {
		if (theServletRequest instanceof HttpServletRequest) {
			HttpServletRequest sr = (HttpServletRequest) theServletRequest;
			b.append("<h1>Request</h1>");
			b.append("<div class=\"headersDiv\">");
			Enumeration<String> headerNamesEnum = sr.getHeaderNames();
			while (headerNamesEnum.hasMoreElements()) {
				String nextHeaderName = headerNamesEnum.nextElement();
				Enumeration<String> headerValuesEnum = sr.getHeaders(nextHeaderName);
				while (headerValuesEnum.hasMoreElements()) {
					String nextHeaderValue = headerValuesEnum.nextElement();
					b.append("<div class=\"headersRow\">");
					b.append("<span class=\"headerName\">").append(nextHeaderName).append(": ").append("</span>");
					b.append("<span class=\"headerValue\">").append(nextHeaderValue).append("</span>");
					b.append("</div>");
				}
			}
			b.append("</div>");
		}
	}

	private void streamResponse(RequestDetails theRequestDetails, HttpServletResponse theServletResponse, IBaseResource theResource, ServletRequest theServletRequest, int theStatusCode) {

		if (theRequestDetails.getServer() instanceof RestfulServer) {
			RestfulServer rs = (RestfulServer) theRequestDetails.getServer();
			rs.addHeadersToResponse(theServletResponse);
		}

		IParser p;
		Map<String, String[]> parameters = theRequestDetails.getParameters();
		if (parameters.containsKey(Constants.PARAM_FORMAT)) {
			FhirVersionEnum forVersion = theResource.getStructureFhirVersionEnum();
			p = RestfulServerUtils.getNewParser(theRequestDetails.getServer().getFhirContext(), forVersion, theRequestDetails);
		} else {
			EncodingEnum defaultResponseEncoding = theRequestDetails.getServer().getDefaultResponseEncoding();
			p = defaultResponseEncoding.newParser(theRequestDetails.getServer().getFhirContext());
			RestfulServerUtils.configureResponseParser(theRequestDetails, p);
		}

		// This interceptor defaults to pretty printing unless the user
		// has specifically requested us not to
		boolean prettyPrintResponse = true;
		String[] prettyParams = parameters.get(Constants.PARAM_PRETTY);
		if (prettyParams != null && prettyParams.length > 0) {
			if (Constants.PARAM_PRETTY_VALUE_FALSE.equals(prettyParams[0])) {
				prettyPrintResponse = false;
			}
		}
		if (prettyPrintResponse) {
			p.setPrettyPrint(true);
		}

		EncodingEnum encoding = p.getEncoding();
		String encoded = p.encodeResourceToString(theResource);

		try {

			if (theStatusCode > 299) {
				theServletResponse.setStatus(theStatusCode);
			}
			theServletResponse.setContentType(Constants.CT_HTML_WITH_UTF8);

			StringBuilder b = new StringBuilder();
			b.append("<html lang=\"en\">\n");
			b.append("	<head>\n");
			b.append("		<meta charset=\"utf-8\" />\n");
			b.append("       <style>\n");
			b.append(".httpStatusDiv {");
			b.append("  font-size: 1.2em;");
			b.append("  font-weight: bold;");
			b.append("}");
			b.append(".hlQuot { color: #88F; }\n");
			b.append(".hlQuot a { text-decoration: underline; text-decoration-color: #CCC; }\n");
			b.append(".hlQuot a:HOVER { text-decoration: underline; text-decoration-color: #008; }\n");
			b.append(".hlQuot .uuid, .hlQuot .dateTime {\n");
			b.append("  user-select: all;\n");
			b.append("  -moz-user-select: all;\n");
			b.append("  -webkit-user-select: all;\n");
			b.append("  -ms-user-select: element;\n");
			b.append("}\n");
			b.append(".hlAttr {\n");
			b.append("  color: #888;\n");
			b.append("}\n");
			b.append(".hlTagName {\n");
			b.append("  color: #006699;\n");
			b.append("}\n");
			b.append(".hlControl {\n");
			b.append("  color: #660000;\n");
			b.append("}\n");
			b.append(".hlText {\n");
			b.append("  color: #000000;\n");
			b.append("}\n");
			b.append(".hlUrlBase {\n");
			b.append("}");
			b.append(".headersDiv {\n");
			b.append("  padding: 10px;");
			b.append("  margin-left: 10px;");
			b.append("  border: 1px solid #CCC;");
			b.append("  border-radius: 10px;");
			b.append("}");
			b.append(".headersRow {\n");
			b.append("}");
			b.append(".headerName {\n");
			b.append("  color: #888;\n");
			b.append("  font-family: monospace;\n");
			b.append("}");
			b.append(".headerValue {\n");
			b.append("  color: #88F;\n");
			b.append("  font-family: monospace;\n");
			b.append("}");
			b.append(".responseBodyTable {");
			b.append("  width: 100%;\n");
			b.append("  margin-left: 0px;\n");
			b.append("  margin-top: -10px;\n");
			b.append("  position: relative;\n");
			b.append("}");
			b.append(".responseBodyTableFirstColumn {");
			b.append("  position: absolute;\n");
			b.append("  width: 70px;\n");
			b.append("}");
			b.append(".responseBodyTableSecondColumn {");
			b.append("  position: absolute;\n");
			b.append("  margin-left: 70px;\n");
			b.append("  vertical-align: top;\n");
			b.append("  left: 0px;\n");
			b.append("  right: 0px;\n");
			b.append("}");
			b.append(".lineAnchor A {");
			b.append("  text-decoration: none;");
			b.append("  padding-left: 20px;");
			b.append("}");
			b.append(".lineAnchor {");
			b.append("  display: block;");
			b.append("  padding-right: 20px;");
			b.append("}");
			b.append(".selectedLine {");
			b.append("  background-color: #EEF;");
			b.append("  font-weight: bold;");
			b.append("}");
			b.append("H1 {");
			b.append("  font-size: 1.1em;");
			b.append("  color: #666;");
			b.append("}");
			b.append("BODY {\n");
			b.append("  font-family: Arial;\n");
			b.append("}");
			b.append("       </style>\n");
			b.append("	</head>\n");
			b.append("\n");
			b.append("	<body>");

			b.append("<p>");
			b.append("This result is being rendered in HTML for easy viewing. ");
			b.append("You may access this content as ");

			b.append("<a href=\"");
			b.append(createLinkHref(parameters, Constants.FORMAT_JSON));
			b.append("\">Raw JSON</a> or ");

			b.append("<a href=\"");
			b.append(createLinkHref(parameters, Constants.FORMAT_XML));
			b.append("\">Raw XML</a>, ");

			b.append(" or view this content in ");

			b.append("<a href=\"");
			b.append(createLinkHref(parameters, Constants.FORMATS_HTML_JSON));
			b.append("\">HTML JSON</a> ");

			b.append("or ");
			b.append("<a href=\"");
			b.append(createLinkHref(parameters, Constants.FORMATS_HTML_XML));
			b.append("\">HTML XML</a>.");

			Date startTime = (Date) theServletRequest.getAttribute(RestfulServer.REQUEST_START_TIME);
			if (startTime != null) {
				long time = System.currentTimeMillis() - startTime.getTime();
				b.append(" Response generated in ");
				b.append(time);
				b.append("ms.");
			}

			b.append("</p>");

			b.append("\n");

			// status (e.g. HTTP 200 OK)
			String statusName = Constants.HTTP_STATUS_NAMES.get(theServletResponse.getStatus());
			statusName = defaultString(statusName);
			b.append("<div class=\"httpStatusDiv\">");
			b.append("HTTP ");
			b.append(theServletResponse.getStatus());
			b.append(" ");
			b.append(statusName);
			b.append("</div>");

			b.append("\n");
			b.append("\n");

			try {
				if (isShowRequestHeaders()) {
					streamRequestHeaders(theServletRequest, b);
				}
				if (isShowResponseHeaders()) {
					streamResponseHeaders(theRequestDetails, theServletResponse, b);
				}
			} catch (Throwable t) {
				// ignore (this will hit if we're running in a servlet 2.5 environment)
			}

			b.append("<h1>Response Body</h1>");

			b.append("<div class=\"responseBodyTable\">");

			// Response Body
			b.append("<div class=\"responseBodyTableSecondColumn\"><pre>");
			StringBuilder target = new StringBuilder();
			int linesCount = format(encoded, target, encoding);
			b.append(target);
			b.append("</pre></div>");

			// Line Numbers
			b.append("<div class=\"responseBodyTableFirstColumn\"><pre>");
			for (int i = 1; i <= linesCount; i++) {
				b.append("<div class=\"lineAnchor\" id=\"anchor");
				b.append(i);
				b.append("\">");

				b.append("<a href=\"#L");
				b.append(i);
				b.append("\" name=\"L");
				b.append(i);
				b.append("\" id=\"L");
				b.append(i);
				b.append("\">");
				b.append(i);
				b.append("</a></div>");
			}
			b.append("</div></td>");

			b.append("</div>");

			b.append("\n");

			InputStream jsStream = ResponseHighlighterInterceptor.class.getResourceAsStream("ResponseHighlighter.js");
			String jsStr = jsStream != null ? IOUtils.toString(jsStream, "UTF-8") : "console.log('ResponseHighlighterInterceptor: javascript theResource not found')";
			jsStr = jsStr.replace("FHIR_BASE", theRequestDetails.getServerBaseForRequest());
			b.append("<script type=\"text/javascript\">");
			b.append(jsStr);
			b.append("</script>\n");

			b.append("</body>");
			b.append("</html>");
			String out = b.toString();

			theServletResponse.getWriter().append(out);
			theServletResponse.getWriter().close();
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
	}

	private void streamResponseHeaders(RequestDetails theRequestDetails, HttpServletResponse theServletResponse, StringBuilder b) {
		if (theServletResponse.getHeaderNames().isEmpty() == false) {
			b.append("<h1>Response Headers</h1>");

			b.append("<div class=\"headersDiv\">");
			for (String nextHeaderName : theServletResponse.getHeaderNames()) {
				for (String nextHeaderValue : theServletResponse.getHeaders(nextHeaderName)) {
					/*
					 * Let's pretend we're returning a FHIR content type even though we're
					 * actually returning an HTML one
					 */
					if (nextHeaderName.equalsIgnoreCase(Constants.HEADER_CONTENT_TYPE)) {
						ResponseEncoding responseEncoding = RestfulServerUtils.determineResponseEncodingNoDefault(theRequestDetails, theRequestDetails.getServer().getDefaultResponseEncoding());
						if (responseEncoding != null && isNotBlank(responseEncoding.getResourceContentType())) {
							nextHeaderValue = responseEncoding.getResourceContentType() + ";charset=utf-8";
						}
					}
					b.append("<div class=\"headersRow\">");
					b.append("<span class=\"headerName\">").append(nextHeaderName).append(": ").append("</span>");
					b.append("<span class=\"headerValue\">").append(nextHeaderValue).append("</span>");
					b.append("</div>");
				}
			}
			b.append("</div>");
		}
	}

}
