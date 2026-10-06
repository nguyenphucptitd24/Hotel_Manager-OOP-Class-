package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.BookingHistoryResponse;
import Hotel_Manager_OOP_Class_.demo.dto.CustomerResponse;
import Hotel_Manager_OOP_Class_.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @GetMapping
    public Page<CustomerResponse> searchCustomers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        return customerService.searchCustomers(
                keyword,
                pageable
        );
    }


    @GetMapping("/{customerId}/bookings")
    public Page<BookingHistoryResponse> getBookingHistory(
            @PathVariable Integer customerId,
            Pageable pageable) {

        return customerService.getBookingHistory(
                customerId,
                pageable
        );
    }
}