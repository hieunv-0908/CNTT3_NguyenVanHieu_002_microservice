package org.example.bookingservice.models.services.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.bookingservice.clients.MovieClient;
import org.example.bookingservice.exceptions.MovieNotFoundException;
import org.example.bookingservice.exceptions.MovieServiceException;
import org.example.bookingservice.models.dto.responses.MovieResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieGatewayService {

    private final MovieClient movieClient;

    // phân biệt trường hợp phim không tồn tại với trường hợp microservice lỗi, không khả dụng
    // thì ta cần throw exception khác nhau để client có thể xử lý khác nhau

    // trường hợp service lỗi
    private MovieResponse fallbackGetMovieById(Long movieId, Exception ex) {
        throw new MovieServiceException("Failed to fetch movie details", ex);
    }

    @CircuitBreaker(name = "movieService",fallbackMethod = "fallbackGetMovieById")
    public MovieResponse getMovieById(Long movieId) {
        MovieResponse movieResponse = movieClient.getMovieById(movieId);
        if (movieResponse == null) {
            // trường hợp phim không tồn tại
            throw new MovieNotFoundException(movieId);
        }

        return movieResponse;
//        throw new UnsupportedOperationException();
    }
}
