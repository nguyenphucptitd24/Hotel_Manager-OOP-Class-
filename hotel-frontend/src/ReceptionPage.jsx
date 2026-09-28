import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ReceptionPage = () => {
  const [bookings, setBookings] = useState([
    { id: 1, bookingCode: 'BK001', customerName: 'Nguyễn Văn A', roomNumber: '101', status: 'CONFIRMED' },
    { id: 2, bookingCode: 'BK002', customerName: 'Trần Thị B', roomNumber: '102', status: 'CHECKED_IN' },
    { id: 3, bookingCode: 'BK003', customerName: 'Lê Văn C', roomNumber: '201', status: 'CLEANING' },
  ]);

  const fetchBookings = () => {
    axios.get('http://localhost:8080/api/v1/reception/bookings')
      .then(res => setBookings(res.data))
      .catch(err => console.log("Chưa kết nối API Backend, sử dụng danh sách tĩnh.", err));
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleCheckIn = (id) => {
    axios.post(`http://localhost:8080/api/v1/reception/check-in/${id}`)
      .then(() => fetchBookings())
      .catch(() => {
        setBookings(bookings.map(b => b.id === id ? { ...b, status: 'CHECKED_IN' } : b));
      });
  };

  const handleCheckOut = (id) => {
    axios.post(`http://localhost:8080/api/v1/reception/check-out/${id}`)
      .then(() => fetchBookings())
      .catch(() => {
        setBookings(bookings.map(b => b.id === id ? { ...b, status: 'CLEANING' } : b));
      });
  };

  const handleCleanComplete = (id) => {
    axios.post(`http://localhost:8080/api/v1/reception/clean-complete/${id}`)
      .then(() => fetchBookings())
      .catch(() => {
        setBookings(bookings.filter(b => b.id !== id));
        alert("Đã dọn dẹp xong! Phòng sẵn sàng.");
      });
  };

  return (
    <div style={{ padding: '24px', fontFamily: 'Segoe UI, sans-serif' }}>
      <h2 style={{ color: '#1a237e', marginBottom: '20px' }}>📋 Nghiệp Vụ Lễ Tân Tại Quầy (Check-in / Check-out)</h2>
      
      <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#fff', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <thead>
          <tr style={{ backgroundColor: '#1a237e', color: '#fff', textAlign: 'left' }}>
            <th style={{ padding: '12px 16px' }}>Mã Đơn</th>
            <th style={{ padding: '12px 16px' }}>Khách Hàng</th>
            <th style={{ padding: '12px 16px' }}>Phòng</th>
            <th style={{ padding: '12px 16px' }}>Trạng Thái</th>
            <th style={{ padding: '12px 16px' }}>Thao Tác Fast-Action</th>
          </tr>
        </thead>
        <tbody>
          {bookings.map((b) => (
            <tr key={b.id} style={{ borderBottom: '1px solid #eee' }}>
              <td style={{ padding: '12px 16px', fontWeight: 'bold' }}>{b.bookingCode}</td>
              <td style={{ padding: '12px 16px' }}>{b.customerName}</td>
              <td style={{ padding: '12px 16px' }}>Phòng {b.roomNumber}</td>
              <td style={{ padding: '12px 16px' }}>
                <span style={{
                  padding: '4px 12px',
                  borderRadius: '12px',
                  fontSize: '12px',
                  fontWeight: 'bold',
                  backgroundColor: b.status === 'CONFIRMED' ? '#fff3e0' : b.status === 'CHECKED_IN' ? '#e8f5e9' : '#e0f7fa',
                  color: b.status === 'CONFIRMED' ? '#e65100' : b.status === 'CHECKED_IN' ? '#2e7d32' : '#006064'
                }}>
                  {b.status}
                </span>
              </td>
              <td style={{ padding: '12px 16px' }}>
                {b.status === 'CONFIRMED' && (
                  <button onClick={() => handleCheckIn(b.id)} style={{ padding: '6px 12px', backgroundColor: '#2e7d32', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Check-in
                  </button>
                )}
                {b.status === 'CHECKED_IN' && (
                  <button onClick={() => handleCheckOut(b.id)} style={{ padding: '6px 12px', backgroundColor: '#1565c0', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Check-out
                  </button>
                )}
                {b.status === 'CLEANING' && (
                  <button onClick={() => handleCleanComplete(b.id)} style={{ padding: '6px 12px', backgroundColor: '#00838f', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Đã dọn dẹp xong
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ReceptionPage;