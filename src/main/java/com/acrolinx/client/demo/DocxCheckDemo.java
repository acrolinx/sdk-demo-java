package com.acrolinx.client.demo;

import com.acrolinx.client.sdk.exceptions.AcrolinxException;
import java.io.IOException;
import java.net.URISyntaxException;

public class DocxCheckDemo {
  public static void main(String[] args)
      throws URISyntaxException, InterruptedException, AcrolinxException, IOException {
    DemoUtil.checkDocxFile();
  }
}
