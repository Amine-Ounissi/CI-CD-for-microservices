package com.value.buildingblocks.xss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

public class XssFilterRequestWrapper extends HttpServletRequestWrapper {
  private final byte[] rawData;
  
  XssFilterRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    this.rawData = StreamUtils.copyToByteArray((InputStream)request.getInputStream());
  }
  
  byte[] getRawData() {
    return this.rawData;
  }
  
  public ServletInputStream getInputStream() throws IOException {
    return new DelegatingServletInputStream(new ByteArrayInputStream(this.rawData));
  }
  
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader((InputStream)getInputStream()));
  }
  
  private class DelegatingServletInputStream extends ServletInputStream {
    private final InputStream sourceStream;
    
    private boolean finished = false;
    
    private DelegatingServletInputStream(InputStream sourceStreamIn) {
      this.sourceStream = sourceStreamIn;
    }
    
    public int read() throws IOException {
      int data = this.sourceStream.read();
      if (data == -1)
        this.finished = true; 
      return data;
    }
    
    public boolean isFinished() {
      return this.finished;
    }
    
    public boolean isReady() {
      return true;
    }
    
    public void setReadListener(ReadListener listener) {
      throw new UnsupportedOperationException();
    }
  }
}
