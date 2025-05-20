package houen.status_service;

public class StatusUpdate {
  private String context;
  private String identifier;
  private String action;

  public StatusUpdate() {}

  public StatusUpdate(String context, String identifier, String action) {
    this.context = context;
    this.identifier = identifier;
    this.action = action;
  }

  public String getContext() {
    return context;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getAction() {
    return action;
  }
}

