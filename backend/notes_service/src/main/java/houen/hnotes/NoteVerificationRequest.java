package houen.hnotes;

import java.io.Serializable;

public class NoteVerificationRequest implements Serializable {

  private Integer id;
  private String title;
  private String content;

  public NoteVerificationRequest() {}

  public NoteVerificationRequest(Integer id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  public Integer getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }
}
