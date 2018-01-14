package spendee.util;

import spendee.model.Transaction;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class HashtagUtil {

  private static final Pattern HASHTAG = Pattern.compile( "#\\p{Alnum}+" );

  public static Map<String, Long> extractHashtags( List<Transaction> aList ) {
    return aList.stream()
                .map( Transaction::getNote )
                .map( HashtagUtil::extractHashtags )
                .flatMap( Collection::stream )
                .collect( groupingBy( Function.identity(), counting() ) );
  }


  public static Collection<String> extractHashtags( String string ) {
    Set<String> found = new HashSet<>();
    Matcher matcher = HASHTAG.matcher( string );
    while ( matcher.find() ) {
      found.add( matcher.group() );
    }
    return found;
  }
}
