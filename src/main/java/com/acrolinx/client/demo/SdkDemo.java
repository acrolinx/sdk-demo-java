/*
 * Copyright 2019-present Acrolinx GmbH
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

package com.acrolinx.client.demo;

import com.acrolinx.client.sdk.AccessToken;
import com.acrolinx.client.sdk.AcrolinxEndpoint;
import com.acrolinx.client.sdk.SignInSuccess;
import com.acrolinx.client.sdk.check.*;
import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import com.acrolinx.client.sdk.platform.Capabilities;
import com.acrolinx.client.sdk.platform.GuidanceProfile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

public class SdkDemo {
    private static final String ACROLINX_URL = "https://partner-dev.internal.acrolinx.sh";

    /**
     * The signature as configured in the Acrolinx license. You'll get the signature
     * for your integration after a successful certification meeting. See:
     * <a href="https://docs.acrolinx.com/customintegrations">...</a>
     */
    private static final String SIGNATURE = "SW50ZWdyYXRpb25EZXZlbG9wbWVudERlbW9Pbmx5";

    private static final String CLIENT_VERSION = "1.2.3.666";
    private static final String CLIENT_LOCALE = "en";

    public static void main(String[] args) throws URISyntaxException, InterruptedException, AcrolinxException {
        AcrolinxEndpoint acrolinxEndpoint = new AcrolinxEndpoint(new URI(ACROLINX_URL), SIGNATURE, CLIENT_VERSION,
                CLIENT_LOCALE);

        /**
         * In order to use methods that require an access token, you need to get an
         * access token first. Alternatively to signInInteractive you might get an API
         * Token in the Acrolinx Dashboard or use
         * {@link AcrolinxEndpoint#signInWithSSO}. See
         * https://github.com/acrolinx/platform-api#authentication
         */
        SignInSuccess signInSuccess = acrolinxEndpoint.signInInteractive(signInUrl ->
                System.out.println("Please sign in at " + signInUrl));
        // Alternatively use SSO to sign in.
        // See:
        // https://github.com/acrolinx/acrolinx-coding-guidance/blob/master/topics/configuration.md#authentication
        // SignInSuccess signInSuccess = acrolinxEndpoint.signInWithSSO(SSO_TOKEN,
        // DOCUMENT_AUTHOR_USER_NAME);
        AccessToken accessToken = signInSuccess.getAccessToken();

        /**
         * You need to configure a Guidance Profile to make sure that the document is
         * checked in the correct language and with the correct settings. In this
         * example we choose the first english Guidance Profile.
         */
        Capabilities capabilities = acrolinxEndpoint.getCapabilities(accessToken);
        Optional<GuidanceProfile> englishGuidanceProfile = capabilities.getCheckingCapabilities().getGuidanceProfiles()
                .stream().filter(it -> it.getLanguage().getId().startsWith("en")).findFirst();

        if (englishGuidanceProfile.isEmpty()) {
            throw new IllegalStateException("Can't find an english guidance profile.");
        }

        /**
         * If you want to check a document without a content reference
         * {@link CheckRequestBuilder#withContentReference}, you should set the content
         * format to make sure it will be processed correctly.
         */
        CheckOptions checkOptions = CheckOptions.getBuilder().withContentFormat("TEXT")
                .withGuidanceProfileId(englishGuidanceProfile.get().getId())
                .withGenerateReportTypes(Collections.singletonList(ReportType.scorecard)).build();

        CheckResult checkResult = acrolinxEndpoint.check(accessToken,
                CheckRequest.ofDocumentContent("This textt has an errorr.").withCheckOptions(checkOptions).build(),
                progress -> System.out.println("Progress: " + progress.getPercent() + "% (" + progress.getMessage() + ")"));

        System.out.println("Score: " + checkResult.getQuality().getScore());
        System.out.println("Status: " + checkResult.getQuality().getStatus());
        System.out.println("Scorecard: " + checkResult.getReport(ReportType.scorecard).getLink());
    }
}
