package houen.hnotes;

import java.io.Serializable;

public class StatusUpdate implements Serializable {

  public static StatusUpdate NoteAccepted(Integer id) {
    return new StatusUpdate("note", id, "accepted");
  }

  public static StatusUpdate NoteRejected(Integer id) {
    return new StatusUpdate("note", id, "rejected");
  }

  public static StatusUpdate NoteCreated(Integer id) {
    return new StatusUpdate("note", id, "created");
  }

  public static StatusUpdate NoteUpdated(Integer id) {
    return new StatusUpdate("note", id, "updated");
  }

  public static StatusUpdate NoteDeleted(Integer id) {
    return new StatusUpdate("note", id, "deleted");
  }

  private final String context;
  private final Integer identifier;
  private final String action;

  public String getContext() {
    return context;
  }

  public Integer getIdentifier() {
    return identifier;
  }

  public String getAction() {
    return action;
  }

  private StatusUpdate(String context, Integer identifier, String action) {
    this.context = context;
    this.identifier = identifier;
    this.action = action;
  }
}
