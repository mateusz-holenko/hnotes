package houen.status_service;

public class StatusUpdate {
  private String context;
  private Integer identifier;
  private String action;

  public StatusUpdate() {}

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

