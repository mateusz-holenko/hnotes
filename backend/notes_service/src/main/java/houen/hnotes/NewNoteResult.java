package houen.hnotes;

import java.time.Instant;

public record NewNoteResult(String id, Instant creationTimestamp) {};

