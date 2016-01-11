package uk.co.ticklethepanda.fitbit.webapi.verifier;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.ticklethepanda.fitbit.webapi.FitbitApi;

public class LocalVerifierCodeServer implements Closeable {
  private static final String SUCCESS_HTML = "<html><body>Retrieved verifier code</body></html>";
  private static final String ERROR_HTML = "<html><body>Could not retrieve verifier code</body></html>";

  public class LocalVerifierServerException extends Exception {

    private static final long serialVersionUID = -2405362843024360581L;

    public LocalVerifierServerException() {
      super();
    }

    public LocalVerifierServerException(String message) {
      super(message);
    }

    public LocalVerifierServerException(Throwable cause) {
      super(cause);
    }

    public LocalVerifierServerException(String message, Throwable cause) {
      super(message, cause);
    }

    public LocalVerifierServerException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }

  }

  private static final Charset ASCII = StandardCharsets.US_ASCII;

  private static final int EXPECTED_PORT = 6046;
  private ServerSocket server;

  public LocalVerifierCodeServer() throws LocalVerifierServerException {
    try {
      this.server = new ServerSocket(EXPECTED_PORT);
    } catch (IOException e) {
      throw new LocalVerifierServerException("Could not start server.", e);
    }
  }

  public String getVerifier() throws LocalVerifierServerException {
    try {
      Desktop.getDesktop().browse(new URI(FitbitApi.AUTHORIZE_URL
          + "?response_type=code"
          + "&client_id=" + CLIENT_ID
          + "&scope=" + SCOPE
          + "&expires_in=" + EXPIRATION_TIME));
    } catch (IOException | URISyntaxException e1) {
      throw new LocalVerifierServerException("Could not open authorize URL in browser", e1);
    }

    System.out.println("listening on port "
        + server.getLocalPort()
        + " for response from fitbit");

    Socket conn;
    try {
      conn = server.accept();
    } catch (IOException e) {
      throw new LocalVerifierServerException("Could not listen for connection.", e);
    }

    String code = getVerifierFromRequest(conn);
    sendResponse(conn, code);

    if (code == null) {
      throw new LocalVerifierServerException("Could not get the verifier from the request");
    }

    return code;

  }

  private void sendResponse(Socket conn, String code) throws LocalVerifierServerException {

    byte[] response = generateResponse(code);

    byte[] header = generateHeader(response);

    try {
      OutputStream out = conn.getOutputStream();
      out.write(header);
      out.write(response);
      out.flush();
    } catch (IOException e) {
      throw new LocalVerifierServerException("Could not get the verifier from the request.", e);
    }
  }

  private byte[] generateResponse(String code) {
    boolean success = code != null;
    String responseString = success
        ? SUCCESS_HTML : ERROR_HTML;

    return responseString.getBytes(ASCII);
  }

  private byte[] generateHeader(byte[] response) {
    String statusLine = "HTTP/1.1 200 OK\r\n";

    String contentLength = "Content-Length: " + response.length + "\r\n";

    String endOfHeader = "\r\n";

    String header = statusLine + contentLength + endOfHeader;
    return header.getBytes(ASCII);
  }

  private String getVerifierFromRequest(Socket conn) throws LocalVerifierServerException {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = null;
      do {
        line = reader.readLine();
        if (line == null) {
          throw new LocalVerifierServerException("The connection was terminated before the request was recieved.");
        }
        if (line.startsWith("GET")) {
          Matcher matcher = Pattern.compile("code=(?<code>[0-9a-f]*) ").matcher(line);
          matcher.find();
          String code = matcher.group("code");

          return code;
        }
      } while (!line.equals(""));
    } catch (IOException e) {
      throw new LocalVerifierServerException("Could not get the verifier from the request.", e);
    }
    return null;
  }

  public static String CLIENT_ID = "229P5C";
  public static String SCOPE = "activity";
  public static int EXPIRATION_TIME = 2592000;

  @Override
  public void close() {
    try {
      server.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getVerificationCodeUsingSingleUseServer() throws LocalVerifierServerException {
    String verifier = null;
    try (LocalVerifierCodeServer server = new LocalVerifierCodeServer()) {
      verifier = server.getVerifier();
    }
    assert verifier != null;
    return verifier;
  }

}