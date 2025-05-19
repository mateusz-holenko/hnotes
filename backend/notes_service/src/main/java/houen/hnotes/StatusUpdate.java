package houen.hnotes;

import java.io.Serializable;

public class StatusUpdate implements Serializable {

  public static StatusUpdate NoteAccepted(String id) {
    return new StatusUpdate("note", id, "accepted");
  }

  public static StatusUpdate NoteRejected(String id) {
    return new StatusUpdate("note", id, "rejected");
  }

  public static StatusUpdate NoteCreated(String id) {
    return new StatusUpdate("note", id, "created");
  }

  public static StatusUpdate NoteUpdated(String id) {
    return new StatusUpdate("note", id, "updated");
  }

  public static StatusUpdate NoteDeleted(String id) {
    return new StatusUpdate("note", id, "deleted");
  }

  private final String context;
  private final String identifier;
  private final String action;

  public String getContext() {
    return context;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getAction() {
    return action;
  }

  private StatusUpdate(String context, String identifier, String action) {
    this.context = context;
    this.identifier = identifier;
    this.action = action;
  }
}
