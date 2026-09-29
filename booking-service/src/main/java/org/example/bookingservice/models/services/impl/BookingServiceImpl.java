package org.example.bookingservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.bookingservice.models.constants.BookingStatus;
import org.example.bookingservice.models.dto.requests.CreateBookingDetailRequest;
import org.example.bookingservice.models.dto.requests.CreateBookingRequest;
import org.example.bookingservice.models.dto.responses.BookingDetailResponse;
import org.example.bookingservice.models.dto.responses.BookingResponse;
import org.example.bookingservice.models.dto.responses.MovieResponse;
import org.example.bookingservice.models.entities.Booking;
import org.example.bookingservice.models.entities.BookingDetail;
import org.example.bookingservice.models.repositories.BookingDetailRepository;
import org.example.bookingservice.models.repositories.BookingRepository;
import org.example.bookingservice.models.services.BookingService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final BookingDetailRepository bookingDetailRepository;
        private final MovieGatewayService movieGatewayService;

        @Override
        @Transactional
        public BookingResponse createBooking(CreateBookingRequest request) {
                double totalAmount = 0.0;
                for (CreateBookingDetailRequest detail : request.items()) {
                    MovieResponse movie = movieGatewayService.getMovieById(detail.movieId());
                    double lineTotal = movie.ticketPrice() * detail.quantity();
                    totalAmount += lineTotal;
                }
                Booking booking = Booking.builder()
                        .customerName(request.customerName())
                        .customerEmail(request.customerEmail())
                        .total(totalAmount)
                        .status(BookingStatus.PENDING)
                        .build();
                Booking savedBooking = bookingRepository.save(booking);
                List<BookingDetail> bookingDetails = new ArrayList<>();
                for (CreateBookingDetailRequest detail : request.items()) {
                    MovieResponse movie = movieGatewayService.getMovieById(detail.movieId());
                    double lineTotal = movie.ticketPrice() * detail.quantity();
                    bookingDetails.add(BookingDetail.builder()
                            .movieId(detail.movieId())
                            .quantity(detail.quantity())
                            .unitPrice(movie.ticketPrice())
//                            .subtotal(lineTotal) theo đề bài booking detail cần có sub total nhưng code base BookingDetail không có field sub total
                            .booking(savedBooking)
                            .build());
                }
                bookingDetailRepository.saveAll(bookingDetails);
                List<BookingDetailResponse> bookingDetailResponses = new ArrayList<>();
                for (BookingDetail bookingDetail : bookingDetails) {
                    MovieResponse movie = movieGatewayService.getMovieById(bookingDetail.getMovieId());
                    BookingDetailResponse bookingDetailResponse = new BookingDetailResponse(bookingDetail.getId(),
                            bookingDetail.getMovieId(),
                            movie.title(),
                            bookingDetail.getQuantity(),
                            movie.ticketPrice(),
                            movie.ticketPrice() * bookingDetail.getQuantity()

                    );
                    bookingDetailResponses.add(bookingDetailResponse);
                }
//                BookingResponse là record
            return new BookingResponse(
                    savedBooking.getId(),
                    savedBooking.getCustomerName(),
                    savedBooking.getCustomerEmail(),
                    savedBooking.getTotal(),
                    savedBooking.getStatus(),
                    bookingDetailResponses
            );


//                throw new UnsupportedOperationException();
        }
}
