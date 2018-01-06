package spendee.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CSVDecoder {


  private static final int DATE_COLUMN = 0;
  private static final int CATEGORY_TYPE_COLUMN = 2;
  private static final int CATEGORY_COLUMN = 3;
  private static final int AMOUNT_COLUMN = 4;
  private static final int NOTE_COLUMN = 6;

  public static List<Transaction> decode( Path aCsv ) throws IOException {
    Stream<String> lines = Files.lines( aCsv ).skip( 1 );  //Skip CSV header

    return lines.map( CSVDecoder::makeTransaction ).collect( toList() );
  }

  private static Transaction makeTransaction( String aCSVLine ) {
    List<String> strings = Arrays.stream( aCSVLine.split( "," ) )
                                 .map( s -> s.substring( 1, s.length() - 1 ) )    // Remove enclosing quotes
                                 .collect( Collectors.toList() );

    ZonedDateTime date = LocalDateTime
        .parse( strings.get( DATE_COLUMN ), DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) ).atZone(
            ZoneId.systemDefault() );

    double amount = Double.parseDouble( strings.get( AMOUNT_COLUMN ) );
    String comment = strings.get( NOTE_COLUMN );

    Category category = new Category( strings.get( CATEGORY_COLUMN ),
                                      Category.Type.valueOf( strings.get( CATEGORY_TYPE_COLUMN ).toUpperCase() ) );

    System.out.println(category.getName());
    return new Transaction( amount, comment, date, category );

  }
}
