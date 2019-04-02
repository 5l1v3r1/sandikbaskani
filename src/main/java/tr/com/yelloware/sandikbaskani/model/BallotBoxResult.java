package tr.com.yelloware.sandikbaskani.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import tr.com.yelloware.sandikbaskani.model.type.PartyType;

@Data
@Builder
public class BallotBoxResult {

  private String title;

  private String address;

  private String yskReceiveTime;

  private Long registeredVoterCount;

  private Long totalVoteCount;

  private Long validVoteCount;

  private Long notValidVoteCount;

  private Long notMatchVoteCount;

  private Long partiesSum;

  private Long chpVoteCount;

  private Long ampulVoteCount;

  private Long saadetVoteCount;

  private Long dspVoteCount;

  private Long independentVoteCount;

  private List<String> validationErrorList;

  private PartyType notMatchVoteParty;

}
