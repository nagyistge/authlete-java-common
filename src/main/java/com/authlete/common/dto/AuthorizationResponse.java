/*
 * Copyright (C) 2014-2015 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.authlete.common.dto;


import com.authlete.common.types.Display;
import com.authlete.common.types.Prompt;
import com.authlete.common.util.Utils;


/**
 * Response from Authlete's {@code /auth/authorization} API.
 *
 * <p>
 * Authlete's {@code /auth/authorization} API returns
 * JSON which can be mapped to this class. The service implementation should
 * retrieve the value of {@code "action"} from the response and take the
 * following steps according to the value.
 * </p>
 *
 * <dl>
 * <dt><b>{@link Action#INTERNAL_SERVER_ERROR INTERNAL_SERVER_ERROR}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "INTERNAL_SERVER_ERROR"},
 * it means that the request from the service implementation was wrong or
 * that an error occurred in Authlete.
 * </p>
 *
 * <p>
 * In either case, from the viewpoint of the client application, it is an
 * error on the server side. Therefore, the service implementation should
 * generate a response to the client application with the HTTP status of
 * {@code "500 Internal Server Error"}. Authlete recommends {@code
 * "application/json"} as the content type although OAuth 2.0 specification
 * does not mention the format of the error response when the redirect URI
 * is not usable.
 * </p>
 *
 * <p>
 * {@link #getResponseContent()} returns a JSON string which describes
 * the error, so it can be used as the entity body of the response.
 * </p>
 *
 * <p>
 * The following illustrates the response which the service implementation
 * should generate and return to the client application.
 * </p>
 *
 * <pre style="border: solid 1px black; padding: 0.5em;">
 * HTTP/1.1 500 Internal Server Error
 * Content-Type: application/json
 * Cache-Control: no-store
 * Pragma: no-cache
 *
 * <i>(The value returned from {@link #getResponseContent()})</i></pre>
 * </dd>
 *
 * <dt><b>{@link Action#BAD_REQUEST BAD_REQUEST}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "BAD_REQUEST"}, it means
 * that the request from the client application is invalid.
 * </p>
 *
 * <p>
 * The HTTP status of the response returned to the client application should
 * be {@code "400 Bad Request"} and Authlete recommends {@code
 * "application/json"} as the content type although OAuth 2.0 specification
 * does not mention the format of the error response when the redirect URI
 * is not usable.
 * </p>
 *
 * <p>
 * {@link #getResponseContent()} returns a JSON string which describes the error,
 * so it can be used as the entity body of the response.
 * </p>
 *
 * <p>
 * The following illustrates the response which the service implementation
 * should generate and return to the client application.
 * </p>
 *
 * <pre style="border: solid 1px black; padding: 0.5em;">
 * HTTP/1.1 400 Bad Request
 * Content-Type: application/json
 * Cache-Control: no-store
 * Pragma: no-cache
 *
 * <i>(The value returned from {@link #getResponseContent()})</i></pre>
 * </dd>
 *
 *
 * <dt><b>{@link Action#LOCATION LOCATION}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "LOCATION"}, it means
 * that the request from the client application is invalid but the
 * redirect URI to which the error should be reported has been determined.
 * </p>
 *
 * <p>
 * The HTTP status of the response returned to the client application should
 * be {@code "302 Found"} and {@code "Location"} header must have a redirect
 * URI with the {@code "error"} parameter.
 * </p>
 *
 * <p>
 * {@link #getResponseContent()} returns the redirect URI which has the {@code
 * "error"} parameter, so it can be used as the value of {@code "Location"}
 * header.
 * </p>
 *
 * <p>
 * The following illustrates the response which the service implementation
 * should generate and return to the client application.
 * </p>
 *
 * <pre style="border: solid 1px black; padding: 0.5em;">
 * HTTP/1.1 302 Found
 * Location: <i>(The value returned from {@link #getResponseContent()})</i>
 * Cache-Control: no-store
 * Pragma: no-cache</pre>
 * </dd>
 *
 *
 * <dt><b>{@link Action#FORM FORM}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "FORM"}, it means
 * that the request from the client application is invalid but the
 * redirect URI to which the error should be reported has been determined,
 * and that the request contains {@code response_mode=form_post} as is
 * defined in <i>"OAuth 2.0 Form Post Response Mode"</i>.
 * </p>
 *
 * <p>
 * The HTTP status of the response returned to the client application should
 * be {@code "200 OK"} and the content type should be {@code
 * "text/html;charset=UTF-8"}.
 * </p>
 *
 * <p>
 * {@link #getResponseContent()} returns an HTML which satisfies the requirements
 * of {@code response_mode=form_post}, so it can be used as the entity body
 * of the response.
 * </p>
 *
 * <p>
 * The following illustrates the response which the service implementation
 * should generate and return to the client application.
 * </p>
 *
 * <pre style="border: solid 1px black; padding: 0.5em;">
 * HTTP/1.1 200 OK
 * Content-Type: text/html;charset=UTF-8
 * Cache-Control: no-store
 * Pragma: no-cache
 *
 * <i>(The value returned from {@link #getResponseContent()})</i></pre>
 * </dd>
 *
 *
 * <dt><b>{@link Action#NO_INTERACTION NO_INTERACTION}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "NO_INTERACTION"}, it means
 * that the request from the client application has no problem and requires
 * the service to process the request without displaying any authentication
 * or consent user interface pages. Put simply, the request contains
 * {@code prompt=none}.
 * </p>
 *
 * <p>
 * The service must follow the following steps.
 * </p>
 *
 * <blockquote>
 * <ol>
 *   <li>
 *     <p><b>[END-USER AUTHENTICATION]</b>
 *     Check whether an end-user has already logged in. If an end-user has
 *     logged in, go to the next step. Otherwise, call Authlete's
 *     {@code /auth/authorization/fail} API with {@code reason=}{@link
 *     AuthorizationFailRequest.Reason#NOT_LOGGED_IN NOT_LOGGED_IN} and use
 *     the response from the API to generate a response to the client
 *     application.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[MAX_AGE]</b>
 *     Get the value of the max age by {@link #getMaxAge()} method. The value
 *     represents the maximum authentication age which has come from {@code
 *     "max_age"} request parameter or {@code "default_max_age"} configuration
 *     parameter of the client application. If the value is 0, go to the next
 *     step ([SUBJECT]). Otherwise, follow the sub steps described below.
 *     </p>
 *     <br/>
 *     <ol style="list-style-type: lower-roman;">
 *       <li>
 *         <p>
 *         Get the time at which the end-user was authenticated. Note that
 *         this value is not managed by Authlete, meaning that it is expected
 *         that the service implementation manages the value. If the service
 *         implementation does not manage authentication time of end-users,
 *         call Authlete's {@code /auth/authorization/fail} API with {@code
 *         reason=}{@link AuthorizationFailRequest.Reason#MAX_AGE_NOT_SUPPORTED
 *         MAX_AGE_NOT_SUPPORTED} and use the response from the API to generate
 *         a response to the client application.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         Add the value of the maximum authentication age (which is represented
 *         in seconds) to the authentication time.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         Check whether the calculated value is equal to or greater than the
 *         current time. If this condition is satisfied, go to the next step
 *         ([SUBJECT]).
 *         Otherwise, call Authlete's {@code /auth/authorization/fail} API with
 *         {@code reason=}{@link AuthorizationFailRequest.Reason#EXCEEDS_MAX_AGE
 *         EXCEEDS_MAX_AGE} and use the response from the API to generate a
 *         response to the client application.
 *         </p>
 *     </ol>
 *     <br/>
 *
 *   <li>
 *     <p><b>[SUBJECT]</b>
 *     Get the value of the requested subject by {@link #getSubject()} method.
 *     The value represents an end-user who the client application expects to
 *     grant authorization. If the value is {@code null}, go to the next step
 *     ([ACRs]). Otherwise, follow the sub steps described below.
 *     </p>
 *     <br/>
 *     <ol style="list-style-type: lower-roman;">
 *       <li>
 *         <p>
 *         Compare the value of the requested subject to the current end-user.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         If they are equal, go to the next step ([ACRs]).
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         If they are not equal, call Authlete's {@code /auth/authorization/fail}
 *         API with {@code reason=}{@link AuthorizationFailRequest.Reason#DIFFERENT_SUBJECT
 *         DIFFERENT_SUBJECT} and use the response from the API to generate
 *         a response to the client application.
 *         </p>
 *     </ol>
 *     <br/>
 *
 *   <li>
 *     <p><b>[ACRs]</b>
 *     Get the value of ACRs (Authentication Context Class References) by {@link
 *     #getAcrs()} method. The value has come from (1) {@code "acr"} claim in
 *     {@code "claims"} request parameter, (2) {@code "acr_values"} request
 *     parameter, or (3) {@code "default_acr_values"} configuration parameter
 *     of the client application.
 *     </p>
 *     <br/>
 *     <p>
 *     It is ensured that all the ACRs are supported by the service implementation.
 *     In other words, it is ensured that all the ACRs are listed in the {@code
 *     "acr_values_supported"} configuration parameter of the service implementation.
 *     </p>
 *     <br/>
 *     <p>
 *     If the value of ACRs is {@code null}, go to the next step ([ISSUE]).
 *     Otherwise, follow the sub steps described below.
 *     </p>
 *     <br/>
 *     <ol style="list-style-type: lower-roman;">
 *       <li>
 *         <p>
 *         Get the ACR performed for the authentication of the current end-user.
 *         Note that this value is managed not by Authlete but by the service
 *         implementation. (If the service implementation cannot handle ACRs,
 *         it should not have listed ACRs as {@code "acr_values_supported"}.)
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         Compare the ACR value obtained in the above step to each element in
 *         the ACR array obtained by {@link #getAcrs()} method in the listed order.
 *         If the ACR value was found in the array, go to the next step ([ISSUE]).
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *         If the ACR value was not found in the ACR array (= the ACR performed
 *         for the authentication of the current end-user did not match any one
 *         of the ACRs requested by the client application), check whether one
 *         of the requested ACRs must be satisfied or not by calling {@link
 *         #isAcrEssential()} method. If this method returns {@code true}, call
 *         Authlete's {@code /auth/authorization/fail} API with {@code reason=}{@link
 *         AuthorizationFailRequest.Reason#ACR_NOT_SATISFIED ACR_NOT_SATISFIED}
 *         and use the response from the API to generate a response to the client
 *         application. Otherwise, go to the next step ([ISSUE]).
 *         </p>
 *     </ol>
 *     <br/>
 *   <li>
 *     <p><b>[ISSUE]</b>
 *     If all the above steps succeeded, the last step is to issue an authorization
 *     code, an ID token and/or an access token. (There is a special case. When
 *     {@code response_type=none}, nothing is issued.) It is performed by calling
 *     Authlete's {@code /auth/authorization/issue} API. The API requires the
 *     following parameters, which is represented as {@link AuthorizationIssueRequest}.
 *     Prepare these parameters and call the {@code /auth/authorization/issue} API.
 *     </p>
 *     <br/>
 *     <ul>
 *       <li>
 *         <p><b>[ticket]</b>
 *           This parameter represents a ticket which is exchanged with tokens
 *           at the {@code /auth/authorization/issue} endpoint.
 *           Use the value returned by {@link #getTicket()} as it is.
 *         </p>
 *         <br/>
 *       <li>
 *         <p><b>[subject]</b>
 *           This parameter represents the current end-user. It may be called
 *           "User ID", "User Account", "Login ID", etc. In any case, it is a
 *           number or a string assigned to an end-user by the service
 *           implementation. Authlete does not care about the format of the
 *           value of {@code subject}, but it must consist of only ASCII letters
 *           and its length must be equal to or less than 100.
 *         </p>
 *         <br/>
 *         <p>
 *           When {@link #getSubject()} method returns a non-null value, the
 *           value of {@code subject} parameter is necessarily identical to the
 *           value returned from the method.
 *         </p>
 *         <br/>
 *         <p>
 *           The value of this parameter will be embedded in an ID token as the
 *           value of {@code "sub"} claim. When the value of {@code "subject_type"}
 *           configuration parameter of the client is {@link
 *           com.authlete.common.types.SubjectType#PAIRWISE PAIRWISE}, the value
 *           of {@code "sub"} claim is different from the value specified here,
 *           but {@code PAIRWISE} is not supported by Authlete yet. See
 *           <a href="http://openid.net/specs/openid-connect-core-1_0.html#SubjectIDTypes"
 *           >8. Subject Identifier Types</a> of OpenID Connect Core 1.0 for
 *           details about subject types.
 *         </p>
 *         <br/>
 *       <li>
 *         <p><b>[authTime]</b>
 *           This parameter represents the time when the end-user authentication
 *           occurred. Its value is the number of seconds from 1970-01-01. The
 *           value of this parameter will be embedded in an ID token as the value
 *           of {@code "auth_time"} claim.
 *         </p>
 *         <br/>
 *       </li>
 *       <li>
 *         <p><b>[acr]</b>
 *           This parameter represents the ACR (Authentication Context Class
 *           Reference) which the authentication of the end-user satisfies.
 *           When {@link #getAcrs()} method returns a non-empty array and
 *           {@link #isAcrEssential()} method returns
 *           {@code true}, the value of this parameter must be one of the array
 *           elements. Otherwise, even {@code null} is allowed. The value of
 *           this parameter will be embedded in an ID token as the value of
 *           {@code "acr"} claim.
 *         </p>
 *         <br/>
 *       </li>
 *       <li>
 *         <p><b>[claims]</b>
 *           This parameter represents claims of the end-user. "Claims" here
 *           are pieces of information about the end-user such as {@code "name"},
 *           {@code "email"} and {@code "birthdate"}. The service implementation
 *           is required to gather claims of the end-user, format the claim values
 *           into a JSON and set the JSON string as the value of this parameter.
 *         </p>
 *         <br/>
 *         <p>
 *           The claims which the service implementation is required to gather
 *           can be obtained by {@link #getClaims()} method.
 *         </p>
 *         <br/>
 *         <p>
 *           For example, if {@link #getClaims()} method returns an array which
 *           contains {@code "name"}, {@code "email"} and {@code "birthdate"},
 *           the value of this parameter should look like the following.
 *         </p>
 *         <br/>
 *         <blockquote>
 *           <code>{"name":"John Smith","email":"john@example.com","birthdate":"1974-05-06"}</code>
 *         </blockquote>
 *         <p>
 *           {@link #getClaimsLocales()} lists the end-user's preferred languages
 *           and scripts for claim values, ordered by preference. When {@link
 *           #getClaimsLocales()} returns a non-empty array, its elements should
 *           be taken into account when the service implementation gathers claim
 *           values. Especially, note the excerpt below from
 *           <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsLanguagesAndScripts"
 *           >5.2. Claims Languages and Scripts</a> of OpenID Connect Core 1.0.
 *         </p>
 *         <blockquote>
 *           <p>
 *             <i>"When the OP determines, either through the claims_locales
 *             parameter, or by other means, that the End-User and Client are
 *             requesting Claims in only one set of languages and scripts, it
 *             is RECOMMENDED that OPs return Claims without language tags
 *             when they employ this language and script. It is also RECOMMENDED
 *             that Clients be written in a manner that they can handle and
 *             utilize Claims using language tags."</i>
 *           </p>
 *         </blockquote>
 *         <p>
 *           If {@link #getClaims()} method returns {@code null} or an empty array,
 *           the value of this parameter should be {@code null}.
 *         </p>
 *         <br/>
 *         <p>
 *           See <a href="http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims"
 *           >5.1. Standard Claims</a> of OpenID Connect Core 1.0 for claim names
 *           and their value formats. Note (1) that the service implementation may
 *           support its special claims
 *           (<a href="http://openid.net/specs/openid-connect-core-1_0.html#AdditionalClaims"
 *           >5.1.2. Additional Claims</a>) and (2) that claim names may be followed
 *           by a language tag
 *           (<a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsLanguagesAndScripts"
 *           >5.2. Claims Languages and Scripts</a>). Read the specification of
 *           <a href="http://openid.net/specs/openid-connect-core-1_0.html"
 *           >OpenID Connect Core 1.0</a> for details.
 *         </p>
 *         <br/>
 *         <p>
 *           The claim values in this parameter will be embedded in an ID token.
 *         </p>
 *       </li>
 *     </ul>
 *     <br/>
 *     <p>
 *     {@code /auth/authorization/issue} API returns a response in JSON format
 *     which can be mapped to {@link AuthorizationIssueResponse}. Use the response
 *     from the API to generate a response to the client application. See the
 *     description of {@link AuthorizationIssueResponse} for details.
 *     </p>
 * </ol>
 * </blockquote>
 * </dd>
 *
 *
 * <dt><b>{@link Action#INTERACTION INTERACTION}</b></dt>
 * <dd>
 * <p>
 * When the value of {@code "action"} is {@code "INTERACTION"}, it means
 * that the request from the client application has no problem and requires
 * the service to process the request with user interaction by an HTML form.
 * </p>
 * <p>
 * The purpose of the UI displayed to the end-user is to ask the end-user
 * to grant authorization to a client application. The items described
 * below are some points which the service implementation should take into
 * account when it builds the UI.
 * </p>
 *
 * <blockquote>
 * <ol>
 *   <li>
 *     <p><b>[DISPLAY MODE]</b>
 *       {@code AuthorizationResponse} contains {@code display} parameter.
 *       The value can be obtained by {@link #getDisplay()} method and is
 *       one of {@link Display#PAGE PAGE} (default), {@link Display#POPUP
 *       POPUP}, {@link Display#TOUCH TOUCH} and {@link Display#WAP WAP}.
 *       The meanings of the values are described in
 *       <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
 *       >3.1.2.1. Authentication Request</a> of OpenID Connect Core 1.0.
 *       Basically, the service implementation should display the UI which
 *       is suitable for the display mode, but it is okay for the service
 *       implementation to <i>"attempt to detect the capabilities of the
 *       User Agent and present an appropriate display."</i>
 *     </p>
 *     <br/>
 *     <p>
 *       It is ensured that the value returned by {@link #getDisplay()} is
 *       one of the supported displays which are specified by {@code
 *       "display_values_supported"} configuration parameter of the service.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[UI LOCALE]</b>
 *       {@code AuthorizationResponse} contains {@code "ui_locales"} parameter.
 *       The value can be obtained by {@link #getUiLocales()} and it is an
 *       array of language tag values (such as {@code "fr-CA"} and {@code
 *       "en"}) ordered by preference. The service implementation should
 *       display the UI in one of the language listed in the {@code
 *       "ui_locales"} parameter when possible.
 *     </p>
 *     <br/>
 *     <p>
 *       It is ensured that language tags returned by {@link #getUiLocales()}
 *       are contained in the list of supported UI locales which are specified
 *       by {@code "ui_locales_supported"} configuration parameter of the
 *       service.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[CLIENT INFORMATION]</b>
 *       The service implementation should show the end-user information
 *       about the client application. The information can be obtained by
 *       {@link #getClient()} method.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[SCOPES]</b>
 *       A client application requires authorization for specific permissions.
 *       In OAuth 2.0 specification, "scope" is a technical term which represents
 *       a permission. {@link #getScopes()} method returns scopes requested by
 *       the client application. The service implementation should show the
 *       end-user the scopes.
 *     </p>
 *     <br/>
 *     <p>
 *       It is ensured that scopes returned by {@link #getScopes()} are
 *       contained in the list of supported scopes which are specified by
 *       {@code "scopes_supported"} configuration parameter of the service.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[END-USER AUTHENTICATION]</b>
 *       Necessarily, the end-user must be authenticated (= must login the
 *       service) before granting authorization to the client application.
 *       Simply put, a login form is expected to be displayed for end-user
 *       authentication. The service implementation must follow the steps
 *       described below to comply with OpenID Connect. (Or just always
 *       show a login form if it's too much of a bother.)
 *     </p>
 *     <br/>
 *     <ol style="list-style-type: lower-roman;">
 *       <li>
 *         <p>
 *           Get the value of {@link #getLowestPrompt()}. The value is one
 *           of {@link Prompt#LOGIN LOGIN}, {@link Prompt#CONSENT CONSENT}
 *           and {@link Prompt#SELECT_ACCOUNT}.
 *           The meanings of the values are described in
 *           <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
 *           >3.1.2.1. Authentication Request</a> of OpenID Connect Core 1.0.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *           When the value of the lowest prompt is {@link Prompt#SELECT_ACCOUNT
 *           SELECT_ACCOUNT}, display a form to let the end-user select one of
 *           his/her accounts for login. If {@link #getSubject()} returns a
 *           non-null value, it is the end-user ID that the client application
 *           expects, so it should be set to the input field for the login ID.
 *           If {@link #getSubject()} returns null, the value returned by
 *           {@link #getLoginHint()} may be set to the input field.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *           Otherwise, when the value of the lowest prompt is {@link Prompt#LOGIN
 *           LOGIN}, display a form to let the end-user login. If {@link
 *           #getSubject()} returns a non-null value, it is the end-user ID
 *           that the client application expects, so it should be set to the
 *           input field for the login ID.
 *           If {@link #getSubject()} returns null, the value returned by
 *           {@link #getLoginHint()} may be set to the input field.
 *         </p>
 *         <br/>
 *       <li>
 *         <p>
 *           Otherwise, when the value of the lowest prompt is {@link Prompt#CONSENT
 *           CONSENT}, the service implementation can omit a login form and
 *           use the end-user who has currently logged in the service if all
 *           the conditions described below are satisfied. If any one of the
 *           conditions is not satisfied, show a login form to authenticate
 *           the end-user.
 *         </p>
 *         <br/>
 *         <ul>
 *           <li>
 *             <p>
 *               An end-user has already logged in the service.
 *             </p>
 *             <br/>
 *           <li>
 *             <p>
 *               The login ID of the current end-user matches the value returned
 *               by {@link #getSubject()}. This check should be performed only
 *               when {@link #getSubject()} returns a non-null value.
 *             </p>
 *             <br/>
 *           <li>
 *             <p>
 *               The max age, which is the number of seconds obtained by {@link
 *               #getMaxAge()} method, has not passed since the current end-user
 *               logged in the service. This check should be performed only when
 *               {@link #getMaxAge()} returns a non-zero value.
 *             </p>
 *             <br/>
 *             <p>
 *               If the service implementation does not manage authentication time
 *               of end-users (= cannot know when end-users logged in) and if
 *               {@link #getMaxAge()} returns a non-zero value, a login form
 *               should be displayed.
 *             </p>
 *             <br/>
 *           <li>
 *             <p>
 *               The ACR (Authentication Context Class Reference) of the
 *               authentication performed for the current end-user satisfies
 *               one of the ACRs listed by {@link #getAcrs()}. This check should
 *               be performed only when {@link #getAcrs()} returns a non-empty
 *               array.
 *             </p>
 *         </ul>
 *     </ol>
 *     <br/>
 *     <p>
 *       In every case, the end-user authentication must satisfy one of the ACRs
 *       listed by {@link #getAcrs()} when {@link #getAcrs()} returns a non-empty
 *       array and {@link #isAcrEssential()} returns {@code true}.
 *     </p>
 *     <br/>
 *
 *   <li>
 *     <p><b>[GRANT/DENY BUTTONS]</b>
 *       The end-user is supposed to choose either (1) to grant authorization
 *       to the client application or (2) to deny the authorization request.
 *       The UI must have UI components to accept the judgment by the user.
 *       Usually, a button to grant authorization and a button to deny the
 *       request are provided.
 *     </p>
 *     <br/>
 * </ol>
 * </blockquote>
 *
 * <p>
 * When the subject returned by {@link #getSubject()} method is not {@code null},
 * the end-user authentication must be performed for the subject, meaning that
 * the service implementation should repeatedly show a login form until the
 * subject is successfully authenticated.
 * </p>
 *
 * <p>
 * The end-user will choose either (1) to grant authorization to the client
 * application or (2) to deny the authorization request. When the end-user
 * chose to deny the authorization request, call Authlete's {@code
 * /auth/authorization/fail} API with {@code reason=}{@link
 * AuthorizationFailRequest.Reason#DENIED DENIED} and use the response from
 * the API to generate a response to the client application.
 * </p>
 *
 * <p>
 * When the end-user chose to grant authorization to the client application,
 * the service implementation has to issue an authorization code, an ID token,
 * and/or an access token to the client application. (There is a special case.
 * When {@code response_type=none}, nothing is issued.) It is performed by
 * calling Authlete's {@code /auth/authorization/issue} API. Read [ISSUE]
 * written above in the description for the case of {@code action=NO_INTERACTION}.
 * </p>
 * </dd>
 * </dl>
 *
 * @see <a href="http://tools.ietf.org/html/rfc6749">RFC 6749, OAuth 2.0</a>
 *
 * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html"
 *      >OpenID Connect Core 1.0</a>
 *
 * @see <a href="http://openid.net/specs/openid-connect-registration-1_0.html"
 *      >OpenID Connect Dynamic Client Registration 1.0</a>
 *
 * @see <a href="http://openid.net/specs/openid-connect-discovery-1_0.html"
 *      >OpenID Connect Discovery 1.0</a>
 *
 * @author Takahiko Kawasaki
 */
public class AuthorizationResponse extends ApiResponse
{
    private static final long serialVersionUID = 4L;


    /**
     * The next action that the service implementation should take.
     */
    public enum Action
    {
        /**
         * The request from the service implementation was wrong or
         * an error occurred in Authlete. The service implementation
         * should return {@code "500 Internal Server Error"} to the
         * client application.
         */
        INTERNAL_SERVER_ERROR,

        /**
         * The authorization request was wrong and the service implementation
         * should notify the client application of the error by
         * {@code "400 Bad Request"}.
         */
        BAD_REQUEST,

        /**
         * The authorization request was wrong and the service implementation
         * should notify the client application of the error by
         * {@code "302 Found"}.
         */
        LOCATION,

        /**
         * The authorization request was wrong and the service implementation
         * should notify the client application of the error by
         * {@code "200 OK"} with an HTML which triggers redirection by
         * JavaScript.
         *
         * @see <a href="http://openid.net/specs/oauth-v2-form-post-response-mode-1_0.html"
         *      >OAuth 2.0 Form Post Response Mode</a>
         */
        FORM,

        /**
         * The authorization request was valid and the service implementation
         * should issue an authorization code, an ID token and/or an access
         * token without interaction with the end-user.
         */
        NO_INTERACTION,

        /**
         * The authorization request was valid and the service implementation
         * should display UI to ask for authorization from the end-user.
         */
        INTERACTION
    }


    private static final String SUMMARY_FORMAT
        = "ticket=%s, action=%s, serviceNumber=%d, clientNumber=%d, clientId=%d, "
        + "clientSecret=%s, clientType=%s, developer=%s, display=%s, maxAge=%d, "
        + "scopes=%s, uiLocales=%s, claimsLocales=%s, claims=%s, acrEssential=%s, "
        + "acrs=%s, subject=%s, loginHint=%s, lowestPrompt=%s";


    /*
     * Do not change variable names. They must match the variable names
     * in JSONs which are exchanged between clients and Authlete server.
     */


    private Action action;
    private Service service;
    private Client client;
    private Display display;
    private int maxAge;
    private Scope[] scopes;
    private String[] uiLocales;
    private String[] claimsLocales;
    private String[] claims;
    private boolean acrEssential;
    private String[] acrs;
    private String subject;
    private String loginHint;
    private Prompt lowestPrompt;
    private String responseContent;
    private String ticket;


    /**
     * Get the next action that the service implementation should take.
     */
    public Action getAction()
    {
        return action;
    }


    /**
     * Set the next action that the service implementation should take.
     */
    public void setAction(Action action)
    {
        this.action = action;
    }


    /**
     * Get the information about the service.
     *
     * @return
     *         Information about the service.
     *
     * @since 1.23
     */
    public Service getService()
    {
        return service;
    }


    /**
     * Set the information about the service.
     *
     * @param service
     *         Information about the service.
     *
     * @since 1.23
     */
    public void setService(Service service)
    {
        this.service = service;
    }


    /**
     * Get the information about the client application which has made
     * the authorization request.
     *
     * @see <a href="http://openid.net/specs/openid-connect-registration-1_0.html"
     *      >OpenID Connect Dynamic Client Registration 1.0</a>
     */
    public Client getClient()
    {
        return client;
    }


    /**
     * Set the information about the client application which has made
     * the authorization request.
     */
    public void setClient(Client client)
    {
        this.client = client;
    }


    /**
     * Get the display mode which the client application requests
     * by {@code "display"} request parameter. When the authorization
     * request does not contain {@code "display"} request parameter,
     * this method returns {@link Display#PAGE} as the default value.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     */
    public Display getDisplay()
    {
        return display;
    }


    /**
     * Set the display mode which the client application requires
     * by {@code "display"} request parameter.
     */
    public void setDisplay(Display display)
    {
        this.display = display;
    }


    /**
     * Get the maximum authentication age which is the allowable
     * elapsed time in seconds since the last time the end-user
     * was actively authenticated by the service implementation.
     * The value comes from {@code "max_age"} request parameter
     * or {@code "default_max_age"} configuration parameter of
     * the client application. 0 may be returned which means
     * that the max age constraint does not have to be imposed.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     *
     * @see <a href="http://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata"
     *      >OpenID Connect Dynamic Client Registration 1.0, 2. Client Metadata</a>
     */
    public int getMaxAge()
    {
        return maxAge;
    }


    /**
     * Set the maximum authentication age.
     */
    public void setMaxAge(int maxAge)
    {
        this.maxAge = maxAge;
    }


    /**
     * Get the scopes which the client application requests by {@code
     * "scope"} request parameter. When the authorization request does
     * not contain {@code "scope"} request parameter, this method
     * returns a list of scopes which are marked as default by the
     * service implementation. {@code null} may be returned if the
     * authorization request does not contain valid scopes and none
     * of registered scopes is marked as default.
     *
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-3.3"
     *      >OAuth 2.0, 3.3. Access Token Scope</a>
     */
    public Scope[] getScopes()
    {
        return scopes;
    }


    /**
     * Set the scopes which the client application requests or the
     * default scopes when the authorization request does not contain
     * {@code "scope"} request parameter.
     */
    public void setScopes(Scope[] scopes)
    {
        this.scopes = scopes;
    }


    /**
     * Get the list of preferred languages and scripts for the user
     * interface. The value comes from {@code "ui_locales"} request
     * parameter.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     */
    public String[] getUiLocales()
    {
        return uiLocales;
    }


    /**
     * Set the list of preferred languages and scripts for the user
     * interface.
     */
    public void setUiLocales(String[] uiLocales)
    {
        this.uiLocales = uiLocales;
    }


    /**
     * Get the list of preferred languages and scripts for claim
     * values contained in the ID token. The value comes from
     * {@code "claims_locales"} request parameter.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsLanguagesAndScripts"
     *      >OpenID Connect Core 1.0, 5.2. Claims Languages and Scripts</a>
     */
    public String[] getClaimsLocales()
    {
        return claimsLocales;
    }


    /**
     * Set the list of preferred languages and scripts for claim
     * values contained in the ID token.
     */
    public void setClaimsLocales(String[] claimsLocales)
    {
        this.claimsLocales = claimsLocales;
    }


    /**
     * Get the list of claims that the client application requests
     * to be embedded in the ID token. The value comes from
     * {@code "scope"} and {@code "claims"} request parameters of
     * the original authorization request.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims"
     *      >OpenID Connect Core 1.0, 5.4. Requesting Claims using Scope Values</a>
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsParameter"
     *      >OpenID Connect Core 1.0, 5.5. Requesting Claims using
     *      the "claims" Request Parameter</a>
     */
    public String[] getClaims()
    {
        return claims;
    }


    /**
     * Set the list of claims that the client application requests
     * to be embedded in the ID token.
     */
    public void setClaims(String[] claims)
    {
        this.claims = claims;
    }


    /**
     * Get the flag which indicates whether the end-user authentication
     * must satisfy one of the requested ACRs.
     *
     * <p>
     * This method returns {@code true} only when the authorization
     * request from the client contains {@code "claim"} request parameter
     * and it contains an entry for {@code "acr"} claim with
     * {@code "essential":true}.
     * </p>
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#IndividualClaimsRequests"
     *      >OpenID Connect Core 1.0, 5.5.1. Individual Claims Requests</a>
     */
    public boolean isAcrEssential()
    {
        return acrEssential;
    }


    /**
     * Set the flag which indicates whether the end-user authentication
     * must satisfy one of the requested ACRs.
     */
    public void setAcrEssential(boolean essential)
    {
        this.acrEssential = essential;
    }


    /**
     * Get the list of ACRs (Authentication Context Class References)
     * requested by the client application. The value come from (1)
     * {@code "acr"} claim in {@code "claims"} request parameter, (2)
     * {@code "acr_values"} request parameter, or (3) {@code
     * "default_acr_values"} configuration parameter of the client
     * application.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsParameter"
     *      >OpenID Connect Core 1.0, 5.5. Requesting Claims using
     *      the "claims" Request Parameter</a>
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     *
     * @see <a href="http://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata"
     *      >OpenID Connect Dynamic Client Registration 1.0, 2. Client Metadata</a>
     */
    public String[] getAcrs()
    {
        return acrs;
    }


    /**
     * Set the list of ACRs (Authentication Context Class References)
     * requested by the client application.
     */
    public void setAcrs(String[] acrs)
    {
        this.acrs = acrs;
    }


    /**
     * Get the subject (= end-user's login ID) that the client
     * application requests. The value comes from {@code "sub"}
     * claim in {@code "claims"} request parameter. This method
     * may return {@code null} (probably in most cases).
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClaimsParameter"
     *      >OpenID Connect Core 1.0, 5.5. Requesting Claims using
     *      the "claims" Request Parameter</a>
     */
    public String getSubject()
    {
        return subject;
    }


    /**
     * Set the subject (= end-user's login ID) that the client
     * application requests.
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }


    /**
     * Get the value of login hint, which is specified by the client
     * application using {@code "login_hint"} request parameter.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     */
    public String getLoginHint()
    {
        return loginHint;
    }


    /**
     * Set the value of login hint, which is specified by the client
     * application using {@code "login_hint"} request parameter.
     */
    public void setLoginHint(String loginHint)
    {
        this.loginHint = loginHint;
    }


    /**
     * Get the prompt that the UI displayed to the end-user must satisfy
     * at least. The value comes from {@code "prompt"} request parameter.
     * When the authorization request does not contain {@code "prompt"}
     * parameter, this method returns {@link Prompt#CONSENT CONSENT} as
     * the default value.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#AuthRequest"
     *      >OpenID Connect Core 1.0, 3.1.2.1. Authentication Request</a>
     */
    public Prompt getLowestPrompt()
    {
        return lowestPrompt;
    }


    /**
     * Set the prompt that the UI displayed to the end-user must satisfy
     * at least.
     */
    public void setLowestPrompt(Prompt prompt)
    {
        this.lowestPrompt = prompt;
    }


    /**
     * Get the response content which can be used to generate a response
     * to the client application. The format of the value varies depending
     * on the value of {@code "action"}.
     */
    public String getResponseContent()
    {
        return responseContent;
    }


    /**
     * Set the response content which can be used to generate a response
     * to the client application.
     */
    public void setResponseContent(String content)
    {
        this.responseContent = content;
    }


    /**
     * Get the ticket which has been issued to the service implementation
     * from Authlete's {@code /auth/authorization} API. This ticket is
     * needed for {@code /auth/authorization/issue} API and
     * {@code /auth/authorization/fail} API.
     */
    public String getTicket()
    {
        return ticket;
    }


    /**
     * Set the ticket for the service implementation to call
     * {@code /auth/authorization/issue} API and
     * {@code /auth/authorization/fail} API.
     */
    public void setTicket(String ticket)
    {
        this.ticket = ticket;
    }


    /**
     * Get the summary of this instance.
     */
    public String summarize()
    {
        return String.format(SUMMARY_FORMAT,
                ticket,
                action,
                (client != null ? client.getServiceNumber() : 0),
                (client != null ? client.getNumber() : 0),
                (client != null ? client.getClientId() : 0),
                (client != null ? client.getClientSecret() : null),
                (client != null ? client.getClientType() : null),
                (client != null ? client.getDeveloper() : null),
                display,
                maxAge,
                listScopeNames(scopes),
                join(uiLocales),
                join(claimsLocales),
                join(claims),
                acrEssential,
                join(acrs),
                subject,
                loginHint,
                lowestPrompt);
    }


    private String listScopeNames(Scope[] scopes)
    {
        if (scopes == null)
        {
            return null;
        }

        if (scopes.length == 0)
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Scope scope : scopes)
        {
            sb.append(scope.getName());
            sb.append(" ");
        }

        // Remove the last " ".
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }


    private String join(String[] strings)
    {
        return Utils.join(strings, " ");
    }
}
