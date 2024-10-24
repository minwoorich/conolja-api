package site.javaghost.conolja.common.security.jwt.mock;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class MockServletInputStream extends ServletInputStream {
  private final ByteArrayInputStream inputStream;

  @Override
  public boolean isFinished() {
    return inputStream.available() == 0;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    throw new UnsupportedOperationException("구현 안 됨");
  }

  @Override
  public int read() throws IOException {
    return inputStream.read();
  }
}
