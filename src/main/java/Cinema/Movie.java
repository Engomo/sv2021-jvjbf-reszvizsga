package Cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private Long id;
    private String title;
    private LocalDateTime date;
    private int spaces;
    private int freeSpaces;

    public void freeSpaces(int spaces)
    {
        if (spaces > freeSpaces) {
            throw new IllegalStateException("no more space");
        }
        freeSpaces -= spaces;
    }
}
