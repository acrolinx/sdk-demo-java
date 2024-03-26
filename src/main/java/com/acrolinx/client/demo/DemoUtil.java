/* Copyright (c) 2023 Acrolinx GmbH */
package com.acrolinx.client.demo;

import com.acrolinx.client.sdk.AccessToken;
import com.acrolinx.client.sdk.AcrolinxEndpoint;
import com.acrolinx.client.sdk.SignInSuccess;
import com.acrolinx.client.sdk.check.CheckOptions;
import com.acrolinx.client.sdk.check.CheckRequest;
import com.acrolinx.client.sdk.check.CheckRequest.ContentEncoding;
import com.acrolinx.client.sdk.check.CheckRequestBuilder;
import com.acrolinx.client.sdk.check.CheckResult;
import com.acrolinx.client.sdk.check.ReportType;
import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import com.acrolinx.client.sdk.platform.Capabilities;
import com.acrolinx.client.sdk.platform.GuidanceProfile;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DemoUtil {
  private static final String ACROLINX_URL = "https://partner-dev.internal.acrolinx.sh";
  private static final String CLIENT_LOCALE = "en";

  /**
   * The client signature as configured in the Acrolinx license. You'll get the signature for your
   * integration after a successful certification meeting.
   *
   * @see <a href=
   *     "https://support.acrolinx.com/hc/en-us/sections/10211640774162-Custom-Integrations">Custom
   *     Integrations</a>
   */
  private static final String CLIENT_SIGNATURE = "SW50ZWdyYXRpb25EZXZlbG9wbWVudERlbW9Pbmx5";

  private static final String CLIENT_VERSION = "1.2.3";
  private static final Logger LOGGER = LoggerFactory.getLogger(DemoUtil.class);

  static void checkDocxFile()
      throws AcrolinxException, InterruptedException, URISyntaxException, IOException {
    AcrolinxEndpoint acrolinxEndpoint =
        new AcrolinxEndpoint(
            new URI(ACROLINX_URL), CLIENT_SIGNATURE, CLIENT_VERSION, CLIENT_LOCALE);

    AccessToken accessToken = signInInteractive(acrolinxEndpoint);
    GuidanceProfile guidanceProfile = getGuidanceProfiles(acrolinxEndpoint, accessToken);
    CheckOptions checkOptions = createCheckOptions(guidanceProfile, "AUTO");
    CheckRequest checkRequest = createDocxCheckRequest(checkOptions);

    checkAndLogResult(acrolinxEndpoint, accessToken, checkRequest);
  }

  static void checkSimpleText() throws AcrolinxException, InterruptedException, URISyntaxException {
    AcrolinxEndpoint acrolinxEndpoint =
        new AcrolinxEndpoint(
            new URI(ACROLINX_URL), CLIENT_SIGNATURE, CLIENT_VERSION, CLIENT_LOCALE);

    AccessToken accessToken = signInInteractive(acrolinxEndpoint);

    GuidanceProfile guidanceProfile = getGuidanceProfiles(acrolinxEndpoint, accessToken);
    CheckOptions checkOptions = createCheckOptions(guidanceProfile, "TEXT");
    CheckRequest checkRequest = createTxtCheckRequest(checkOptions);

    checkAndLogResult(acrolinxEndpoint, accessToken, checkRequest);
  }

  private static void checkAndLogResult(
      AcrolinxEndpoint acrolinxEndpoint, AccessToken accessToken, CheckRequest checkRequest)
      throws AcrolinxException {
    CheckResult checkResult =
        acrolinxEndpoint.check(
            accessToken,
            checkRequest,
            progress ->
                LOGGER.info("Progress: {}% ({})", progress.getPercent(), progress.getMessage()));

    LOGGER.info("Score: {}", checkResult.getQuality().getScore());
    LOGGER.info("Status: {}", checkResult.getQuality().getStatus());
    LOGGER.info("Scorecard: {}", checkResult.getReport(ReportType.scorecard).getLink());
  }

  /**
   * If you want to check a document without a content reference {@link
   * CheckRequestBuilder#withContentReference}, you should set the content format to make sure it
   * will be processed correctly.
   */
  private static CheckOptions createCheckOptions(
      GuidanceProfile guidanceProfile, String contentFormat) {
    return CheckOptions.getBuilder()
        .withContentFormat(contentFormat)
        .withGuidanceProfileId(guidanceProfile.getId())
        .withGenerateReportTypes(List.of(ReportType.scorecard))
        .build();
  }

  private static CheckRequest createDocxCheckRequest(CheckOptions checkOptions)
      throws IOException, URISyntaxException {
    String wordDocumentName = "/document.docx";
    byte[] fileContents =
        Files.readAllBytes(Path.of(DemoUtil.class.getResource(wordDocumentName).toURI()));
    return CheckRequest.ofDocumentContent(Base64.getEncoder().encodeToString(fileContents))
        .withContentEncoding(ContentEncoding.base64)
        .withContentReference(wordDocumentName)
        .withCheckOptions(checkOptions)
        .build();
  }

  private static CheckRequest createTxtCheckRequest(CheckOptions checkOptions) {
    return CheckRequest.ofDocumentContent("This textt has an errorr.")
        .withCheckOptions(checkOptions)
        .build();
  }

  /**
   * You need to configure a Guidance Profile to make sure that the document is checked in the
   * correct language and with the correct settings. In this example we choose the first English
   * Guidance Profile.
   */
  private static GuidanceProfile getGuidanceProfiles(
      AcrolinxEndpoint acrolinxEndpoint, AccessToken accessToken) throws AcrolinxException {
    Capabilities capabilities = acrolinxEndpoint.getCapabilities(accessToken);
    return capabilities.getCheckingCapabilities().getGuidanceProfiles().stream()
        .filter(guidanceProfile -> guidanceProfile.getLanguage().getId().startsWith("en"))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Can't find an English guidance profile."));
  }

  /**
   * In order to use methods that require an access token, you need to get an access token first.
   * Alternatively to signInInteractive you might get an API Token in the Acrolinx Dashboard or use
   * {@link AcrolinxEndpoint#signInWithSSO}.
   *
   * @see <a href="https://github.com/acrolinx/platform-api#authentication">Acrolinx Platform
   *     API</a>
   * @see <a href=
   *     "https://github.com/acrolinx/acrolinx-coding-guidance/blob/main/topics/configuration.md#authentication">Authentication
   *     and Setting the Acrolinx URL</a>
   */
  private static AccessToken signInInteractive(AcrolinxEndpoint acrolinxEndpoint)
      throws AcrolinxException, InterruptedException {
    SignInSuccess signInSuccess =
        acrolinxEndpoint.signInInteractive(
            urlString -> LOGGER.info("Please sign in at: {}", urlString));
    return signInSuccess.getAccessToken();
  }

  private DemoUtil() {
    throw new IllegalStateException();
  }
}
