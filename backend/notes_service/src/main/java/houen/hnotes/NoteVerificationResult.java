package houen.hnotes;

import java.io.Serializable;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NoteVerificationResult implements Serializable {

  private static ObjectMapper objectMapper = new ObjectMapper();

  private String id;
  private String result;

  public NoteVerificationResult() {}

  public NoteVerificationResult(String id, String result) {
    this.id = id;
    this.result = result;
  }

  public String getId() {
    return id;
  }

  public String getResult() {
    return result;
  }

  public String toJSONString() {
    return new JSONObject(this).toString();
  }

  public static NoteVerificationResult fromJSON(String json) {
    try {
      var result = (NoteVerificationResult)objectMapper.readValue(json, NoteVerificationResult.class);

      // TODO: this should be somehow automated!
      if(result.getId() == null || result.getResult() == null) {
        return null;
      }

      return result;
    } catch(Exception e) {
      return null;
    }
  }
}
