package com.kakaopay.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import static com.kakaopay.constant.SprinklingConstant.DECISION_LOWER_ALPHABET_TYPE;
import static com.kakaopay.constant.SprinklingConstant.DECISION_NUMBER_TYPE;
import static com.kakaopay.constant.SprinklingConstant.DECISION_UPPER_ALPHABET_TYPE;
import static com.kakaopay.constant.SprinklingConstant.TOKEN_LENGTH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtils {

  public static String generateToken() {

    StringBuilder tokenBuilder = new StringBuilder();
    List<Integer> decisionInts = getRandomDecisionInt();

    for (Integer decisionInt : decisionInts) {
      if (decisionInt == DECISION_NUMBER_TYPE) {
        tokenBuilder.append(getSingleNumber());
      } else if (decisionInt == DECISION_UPPER_ALPHABET_TYPE) {
        tokenBuilder.append(getSingleUpperAlphabet());
      } else if (decisionInt == DECISION_LOWER_ALPHABET_TYPE) {
        tokenBuilder.append(getSingleLowerAlphabet());
      }
    }

    return tokenBuilder.toString();
  }

  public static long generateRandomMoney(long remainAmount, long remainPeople) {

    long min = 1;
    long max = remainAmount - remainPeople + 1; // 한명당 최소 1원은 받을 수 있도록 금액을 남겨야 한다
    return min == max ? 1 : new SecureRandom().longs(1, min, max).sum();
  }

  private static List<Integer> getRandomDecisionInt() {

    return new SecureRandom()
        .ints(TOKEN_LENGTH, DECISION_NUMBER_TYPE, DECISION_LOWER_ALPHABET_TYPE + 1)
        .boxed()
        .collect(Collectors.toList());
  }

  private static StringBuilder getSingleNumber() {
    return getSingleRandomChar('0', '9');
  }

  private static StringBuilder getSingleUpperAlphabet() {
    return getSingleRandomChar('A', 'Z');
  }

  private static StringBuilder getSingleLowerAlphabet() {
    return getSingleRandomChar('a', 'z');
  }

  private static StringBuilder getSingleRandomChar(char min, char max) {

    return new SecureRandom()
        .ints(1, min, max + 1)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);
  }
}
