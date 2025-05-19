package houen.hnotes;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
public class Note {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;

  private String title;

  private String content;

  private NoteStatus status;

  @CreatedDate
  private Instant creationTimestamp;

  @LastModifiedDate
  private Instant lastModificationTimestamp;

  public Note() {
    this.status = NoteStatus.UNVERIFIED;
  }

  public Note(String title, String content) {
    this();
    this.title = title;
    this.content = content;
  }

  public String getId() {
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

  public void setId(String id) {
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
