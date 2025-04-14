package houen.hnotes;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SourceType;

@Entity
public class Note {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String title;

  private String content;

  private NoteStatus status;

  @CreationTimestamp(source = SourceType.DB)
  private Instant creationTimestamp;

  @UpdateTimestamp(source = SourceType.DB)
  private Instant lastModificationTimestamp;

  public Note() { }

  public Note(String title, String content) {
    this.title = title;
    this.content = content;
  }

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

  public Instant getCreationTimestamp() {
    return this.creationTimestamp;
  }

  public Instant getLastModificationTimestamp() {
    return this.lastModificationTimestamp;
  }

  public NoteStatus getStatus() {
  	return status;
  }

  public void setStatus(NoteStatus status) {
  	this.status = status;
  }
}
