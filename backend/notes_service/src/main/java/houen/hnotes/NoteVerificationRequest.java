package houen.hnotes;

import java.io.Serializable;

public class NoteVerificationRequest implements Serializable {

  private String id;
  private String title;
  private String content;

  public NoteVerificationRequest() {}

  public NoteVerificationRequest(String id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }
}
