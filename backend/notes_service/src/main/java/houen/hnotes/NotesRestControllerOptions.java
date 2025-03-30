package houen.hnotes;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class NotesRestControllerOptions {

    private Integer createDelayInMilliseconds = 3000;
    private Integer updateDelayInMilliseconds = 3000;
    private Integer removeDelayInMilliseconds = 3000;
    private Integer fetchDelayInMilliseconds = 3000;

    @ManagedAttribute
    public Integer getCreateDelayInMilliseconds() {
      return this.createDelayInMilliseconds;
    }

    @ManagedAttribute
    public void setCreateDelayInMilliseconds(final Integer delayInMilliseconds) {
      this.createDelayInMilliseconds = delayInMilliseconds;
    }

    public void waitOnCreate() {
      wait(createDelayInMilliseconds);
    }

    public void waitOnUpdate() {
      wait(updateDelayInMilliseconds);
    }

    public void waitOnRemove() {
      wait(removeDelayInMilliseconds);
    }

    public void waitOnFetch() {
      wait(fetchDelayInMilliseconds);
    }

    private void wait(Integer delayInMilliseconds) {
      if(delayInMilliseconds <= 0) {
        return;
      }

      try {
        Thread.sleep(delayInMilliseconds);
      } catch (Exception e) { }
    }
}
