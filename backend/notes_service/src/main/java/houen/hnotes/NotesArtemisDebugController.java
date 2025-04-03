package houen.hnotes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@CrossOrigin(origins = "*")
public class NotesArtemisDebugController {
    @Autowired
    private ArtemisService artemisService;

    @GetMapping("/artemis/send/{text}")
    public void artemisSend(@PathVariable(value = "text") String text) {
      artemisService.send(text);
    }
}
