package tr.com.yelloware.sandikbaskani.util;

import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {

  public static Long toLong(String value) {
    try {
      return Long.valueOf(value);
    } catch (Exception e) {
      return null;
    }
  }

  public static Long sum(Long... values) {
    Long result = 0L;
    for (Long val : values) {
      result += Objects.nonNull(val) ? val : 0L;
    }
    return result;
  }

  public static boolean equals(Long value1, Long value2) {
    if (Objects.isNull(value1) && Objects.isNull(value2)) {
      return true;
    }
    if (Objects.nonNull(value1) && Objects.nonNull(value2)) {
      return value1.compareTo(value2) == 0;
    }
    return false;
  }

  public static boolean notEquals(Long value1, Long value2) {
    return !equals(value1, value2);
  }

  public static boolean isGreaterThenZero(Long value) {
    if (Objects.isNull(value)) {
      return false;
    }
    return value.compareTo(0L) > 0;
  }
}
