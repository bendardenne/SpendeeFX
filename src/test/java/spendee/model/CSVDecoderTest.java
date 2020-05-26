package spendee.model;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class CSVDecoderTest {

  @Test
  void decode() throws IOException, URISyntaxException {
    URI path = CSVDecoderTest.class.getClassLoader().getResource( "test.csv" ).toURI();
    List<Transaction> decoded = CSVDecoder.decode( Paths.get( path ) );

    assert (decoded.size() == 10);
  }

}
