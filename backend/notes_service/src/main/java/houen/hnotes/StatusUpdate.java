package houen.hnotes;

import java.io.Serializable;

public class StatusUpdate implements Serializable {

  public static StatusUpdate NoteAccepted(Integer id) {
    // TODO: implement
    return new StatusUpdate();
  }

  public static StatusUpdate NoteRejected(Integer id) {
    // TODO: implement
    return new StatusUpdate();
  }

  public static StatusUpdate NoteCreated(Integer id) {
    // TODO: implement
    return new StatusUpdate();
  }

  public static StatusUpdate NoteEdited(Integer id) {
    // TODO: implement
    return new StatusUpdate();
  }

  public static StatusUpdate NoteDeleted(Integer id) {
    // TODO: implement
    return new StatusUpdate();
  }

  public StatusUpdate() {}
}
