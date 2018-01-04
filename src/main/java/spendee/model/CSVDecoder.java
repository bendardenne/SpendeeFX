package spendee.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CSVDecoder {


  private static final int DATE_COLUMN = 0;
  private static final int CATEGORY_COLUMN = 3;
  private static final int AMOUNT_COLUMN = 4;
  private static final int NOTE_COLUMN = 6;

  public static List<Transaction> decode( Path aCsv ) throws IOException {

    Stream<String> lines = Files.lines( aCsv );

    // SKip CSV header
    lines = lines.skip( 1 );

    // Build transactions
    return lines.map( CSVDecoder::makeTransaction ).collect( toList() );
  }

  private static Transaction makeTransaction( String aCSVLine ) {
    List<String> strings = Arrays.stream( aCSVLine.split( "," ) )
                                 .map( s -> s.substring( 1, s.length() - 1 ) )
                                 .collect( Collectors.toList() );

    LocalDateTime date = LocalDateTime.parse( strings.get( DATE_COLUMN ), DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) );
    double amount = Double.parseDouble( strings.get( AMOUNT_COLUMN ) );
    String comment = strings.get( NOTE_COLUMN );
    String category = strings.get( CATEGORY_COLUMN );

    return new Transaction( amount, comment, date, category );

  }
}
