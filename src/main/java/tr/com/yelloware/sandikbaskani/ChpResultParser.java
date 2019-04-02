package tr.com.yelloware.sandikbaskani;

import java.io.IOException;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tr.com.yelloware.sandikbaskani.http.ChpSession;
import tr.com.yelloware.sandikbaskani.model.BallotBox;
import tr.com.yelloware.sandikbaskani.model.BallotBoxResult;
import tr.com.yelloware.sandikbaskani.model.City;
import tr.com.yelloware.sandikbaskani.model.District;
import tr.com.yelloware.sandikbaskani.model.type.PartyType;
import tr.com.yelloware.sandikbaskani.validation.BallotBoxResultValidator;

@UtilityClass
@Slf4j
public class ChpResultParser {

  private static final String MD_COL_SEP = "\t|\t";

  public static void main(String[] args) throws IOException, InterruptedException {
    ChpSession chpSession = new ChpSession();
    chpSession.openMainPage();
    List<City> cities = chpSession.listCities();
    log.info("Toplam {} adet il bulundu", cities.size());
    String cityCodeOfIstanbul = "34";
    int boxCount = 0;
    Long akpCount = 0L;
    Long chpCount = 0L;
    List<District> districtList = chpSession.listDistricts(cityCodeOfIstanbul);
    try {
      for (District district : districtList) {
        List<BallotBox> ballotBoxList = chpSession.listBallotBoxes(cityCodeOfIstanbul, district.getCode());
        log.info("<details>");
        log.info("<summary>{} ({} adet sandık)</summary>", district.getText(), ballotBoxList.size());
        boolean notValidBoxExists = false;
        for (BallotBox bb : ballotBoxList) {
          BallotBoxResult result = chpSession.getBallotBoxResult(cityCodeOfIstanbul, district.getCode(), bb);
          boxCount++;
          BallotBoxResultValidator.validate(result);
          if (result.getValidationErrorList().isEmpty()) {
            continue;
          }
          notValidBoxExists = true;
          log.info("* {} sandık sonuçları:", result.getTitle());
          log.info("    * Ysk zamanı            : {}", result.getYskReceiveTime());
          log.info("    * Kullanılan oy sayısı  : {}", result.getTotalVoteCount());
          log.info("    * Geçerli oy sayısı     : {}", result.getValidVoteCount());
          log.info("    * Geçersiz oy sayısı    : {}", result.getNotValidVoteCount());
          log.info("    * İşlenmemiş oy sayısı  : {}", result.getNotMatchVoteCount());
          log.info("    * CHP                   : {}", result.getChpVoteCount());
          log.info("    * AKP                   : {}", result.getAmpulVoteCount());
          log.info("    * Hata                  : {}", result.getValidationErrorList().get(0));
          log.info("----------");
          if (PartyType.AKP.equals(result.getNotMatchVoteParty())) {
            akpCount += result.getNotMatchVoteCount();
          } else if (PartyType.CHP.equals(result.getNotMatchVoteParty())) {
            chpCount += result.getNotMatchVoteCount();
          }
        }
        if (!notValidBoxExists) {
          log.info("<p>Sandık sonuçlarında sorun bulunmamaktadır</p>");
        }
        log.info("</details>");
      }
    } finally {
      log.info("Toplam {} adet sandık bulundu", boxCount);
      log.info("AKP nin olası işlenmemiş oy toplamı {}", akpCount);
      log.info("CHP nin olası işlenmemiş oy toplamı {}", chpCount);
    }

  }
}
