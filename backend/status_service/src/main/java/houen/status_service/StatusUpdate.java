package houen.status_service;

public class StatusUpdate {
  private final String context;
  private final Integer identifier;
  private final String action;

  public StatusUpdate(String context, Integer identifier, String action) {
    this.context = context;
    this.identifier = identifier;
    this.action = action;
  }

  public String getContext() {
    return context;
  }

  public Integer getIdentifier() {
    return identifier;
  }

  public String getAction() {
    return action;
  }
}

