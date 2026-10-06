import { useCallback, useEffect, useState } from "react";
import api from "../services/api";
import "./RoomMatrix.css";
import RoomForm from "./RoomForm";

function RoomMatrix({ role }) {
    const [showForm, setShowForm] = useState(false);
    const [editingRoom, setEditingRoom] = useState(null);
    const [rooms, setRooms] = useState([]);
    const [roomTypes, setRoomTypes] = useState([]);
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [floor, setFloor] = useState("");
    const [status, setStatus] = useState("");
    const [roomTypeId, setRoomTypeId] = useState("");
    const [loading, setLoading] = useState(false);
    const isAdmin = role === "ADMIN";

    const getRooms = useCallback(async () => {
        try {
            setLoading(true);
            const params = {};

            if (floor) params.floor = floor;
            if (status) params.status = status;
            if (roomTypeId) params.roomTypeId = roomTypeId;

            const response = await api.get("/api/v1/rooms", {
                params: params
            });

            setRooms(response.data);
        } catch (error) {
            console.log("Lỗi lấy danh sách phòng:", error);
        } finally {
            setLoading(false);
        }
    }, [floor, status, roomTypeId]);

    const getRoomTypes = useCallback(async () => {
        try {
            const response = await api.get("/api/v1/room-types");
            setRoomTypes(response.data);
        } catch (error) {
            console.log("Lỗi lấy loại phòng:", error);
        }
    }, []);

    useEffect(() => {
        getRoomTypes();
    }, [getRoomTypes]);

    useEffect(() => {
        getRooms();
    }, [getRooms]);

    const getStatusClass = (status) => {
        switch (status) {
            case "AVAILABLE":
                return "available";
            case "OCCUPIED":
                return "occupied";
            case "CLEANING":
                return "cleaning";
            default:
                return "";
        }
    };

    const changeStatus = async (newStatus) => {
        if (!selectedRoom) return;
        try {
            await api.patch(
                `/api/v1/rooms/${selectedRoom.id}/status`,
                null,
                {
                    params: {
                        status: newStatus,
                    },
                }
            );

            // Cập nhật state selectedRoom cục bộ để hiển thị ngay
            setSelectedRoom({ ...selectedRoom, status: newStatus });

            // Lấy lại dữ liệu mới từ Backend
            getRooms();

        } catch (error) {
            console.log("Lỗi đổi trạng thái:", error);
            alert("Không thể đổi trạng thái!");
        }
    };

    const handleDeleteRoom = async () => {
        if (!selectedRoom) return;
        if (!window.confirm(`Bạn có chắc muốn xóa phòng ${selectedRoom.roomNumber}?`)) {
            return;
        }

        try {
            await api.delete(`/api/v1/rooms/${selectedRoom.id}`);
            alert("Xóa phòng thành công!");
            setSelectedRoom(null);
            getRooms();
        } catch (error) {
            console.log("Lỗi xóa phòng:", error);
            alert(error.response?.data || "Không thể xóa phòng!");
        }
    };

    const resetFilters = () => {
        setFloor("");
        setStatus("");
        setRoomTypeId("");
    };

    const handleRoomClick = (room) => {
        // Toggle: nếu click lại vào phòng đang chọn thì đóng lại
        if (selectedRoom?.id === room.id) {
            setSelectedRoom(null);
        } else {
            setSelectedRoom(room);
        }
    };

    const roomsByFloor = rooms.reduce((result, room) => {
        if (!result[room.floor]) {
            result[room.floor] = [];
        }
        result[room.floor].push(room);
        return result;
    }, {});

    return (
        <div className="room-page">
            <div className="room-page-header">
                <div>
                    <h1>Quản lý phòng</h1>
                    <p className="subtitle">Sơ đồ ma trận phòng và trạng thái buồng phòng thời gian thực</p>
                </div>

                {isAdmin && (
                    <button
                        className={`btn-toggle-form ${showForm ? "active" : ""}`}
                        onClick={() => {
                            if (showForm && editingRoom) {
                                setEditingRoom(null);
                            } else {
                                setEditingRoom(null);
                                setShowForm(!showForm);
                            }
                        }}
                    >
                        {showForm ? "Đóng biểu mẫu" : "+ Thêm phòng"}
                    </button>
                )}
            </div>

            {/* Form Thêm/Sửa phòng xuất hiện dạng khối mở rộng xuống dòng */}
            {showForm && (
                <div className="room-form-wrapper">
                    <RoomForm
                        key={editingRoom?.id ?? "new"}
                        room={editingRoom}
                        roomTypes={roomTypes}
                        onCancel={() => {
                            setShowForm(false);
                            setEditingRoom(null);
                        }}
                        onSuccess={() => {
                            setShowForm(false);
                            setEditingRoom(null);
                            getRooms();
                        }}
                    />
                </div>
            )}

            {/* Bộ lọc */}
            <div className="filters">
                <select
                    value={floor}
                    onChange={(e) => setFloor(e.target.value)}
                >
                    <option value="">Tất cả tầng</option>
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((f) => (
                        <option key={f} value={f}>Tầng {f}</option>
                    ))}
                </select>

                <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    <option value="AVAILABLE">Available (Trống)</option>
                    <option value="OCCUPIED">Occupied (Có khách)</option>
                    <option value="CLEANING">Cleaning (Dọn dẹp)</option>
                </select>

                <select
                    value={roomTypeId}
                    onChange={(e) => setRoomTypeId(e.target.value)}
                >
                    <option value="">Tất cả loại phòng</option>
                    {roomTypes.map((type) => (
                        <option key={type.id} value={type.id}>
                            {type.name}
                        </option>
                    ))}
                </select>

                <button className="btn-filter" onClick={getRooms}>
                    Lọc
                </button>

                <button className="btn-reset" onClick={resetFilters}>
                    Xóa lọc
                </button>
            </div>

            {loading && <p className="loading-text">Đang tải danh sách phòng...</p>}

            {/* Chú thích màu sắc trạng thái */}
            <div className="legend">
                <span className="legend-item">
                    <span className="legend-box available"></span>
                    Available (Trống)
                </span>

                <span className="legend-item">
                    <span className="legend-box occupied"></span>
                    Occupied (Có khách)
                </span>

                <span className="legend-item">
                    <span className="legend-box cleaning"></span>
                    Cleaning (Dọn dẹp)
                </span>
            </div>

            {/* Danh sách tầng và phòng */}
            {Object.keys(roomsByFloor).length === 0 && !loading && (
                <div className="empty-rooms">Không tìm thấy phòng nào phù hợp với bộ lọc.</div>
            )}

            {Object.keys(roomsByFloor)
                .sort((a, b) => Number(a) - Number(b))
                .map((currentFloor) => (
                    <div className="floor" key={currentFloor}>
                        <div className="floor-header">
                            <h2>Tầng {currentFloor}</h2>
                            <span className="floor-count">{roomsByFloor[currentFloor].length} phòng</span>
                        </div>

                        <div className="room-grid">
                            {roomsByFloor[currentFloor].map((room) => {
                                const isSelected = selectedRoom?.id === room.id;
                                return (
                                    <div
                                        key={room.id}
                                        className={`room-card ${getStatusClass(room.status)} ${isSelected ? "selected" : ""}`}
                                        onClick={() => handleRoomClick(room)}
                                        title={`Phòng ${room.roomNumber} - Nhấn để xem chi tiết`}
                                    >
                                        <strong>{room.roomNumber}</strong>
                                        <span>{room.status}</span>
                                    </div>
                                );
                            })}
                        </div>

                        {/* Chi tiết phòng hiển thị trực tiếp xuống dòng ngay bên dưới tầng của phòng đó (không dùng overlay) */}
                        {selectedRoom && String(selectedRoom.floor) === String(currentFloor) && (
                            <div className="room-inline-detail">
                                <div className="detail-top-bar">
                                    <div className="detail-title-group">
                                        <h3>Phòng {selectedRoom.roomNumber}</h3>
                                        <span className={`status-pill status-${(selectedRoom.status || "").toLowerCase()}`}>
                                            {selectedRoom.status}
                                        </span>
                                    </div>
                                    <button
                                        type="button"
                                        className="btn-close-inline"
                                        onClick={() => setSelectedRoom(null)}
                                    >
                                        Đóng chi tiết
                                    </button>
                                </div>

                                <div className="detail-info-grid">
                                    <div className="info-block">
                                        <span className="info-label">Tầng</span>
                                        <span className="info-value">Tầng {selectedRoom.floor}</span>
                                    </div>
                                    <div className="info-block">
                                        <span className="info-label">Loại phòng</span>
                                        <span className="info-value">{selectedRoom.roomTypeName || "Chưa phân loại"}</span>
                                    </div>
                                    <div className="info-block">
                                        <span className="info-label">Giá niêm yết</span>
                                        <span className="info-value">${selectedRoom.basePrice || 0} / đêm</span>
                                    </div>
                                </div>

                                <div className="detail-actions-panel">
                                    <div className="action-row">
                                        <span className="action-title">Chuyển trạng thái phòng:</span>
                                        <div className="status-btn-group">
                                            {selectedRoom.status === "AVAILABLE" && (
                                                <button
                                                    className="btn-change-status btn-to-occupied"
                                                    onClick={() => changeStatus("OCCUPIED")}
                                                >
                                                    Chuyển sang OCCUPIED (Nhận phòng)
                                                </button>
                                            )}
                                            {selectedRoom.status === "OCCUPIED" && (
                                                <button
                                                    className="btn-change-status btn-to-cleaning"
                                                    onClick={() => changeStatus("CLEANING")}
                                                >
                                                    Chuyển sang CLEANING (Dọn dẹp)
                                                </button>
                                            )}
                                            {selectedRoom.status === "CLEANING" && (
                                                <button
                                                    className="btn-change-status btn-to-available"
                                                    onClick={() => changeStatus("AVAILABLE")}
                                                >
                                                    Chuyển sang AVAILABLE (Sẵn sàng)
                                                </button>
                                            )}
                                        </div>
                                    </div>

                                    {isAdmin && (
                                        <div className="action-row admin-row">
                                            <span className="action-title">Thao tác quản trị:</span>
                                            <div className="admin-btn-group">
                                                <button
                                                    className="btn-edit-room"
                                                    onClick={() => {
                                                        setEditingRoom(selectedRoom);
                                                        setShowForm(true);
                                                        window.scrollTo({ top: 0, behavior: "smooth" });
                                                    }}
                                                >
                                                    Sửa phòng
                                                </button>
                                                <button
                                                    className="btn-delete-room"
                                                    onClick={handleDeleteRoom}
                                                >
                                                    Xóa phòng
                                                </button>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}
                    </div>
                ))}
        </div>
    );
}

export default RoomMatrix;
