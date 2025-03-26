package houen.hnotes;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class NotesRestControllerOptions {

    private Integer createDelay = 0;

    @ManagedAttribute
    public Integer getCreateDelay() {
      return this.createDelay;
    }

    @ManagedAttribute
    public void setCreateDelay(final Integer delay) {
      this.createDelay = delay;
    }

}
