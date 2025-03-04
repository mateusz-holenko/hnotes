package houen.hnotes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Note {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String title;

  private String content;

  public Integer getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public String getContent() {
    return this.content;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
