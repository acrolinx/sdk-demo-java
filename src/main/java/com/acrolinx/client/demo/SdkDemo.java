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
import com.acrolinx.client.sdk.check.CheckOptions;
import com.acrolinx.client.sdk.check.CheckRequest;
import com.acrolinx.client.sdk.check.CheckResult;
import com.acrolinx.client.sdk.check.ReportType;
import com.acrolinx.client.sdk.exceptions.AcrolinxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class SdkDemo {
    private static final String ACROLINX_URL = "https://test-ssl.acrolinx.com";
    private static final String CLIENT_SIGNATURE = "SW50ZWdyYXRpb25EZXZlbG9wbWVudERlbW9Pbmx5";
    private static final String CLIENT_VERSION = "1.2.3.666";
    private static final String CLIENT_LOCALE = "en";

    public static void main(String[] args) throws URISyntaxException, InterruptedException, AcrolinxException {
        AcrolinxEndpoint acrolinxEndpoint = new AcrolinxEndpoint(new URI(ACROLINX_URL), CLIENT_SIGNATURE,
                CLIENT_VERSION, CLIENT_LOCALE);

        SignInSuccess signInSuccess = acrolinxEndpoint.signInInteractive(signInUrl -> {
            System.out.println("Please sign-in at " + signInUrl);
        });

        AccessToken accessToken = signInSuccess.getAccessToken();

        CheckOptions checkOptions = CheckOptions.getBuilder()
                .withContentFormat("TEXT")
                .withGenerateReportTypes(Collections.singletonList(ReportType.scorecard))
                .build();

        CheckResult checkResult = acrolinxEndpoint.checkAndGetResult(accessToken,
                CheckRequest.ofDocumentContent("This textt has an errorr.").setCheckOptions(checkOptions).build(),
                progress -> {
                    System.out.println("Progress: " + progress.getPercent() + "% (" + progress.getMessage() + ")");
                }
        );

        System.out.println("Score: " + checkResult.getQuality().getScore());
        System.out.println("Status: " + checkResult.getQuality().getStatus());
        System.out.println("Scorecard: " + checkResult.getReport(ReportType.scorecard).getLink());
    }
}
