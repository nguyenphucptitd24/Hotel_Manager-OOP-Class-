import React, { useState, useEffect } from "react";
import api from "../services/api";
import "./ReceptionPage.css";

const ReceptionPage = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: "", type: "" });

    // Check-in form state
    const [checkInModal, setCheckInModal] = useState(false);
    const [formBookingId, setFormBookingId] = useState("");
    const [formRoomId, setFormRoomId] = useState("");
    const [formNote, setFormNote] = useState("");

    // Quick action states
    const [quickCheckOutId, setQuickCheckOutId] = useState("");
    const [quickCleanRoomId, setQuickCleanRoomId] = useState("");

    const showMessage = (text, type = "success") => {
        setMessage({ text, type });
        setTimeout(() => setMessage({ text: "", type: "" }), 5000);
    };

    const fetchBookings = async () => {
        setLoading(true);
        try {
            const res = await api.get("/api/v1/reception/bookings");
            setBookings(res.data || []);
        } catch (err) {
            console.error("Lỗi tải danh sách lễ tân:", err);
            if (bookings.length === 0) {
                setBookings([
                    { bookingId: 1, bookingCode: "INN00001", customerName: "Nguyễn Văn A", customerPhone: "0901234567", roomId: 1, roomNumber: "101", checkInExpected: "2026-10-05", checkOutExpected: "2026-10-08", status: "CONFIRMED" },
                    { bookingId: 2, bookingCode: "INN00002", customerName: "Trần Thị B", customerPhone: "0912345678", roomId: 2, roomNumber: "102", checkInExpected: "2026-10-04", checkOutExpected: "2026-10-07", status: "CHECKED_IN" },
                    { bookingId: 3, bookingCode: "INN00003", customerName: "Lê Văn C", customerPhone: "0987654321", roomId: 3, roomNumber: "103", checkInExpected: "2026-10-02", checkOutExpected: "2026-10-05", status: "CLEANING" },
                ]);
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const handleCheckInSubmit = async (e) => {
        if (e) e.preventDefault();
        if (!formBookingId) {
            showMessage("Vui lòng nhập Mã đơn đặt phòng (Booking ID)!", "error");
            return;
        }

        try {
            const payload = {
                bookingId: parseInt(formBookingId),
                roomId: formRoomId ? parseInt(formRoomId) : null,
                note: formNote || ""
            };
            const res = await api.post("/api/v1/reception/check-in", payload);
            showMessage(res.data?.message || "Check-in thành công!", "success");
            setCheckInModal(false);
            setFormBookingId("");
            setFormRoomId("");
            setFormNote("");
            fetchBookings();
        } catch (err) {
            showMessage(err.response?.data?.message || "Check-in thất bại!", "error");
        }
    };

    const handleFastCheckIn = async (bookingId, roomId) => {
        try {
            const payload = {
                bookingId: bookingId,
                roomId: roomId || null,
                note: "Fast Check-in tại quầy lễ tân"
            };
            const res = await api.post("/api/v1/reception/check-in", payload);
            showMessage(res.data?.message || "Check-in thành công!", "success");
            fetchBookings();
        } catch (err) {
            showMessage(err.response?.data?.message || "Check-in thất bại!", "error");
        }
    };

    const handleCheckOut = async (id) => {
        if (!id) return;
        try {
            const res = await api.post(`/api/v1/reception/check-out/${id}`);
            showMessage(res.data?.message || "Check-out thành công!", "success");
            setQuickCheckOutId("");
            fetchBookings();
        } catch (err) {
            showMessage(err.response?.data?.message || "Check-out thất bại!", "error");
        }
    };

    const handleCleanComplete = async (roomId) => {
        if (!roomId) return;
        try {
            const res = await api.post(`/api/v1/reception/clean-complete/${roomId}`);
            showMessage(res.data?.message || "Phòng đã sẵn sàng!", "success");
            setQuickCleanRoomId("");
            fetchBookings();
        } catch (err) {
            showMessage(err.response?.data?.message || "Không thể cập nhật dọn dẹp!", "error");
        }
    };

    const handleDeleteBooking = async (bookingId) => {
        if (!bookingId) return;
        try {
            const res = await api.delete(`/api/v1/reception/bookings/${bookingId}`);
            showMessage(res.data?.message || "Đã xóa đơn đặt phòng!", "success");
            fetchBookings();
        } catch (err) {
            showMessage(err.response?.data?.message || "Không thể xóa đơn đặt phòng!", "error");
        }
    };

    const openCheckInModalForBooking = (b) => {
        setFormBookingId(b.bookingId);
        setFormRoomId(b.roomId || "");
        setFormNote("");
        setCheckInModal(true);
    };

    return (
        <div className="reception-page">
            <div className="reception-page-header">
                <div>
                    <h1>Quản lý lễ tân</h1>
                    <p className="subtitle">Tiếp nhận check-in, check-out và giám sát quy trình dọn buồng phòng</p>
                </div>
                <div className="header-buttons">
                    <button className="btn-primary-action" onClick={() => {
                        setFormBookingId("");
                        setFormRoomId("");
                        setFormNote("");
                        setCheckInModal(true);
                    }}>
                        Check-in mới
                    </button>
                    <button className="btn-secondary-action" onClick={fetchBookings}>
                        {loading ? "Đang tải..." : "Làm mới"}
                    </button>
                </div>
            </div>

            {message.text && (
                <div className={`alert-banner ${message.type}`}>
                    {message.text}
                </div>
            )}

            {/* Quick Action Panels */}
            <div className="quick-actions-bar">
                <div className="quick-panel">
                    <h4>Check-out nhanh</h4>
                    <div className="quick-input-group">
                        <input
                            type="number"
                            placeholder="Nhập Booking ID hoặc Room ID..."
                            value={quickCheckOutId}
                            onChange={(e) => setQuickCheckOutId(e.target.value)}
                        />
                        <button
                            className="btn-action-checkout"
                            onClick={() => handleCheckOut(quickCheckOutId)}
                            disabled={!quickCheckOutId}
                        >
                            Check-out
                        </button>
                    </div>
                </div>

                <div className="quick-panel">
                    <h4>Hoàn tất dọn dẹp</h4>
                    <div className="quick-input-group">
                        <input
                            type="number"
                            placeholder="Nhập Room ID..."
                            value={quickCleanRoomId}
                            onChange={(e) => setQuickCleanRoomId(e.target.value)}
                        />
                        <button
                            className="btn-action-clean"
                            onClick={() => handleCleanComplete(quickCleanRoomId)}
                            disabled={!quickCleanRoomId}
                        >
                            Sẵn sàng đón khách
                        </button>
                    </div>
                </div>
            </div>

            {/* Reception Table */}
            <div className="reception-table-card">
                <div className="card-header-bar">
                    <h3>Danh sách đơn đặt phòng đang hoạt động</h3>
                    <span className="booking-counter">{bookings.length} đơn</span>
                </div>
                <div className="table-responsive">
                    <table className="reception-table">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Khách hàng</th>
                                <th>Số điện thoại</th>
                                <th>Phòng</th>
                                <th>Dự kiến nhận / trả</th>
                                <th>Trạng thái</th>
                                <th>Thao tác nhanh</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookings.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="empty-row">
                                        Không có đơn đặt phòng nào đang chờ xử lý.
                                    </td>
                                </tr>
                            ) : (
                                bookings.map((b, idx) => (
                                    <tr key={`${b.bookingId}-${b.roomId || idx}`}>
                                        <td className="code-cell">#{b.bookingCode || b.bookingId}</td>
                                        <td>{b.customerName || "Khách vãng lai"}</td>
                                        <td className="text-muted">{b.customerPhone || "-"}</td>
                                        <td>
                                            <span className="room-pill">
                                                {b.roomNumber ? `P.${b.roomNumber}` : (b.roomId ? `ID ${b.roomId}` : "Chưa gán")}
                                            </span>
                                        </td>
                                        <td className="date-cell">
                                            {b.checkInExpected} - {b.checkOutExpected}
                                        </td>
                                        <td>
                                            <span className={`status-tag status-${(b.status || "").toLowerCase()}`}>
                                                {b.status === "CONFIRMED" ? "CONFIRMED (Đã xác nhận)" :
                                                    b.status === "CHECKED_IN" ? "CHECKED_IN (Đang ở)" :
                                                        b.status === "CLEANING" ? "CLEANING (Dọn dẹp)" :
                                                            b.status === "COMPLETED" ? "COMPLETED (Hoàn tất)" :
                                                                b.status === "CANCELED" || b.status === "CANCELLED" ? "CANCELED (Đã hủy)" : b.status}
                                            </span>
                                        </td>
                                        <td className="actions-cell">
                                            {b.status === "CONFIRMED" && (
                                                <>
                                                    <button
                                                        className="btn-tbl btn-tbl-checkin"
                                                        onClick={() => handleFastCheckIn(b.bookingId, b.roomId)}
                                                    >
                                                        Check-in
                                                    </button>
                                                    <button
                                                        className="btn-tbl btn-tbl-outline"
                                                        onClick={() => openCheckInModalForBooking(b)}
                                                    >
                                                        Chi tiết
                                                    </button>
                                                </>
                                            )}
                                            {b.status === "CHECKED_IN" && (
                                                <button
                                                    className="btn-tbl btn-tbl-checkout"
                                                    onClick={() => handleCheckOut(b.bookingId)}
                                                >
                                                    Check-out
                                                </button>
                                            )}
                                            {b.status === "CLEANING" && (
                                                <button
                                                    className="btn-tbl btn-tbl-clean"
                                                    onClick={() => handleCleanComplete(b.roomId)}
                                                >
                                                    Đã dọn xong
                                                </button>
                                            )}
                                            {(b.status === "COMPLETED" || b.status === "CANCELED" || b.status === "CANCELLED") && (
                                                <button
                                                    className="btn-tbl btn-tbl-delete"
                                                    onClick={() => handleDeleteBooking(b.bookingId)}
                                                >
                                                    Xóa
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Check-In Modal / Panel */}
            {checkInModal && (
                <div className="modal-backdrop">
                    <div className="modal-card">
                        <div className="modal-header">
                            <h3>Biểu mẫu nhận phòng (Check-in)</h3>
                            <button className="btn-close-modal" onClick={() => setCheckInModal(false)}>Đóng</button>
                        </div>
                        <form onSubmit={handleCheckInSubmit}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label>Mã đơn đặt phòng (Booking ID) *</label>
                                    <input
                                        type="number"
                                        required
                                        placeholder="Ví dụ: 1"
                                        value={formBookingId}
                                        onChange={(e) => setFormBookingId(e.target.value)}
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Mã phòng cụ thể (Room ID - Tùy chọn)</label>
                                    <input
                                        type="number"
                                        placeholder="Để trống nếu check-in toàn bộ phòng trong đơn"
                                        value={formRoomId}
                                        onChange={(e) => setFormRoomId(e.target.value)}
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Ghi chú lễ tân (Note)</label>
                                    <textarea
                                        rows="3"
                                        placeholder="Ghi chú nhận phòng, yêu cầu của khách..."
                                        value={formNote}
                                        onChange={(e) => setFormNote(e.target.value)}
                                    />
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn-modal-cancel" onClick={() => setCheckInModal(false)}>
                                    Hủy
                                </button>
                                <button type="submit" className="btn-modal-submit">
                                    Xác nhận check-in
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ReceptionPage;
