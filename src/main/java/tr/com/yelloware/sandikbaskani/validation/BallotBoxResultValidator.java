package tr.com.yelloware.sandikbaskani.validation;

import java.util.ArrayList;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import tr.com.yelloware.sandikbaskani.model.BallotBoxResult;
import tr.com.yelloware.sandikbaskani.model.type.PartyType;
import tr.com.yelloware.sandikbaskani.util.NumberUtil;

@UtilityClass
public class BallotBoxResultValidator {

  public static void validate(BallotBoxResult result) {
    result.setValidationErrorList(new ArrayList<>());
    BallotBoxResultValidator.validateTotalVoteCount(result);
    boolean partySumValid = BallotBoxResultValidator.validatePartySums(result);
    result.getValidationErrorList().clear();
    if (!partySumValid) {
      validateZeroVote(result);
    }
  }

  private static boolean validateTotalVoteCount(BallotBoxResult result) {
    if (NumberUtil.notEquals(result.getTotalVoteCount(), result.getPartiesSum())) {
      result.getValidationErrorList().add("Toplam oy sayısı ile , geçerli ve geçersiz oy sayıları toplamı farklı");
      return false;
    }
    return true;
  }

  private static boolean validatePartySums(BallotBoxResult result) {
    Long validVoteCount = result.getValidVoteCount();
    if (NumberUtil.notEquals(validVoteCount, result.getPartiesSum())) {
      result.getValidationErrorList().add(
        "Tüm partilerin toplam oy sayıları ile toplam geçerli oy sayısı farklı, topam oy : " + validVoteCount + ",partiler : " + result.getPartiesSum());
      return false;
    }
    return true;
  }

  private static void validateZeroVote(BallotBoxResult result) {
    if (!NumberUtil.isGreaterThenZero(result.getAmpulVoteCount())) {
      result.getValidationErrorList().add("AKP oyları sıfır");
      result.setNotMatchVoteParty(PartyType.AKP);
    }
    if (!NumberUtil.isGreaterThenZero(result.getChpVoteCount())) {
      if (Objects.isNull(result.getNotMatchVoteParty())) {
        result.getValidationErrorList().add("CHP oyları sıfır");
        result.setNotMatchVoteParty(PartyType.CHP);
      }
    }
  }

}
