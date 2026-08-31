package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByIdentityCard(String identityCard);

    @Query("""
            SELECT c
            FROM Customer c
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.identityCard) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.country) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
}
