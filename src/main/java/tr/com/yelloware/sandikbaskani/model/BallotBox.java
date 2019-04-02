package tr.com.yelloware.sandikbaskani.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BallotBox extends KeyValueModel {

  private BallotBoxResult result;

  public BallotBox(String text, String code) {
    super(text, code);
  }

}
