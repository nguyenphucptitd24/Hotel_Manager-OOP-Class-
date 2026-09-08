package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.BookingHistoryResponse;
import Hotel_Manager_OOP_Class_.demo.dto.CustomerResponse;
import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import Hotel_Manager_OOP_Class_.demo.entity.Customer;
import Hotel_Manager_OOP_Class_.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class_.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;


    public Page<CustomerResponse> searchCustomers(
            String keyword,
            Pageable pageable) {

        Page<Customer> customers =
                customerRepository.searchCustomers(keyword, pageable);

        return customers.map(this::toCustomerResponse);
    }


    public Page<BookingHistoryResponse> getBookingHistory(
            Integer customerId,
            Pageable pageable) {

        Page<Booking> bookings =
                bookingRepository.findByCustomerId(
                        customerId,
                        pageable
                );

        return bookings.map(this::toBookingHistoryResponse);
    }


    private CustomerResponse toCustomerResponse(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .identityCard(customer.getIdentityCard())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .country(customer.getCountry())
                .build();
    }


    private BookingHistoryResponse toBookingHistoryResponse(
            Booking booking) {

        return BookingHistoryResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingDate(booking.getBookingDate())
                .totalDeposit(booking.getTotalDeposit())
                .status(booking.getStatus())
                .build();
    }
}