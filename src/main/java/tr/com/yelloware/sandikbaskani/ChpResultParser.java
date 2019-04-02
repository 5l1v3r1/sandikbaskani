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
        log.info("İlçe {}: Sandık Toplam {}", district, ballotBoxList.size());
        for (BallotBox bb : ballotBoxList) {
          BallotBoxResult result = chpSession.getBallotBoxResult(cityCodeOfIstanbul, district.getCode(), bb);
          boxCount++;
          BallotBoxResultValidator.validate(result);
          if (result.getValidationErrorList().isEmpty()) {
            continue;
          }
          log.info("{} sandık sonuçları:", result.getTitle());
          log.info("\tYsk zamanı {}", result.getYskReceiveTime());
          log.info("\tKullanılan oy sayısı {}", result.getTotalVoteCount());
          log.info("\tGeçerli oy sayısı {}", result.getValidVoteCount());
          log.info("\tGeçersiz oy sayısı {}", result.getNotValidVoteCount());
          log.info("\tİşlenmemiş oy sayısı {}", result.getNotMatchVoteCount());
          log.info("\tCHP {}", result.getChpVoteCount());
          log.info("\tAKP {}", result.getAmpulVoteCount());
          log.info("\tHatalar:");
          if (PartyType.AKP.equals(result.getNotMatchVoteParty())) {
            akpCount += result.getNotMatchVoteCount();
          } else if (PartyType.CHP.equals(result.getNotMatchVoteParty())) {
            chpCount += result.getNotMatchVoteCount();
          }
          for (String err : result.getValidationErrorList()) {
            log.info("\t**{}", err);
          }
        }
      }
    } finally {
      log.info("Toplam {} adet sandık bulundu", boxCount);
      log.info("AKP nin olası işlenmemiş oy toplamı {}", akpCount);
      log.info("CHP nin olası işlenmemiş oy toplamı {}", chpCount);
    }

  }
}
