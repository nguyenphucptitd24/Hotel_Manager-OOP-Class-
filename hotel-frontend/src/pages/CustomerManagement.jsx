import { useEffect, useState } from 'react';
import api from '../services/api';
import './CustomerManagement.css';

function CustomerManagement() {
  // =========================
  // CUSTOMER
  // =========================
  const [customers, setCustomers] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [customerPage, setCustomerPage] = useState(0);
  const [customerTotalPages, setCustomerTotalPages] = useState(0);
  const [customerLoading, setCustomerLoading] = useState(false);
  const [customerError, setCustomerError] = useState('');

  const CUSTOMER_PAGE_SIZE = 10;

  // =========================
  // BOOKING HISTORY
  // =========================
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [bookingHistory, setBookingHistory] = useState([]);
  const [bookingPage, setBookingPage] = useState(0);
  const [bookingTotalPages, setBookingTotalPages] = useState(0);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [bookingError, setBookingError] = useState('');

  const BOOKING_PAGE_SIZE = 5;

  // =========================
  // LOAD CUSTOMERS
  // =========================
  const loadCustomers = async () => {
    setCustomerLoading(true);
    setCustomerError('');

    try {
      const response = await api.get('/api/v1/customers', {
        params: {
          keyword: keyword.trim() || undefined,
          page: customerPage,
          size: CUSTOMER_PAGE_SIZE,
        },
      });

      setCustomers(response.data.content || []);
      setCustomerTotalPages(response.data.totalPages || 0);
    } catch (error) {
      console.error('Lỗi khi lấy danh sách khách hàng:', error);

      setCustomers([]);
      setCustomerTotalPages(0);
      setCustomerError(
        error.response?.data?.message || 'Không thể tải danh sách khách hàng.',
      );
    } finally {
      setCustomerLoading(false);
    }
  };

  // Load customers khi page hoặc keyword thay đổi
  useEffect(() => {
    loadCustomers();
  }, [customerPage, keyword]);

  // =========================
  // SEARCH
  // =========================
  const handleSearch = (event) => {
    event.preventDefault();

    // Khi tìm kiếm thì quay về trang đầu tiên
    if (customerPage === 0) {
      // Nếu đã ở trang 0, gọi loadCustomers() trực tiếp
      loadCustomers();
    } else {
      setCustomerPage(0);
    }
  };

  // =========================
  // CUSTOMER PAGINATION
  // =========================
  const goToPreviousCustomerPage = () => {
    if (customerPage > 0) {
      setCustomerPage((prev) => prev - 1);
    }
  };

  const goToNextCustomerPage = () => {
    if (customerPage < customerTotalPages - 1) {
      setCustomerPage((prev) => prev + 1);
    }
  };

  // =========================
  // OPEN BOOKING HISTORY
  // =========================
  const openBookingHistory = async (customer) => {
    setSelectedCustomer(customer);
    setBookingPage(0);
    setBookingHistory([]);
    setBookingTotalPages(0);
    setBookingError('');

    await loadBookingHistory(customer.id, 0);
  };

  // =========================
  // LOAD BOOKING HISTORY
  // =========================
  const loadBookingHistory = async (customerId, page) => {
    setBookingLoading(true);
    setBookingError('');

    try {
      const response = await api.get(
        `/api/v1/customers/${customerId}/bookings`,
        {
          params: {
            page: page,
            size: BOOKING_PAGE_SIZE,
          },
        },
      );

      setBookingHistory(response.data.content || []);
      setBookingTotalPages(response.data.totalPages || 0);
    } catch (error) {
      console.error('Lỗi khi lấy lịch sử booking:', error);

      setBookingHistory([]);
      setBookingTotalPages(0);

      setBookingError(
        error.response?.data?.message || 'Không thể tải lịch sử lưu trú.',
      );
    } finally {
      setBookingLoading(false);
    }
  };

  // =========================
  // BOOKING PAGINATION
  // =========================
  const goToPreviousBookingPage = () => {
    if (bookingPage > 0) {
      const newPage = bookingPage - 1;

      setBookingPage(newPage);

      if (selectedCustomer) {
        loadBookingHistory(selectedCustomer.id, newPage);
      }
    }
  };

  const goToNextBookingPage = () => {
    if (bookingPage < bookingTotalPages - 1) {
      const newPage = bookingPage + 1;

      setBookingPage(newPage);

      if (selectedCustomer) {
        loadBookingHistory(selectedCustomer.id, newPage);
      }
    }
  };

  // =========================
  // CLOSE BOOKING MODAL
  // =========================
  const closeBookingHistory = () => {
    setSelectedCustomer(null);
    setBookingHistory([]);
    setBookingPage(0);
    setBookingTotalPages(0);
    setBookingError('');
  };

  // =========================
  // FORMAT MONEY
  // =========================
  const formatMoney = (value) => {
    if (value === null || value === undefined) {
      return '-';
    }

    const number = Number(value);

    if (Number.isNaN(number)) {
      return value;
    }

    return number.toLocaleString('vi-VN') + ' đ';
  };

  // =========================
  // STATUS
  // =========================
  const getStatusText = (status) => {
    if (!status) {
      return '-';
    }

    switch (String(status).toUpperCase()) {
      case 'CONFIRMED':
        return 'Đã xác nhận';

      case 'CHECKED_IN':
        return 'Đang lưu trú';

      case 'COMPLETED':
        return 'Hoàn thành';

      case 'CANCELED':
      case 'CANCELLED':
        return 'Đã hủy';

      case 'PENDING':
        return 'Chờ xác nhận';

      default:
        return status;
    }
  };

  // =========================
  // RENDER
  // =========================
  return (
    <div className="customer-management">
      {/* =========================
                HEADER
            ========================= */}
      <div className="customer-page-header">
        <div>
          <h1>Quản lý khách hàng</h1>
          <p>Tìm kiếm và xem thông tin lịch sử lưu trú của khách hàng.</p>
        </div>
      </div>

      {/* =========================
                SEARCH
            ========================= */}
      <form className="customer-search" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Tìm theo tên, SĐT, CCCD/Hộ chiếu, email, quốc gia..."
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
        />

        <button type="submit">Tìm kiếm</button>
      </form>

      {/* =========================
                ERROR
            ========================= */}
      {customerError && <div className="customer-error">{customerError}</div>}

      {/* =========================
                CUSTOMER TABLE
            ========================= */}
      <div className="customer-table-container">
        {customerLoading ? (
          <div className="customer-loading">
            Đang tải danh sách khách hàng...
          </div>
        ) : customers.length === 0 ? (
          <div className="customer-empty">Không tìm thấy khách hàng.</div>
        ) : (
          <table className="customer-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Họ tên</th>
                <th>CCCD / Hộ chiếu</th>
                <th>Số điện thoại</th>
                <th>Email</th>
                <th>Quốc gia</th>
                <th>Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {customers.map((customer) => (
                <tr key={customer.id}>
                  <td>{customer.id}</td>

                  <td>
                    <strong>{customer.fullName || '-'}</strong>
                  </td>

                  <td>{customer.identityCard || '-'}</td>

                  <td>{customer.phone || '-'}</td>

                  <td>{customer.email || '-'}</td>

                  <td>{customer.country || '-'}</td>

                  <td>
                    <button
                      className="history-button"
                      onClick={() => openBookingHistory(customer)}
                    >
                      Lịch sử
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* =========================
                CUSTOMER PAGINATION
            ========================= */}
      {!customerLoading && customers.length > 0 && (
        <div className="pagination">
          <button
            onClick={goToPreviousCustomerPage}
            disabled={customerPage === 0}
          >
            ← Trước
          </button>

          <span>
            Trang <strong>{customerPage + 1}</strong> /{' '}
            <strong>{customerTotalPages}</strong>
          </span>

          <button
            onClick={goToNextCustomerPage}
            disabled={customerPage >= customerTotalPages - 1}
          >
            Sau →
          </button>
        </div>
      )}

      {/* =========================
                BOOKING HISTORY MODAL
            ========================= */}
      {selectedCustomer && (
        <div className="customer-modal-overlay" onClick={closeBookingHistory}>
          <div
            className="customer-modal"
            onClick={(event) => event.stopPropagation()}
          >
            {/* MODAL HEADER */}
            <div className="customer-modal-header">
              <div>
                <h2>Lịch sử lưu trú</h2>

                <p>
                  Khách hàng: <strong>{selectedCustomer.fullName}</strong>
                </p>

                <p>SĐT: {selectedCustomer.phone || '-'}</p>
              </div>

              <button
                className="modal-close-button"
                onClick={closeBookingHistory}
              >
                ×
              </button>
            </div>

            {/* MODAL ERROR */}
            {bookingError && (
              <div className="customer-error">{bookingError}</div>
            )}

            {/* MODAL CONTENT */}
            {bookingLoading ? (
              <div className="customer-loading">
                Đang tải lịch sử lưu trú...
              </div>
            ) : bookingHistory.length === 0 ? (
              <div className="customer-empty">
                Khách hàng chưa có lịch sử booking.
              </div>
            ) : (
              <div className="booking-history-list">
                {bookingHistory.map((booking) => (
                  <div className="booking-history-item" key={booking.id}>
                    {/* BOOKING INFO */}
                    <div className="booking-info">
                      <div className="booking-info-row">
                        <span>Mã booking</span>

                        <strong>{booking.bookingCode || '-'}</strong>
                      </div>

                      <div className="booking-info-row">
                        <span>Ngày đặt</span>

                        <strong>{booking.bookingDate || '-'}</strong>
                      </div>

                      <div className="booking-info-row">
                        <span>Tiền cọc</span>

                        <strong>{formatMoney(booking.totalDeposit)}</strong>
                      </div>

                      <div className="booking-info-row">
                        <span>Trạng thái</span>

                        <strong>{getStatusText(booking.status)}</strong>
                      </div>
                    </div>

                    {/* =========================
                                                STAY DETAILS
                                            ========================= */}
                    {booking.details && booking.details.length > 0 && (
                      <div className="stay-details">
                        <h4>Chi tiết lưu trú</h4>

                        {booking.details.map((detail) => (
                          <div className="stay-detail" key={detail.id}>
                            <div>
                              <span>Phòng</span>

                              <strong>
                                {detail.roomNumber || detail.roomId || '-'}
                              </strong>
                            </div>

                            <div>
                              <span>Check-in dự kiến</span>

                              <strong>{detail.checkInExpected}</strong>
                            </div>

                            <div>
                              <span>Check-out dự kiến</span>

                              <strong>{detail.checkOutExpected}</strong>
                            </div>

                            <div>
                              <span>Check-in thực tế</span>

                              <strong>{detail.checkInActual || '-'}</strong>
                            </div>

                            <div>
                              <span>Check-out thực tế</span>

                              <strong>{detail.checkOutActual || '-'}</strong>
                            </div>

                            <div>
                              <span>Giá / đêm</span>

                              <strong>
                                {formatMoney(detail.pricePerNight)}
                              </strong>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}

                    {/* Không có detail */}
                    {(!booking.details || booking.details.length === 0) && (
                      <div className="no-stay-details">
                        Chưa có thông tin chi tiết lưu trú.
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* =========================
                            BOOKING PAGINATION
                        ========================= */}
            {!bookingLoading && bookingHistory.length > 0 && (
              <div className="pagination modal-pagination">
                <button
                  onClick={goToPreviousBookingPage}
                  disabled={bookingPage === 0}
                >
                  ← Trước
                </button>

                <span>
                  Trang <strong>{bookingPage + 1}</strong> /{' '}
                  <strong>{bookingTotalPages}</strong>
                </span>

                <button
                  onClick={goToNextBookingPage}
                  disabled={bookingPage >= bookingTotalPages - 1}
                >
                  Sau →
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default CustomerManagement;
