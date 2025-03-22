package houen.hnotes.users;

// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;

// @Entity
public class User {
  // @Id
  // @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String handle;

  public User() {
  }

  public User(Integer id, String handle) {
    this.id = id;
    this.handle = handle;
  }

  public Integer getId() {
    return this.id;
  }

  public String getHandle() {
    return this.handle;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean checkCredentials(String password) {
    return this.handle.equals(password);
  }
}
