package Cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/cinema")
public class MovieController {

    private MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return service.getMovies(title);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        service.delete();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@RequestBody @Valid CreateMovieCommand command) {
        return service.createMovie(command);
    }

    @GetMapping("{id}")
    public MovieDTO findById(@PathVariable("id") long id) {
        return service.findById(id);
    }

    @PutMapping("{id}")
    public MovieDTO updateDate(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return service.updateDate(id, command.getDate());
    }

    @PostMapping("{id}/reserve")
    public MovieDTO reserveSpaces(@PathVariable("id") long id, @RequestBody CreateReservationCommand command) {
        return service.reserveSpaces(id, command.getSpaces());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> notFound(IllegalArgumentException iae) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(iae.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> badReservation(IllegalStateException ise) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Bad Reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(ise.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Problem> handleValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("cinema/validation-error"))
                .withTitle("Validation error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violation", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
