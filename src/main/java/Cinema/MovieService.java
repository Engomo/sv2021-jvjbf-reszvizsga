package Cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private List<Movie> movies = Collections.synchronizedList(new ArrayList<>());
    private AtomicLong idGenerator = new AtomicLong();
    private ModelMapper modelMapper;

    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public List<MovieDTO> getMovies(Optional<String> title) {
        Type targetListType = new TypeToken<List<MovieDTO>>() {
        }.getType();

        List<Movie> filtered = movies.stream()
                .filter(m -> title.isEmpty() || m.getTitle().equalsIgnoreCase(title.get()))
                .collect(Collectors.toList());

        return modelMapper.map(filtered, targetListType);
    }


    public void delete() {
        movies.clear();
        idGenerator = new AtomicLong();
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGenerator.incrementAndGet(), command.getTitle(), command.getDate(), command.getSpaces(), command.getSpaces());

        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }


    public MovieDTO findById(long id) {
        Movie result = getMovie(id);

        return modelMapper.map(result, MovieDTO.class);
    }


    public MovieDTO updateDate(long id, LocalDateTime newDate) {
        Movie result = getMovie(id);

        if (!result.getDate().equals(newDate)) {
            result.setDate(newDate);
        }
        return modelMapper.map(result, MovieDTO.class);
    }

    private Movie getMovie(long id) {
        return movies.stream()
                .filter(m -> m.getId().longValue() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    public MovieDTO reserveSpaces(long id, int spaces) {
        Movie result = getMovie(id);

        result.freeSpaces(spaces);

        return modelMapper.map(result, MovieDTO.class);
    }


}
