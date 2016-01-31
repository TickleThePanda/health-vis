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
  public class LocalVerifierServerException extends Exception {

    private static final long serialVersionUID = -2405362843024360581L;

    public LocalVerifierServerException() {
      super();
    }

    public LocalVerifierServerException(String message) {
      super(message);
    }

    public LocalVerifierServerException(String message, Throwable cause) {
      super(message, cause);
    }

    public LocalVerifierServerException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }

    public LocalVerifierServerException(Throwable cause) {
      super(cause);
    }

  }

  private static final String SUCCESS_HTML = "<html><body>Retrieved verifier code</body></html>";

  private static final String ERROR_HTML = "<html><body>Could not retrieve verifier code</body></html>";

  private static final Charset ASCII = StandardCharsets.US_ASCII;

  private static final int EXPECTED_PORT = 6046;
  private static final String CLIENT_ID = "229P5C";

  private static final String SCOPE = "activity";

  private static final int EXPIRATION_TIME = 2592000;

  public static String getVerificationCodeUsingSingleUseServer() throws LocalVerifierServerException {
    String verifier = null;
    try (LocalVerifierCodeServer server = new LocalVerifierCodeServer()) {
      verifier = server.getVerifier();
    }
    assert verifier != null;
    return verifier;
  }

  private ServerSocket server;

  private LocalVerifierCodeServer() throws LocalVerifierServerException {
    try {
      this.server = new ServerSocket(EXPECTED_PORT);
    } catch (final IOException e) {
      throw new LocalVerifierServerException("Could not start server.", e);
    }
  }

  @Override
  public void close() {
    try {
      this.server.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private String getVerifier() throws LocalVerifierServerException {
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
        + this.server.getLocalPort()
        + " for response from fitbit");

    Socket conn;
    try {
      conn = this.server.accept();
    } catch (final IOException e) {
      throw new LocalVerifierServerException("Could not listen for connection.", e);
    }

    final String code = this.getVerifierFromRequest(conn);
    this.sendResponse(conn, code);

    if (code == null) {
      throw new LocalVerifierServerException("Could not get the verifier from the request");
    }

    return code;

  }

  private byte[] generateHeader(byte[] response) {
    final String statusLine = "HTTP/1.1 200 OK\r\n";

    final String contentLength = "Content-Length: " + response.length + "\r\n";

    final String endOfHeader = "\r\n";

    final String header = statusLine + contentLength + endOfHeader;
    return header.getBytes(ASCII);
  }

  private byte[] generateResponse(String code) {
    final boolean success = code != null;
    final String responseString = success
        ? SUCCESS_HTML : ERROR_HTML;

    return responseString.getBytes(ASCII);
  }

  private String getVerifierFromRequest(Socket conn) throws LocalVerifierServerException {
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = null;
      do {
        line = reader.readLine();
        if (line == null) {
          throw new LocalVerifierServerException("The connection was terminated before the request was received.");
        }
        if (line.startsWith("GET")) {
          final Matcher matcher = Pattern.compile("code=(?<code>[0-9a-f]*) ").matcher(line);

          boolean found = matcher.find();
          if(!found) {
            throw new LocalVerifierServerException("Could not not extract the result from the request due to the code not being found.");
          }

          return matcher.group("code");
        }
      } while (!line.equals(""));
    } catch (final IOException e) {
      throw new LocalVerifierServerException("Could not get the verifier from the request.", e);
    }
    return null;
  }

  private void sendResponse(Socket conn, String code) throws LocalVerifierServerException {

    final byte[] response = this.generateResponse(code);

    final byte[] header = this.generateHeader(response);

    try {
      final OutputStream out = conn.getOutputStream();
      out.write(header);
      out.write(response);
      out.flush();
    } catch (final IOException e) {
      throw new LocalVerifierServerException("Could not get the verifier from the request.", e);
    }
  }

}