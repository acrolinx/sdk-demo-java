/* Copyright (c) 2023 Acrolinx GmbH */
package com.acrolinx.client.demo;

import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import java.net.URISyntaxException;

public class SimpleStringCheckDemo {
  public static void main(String[] args)
      throws URISyntaxException, InterruptedException, AcrolinxException {
    DemoUtil.checkSimpleText();
  }
}
