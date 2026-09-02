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
            console.log("LỖI ROOM TYPES:", error);
            console.log("STATUS:", error.response?.status);
            console.log("DATA:", error.response?.data);
        }
    }, []);

    useEffect(() => {
        // Fetching initial API data is the external sync this effect owns.
        // eslint-disable-next-line react-hooks/set-state-in-effect
        getRoomTypes();
    }, [getRoomTypes]);

    useEffect(() => {
        // Refetch rooms when filters change.
        // eslint-disable-next-line react-hooks/set-state-in-effect
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

            alert("Đổi trạng thái thành công!");
            setSelectedRoom(null);

            // Lấy lại dữ liệu mới từ Backend
            getRooms();

        } catch (error) {
            console.log("Lỗi đổi trạng thái:", error);
            alert("Không thể đổi trạng thái!");
        }
    };

    const resetFilters = () => {
        setFloor("");
        setStatus("");
        setRoomTypeId("");
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

            <h1>Quản lý phòng</h1>
            <div className="filters">

                <select
                    value={floor}
                    onChange={(e) => setFloor(e.target.value)}
                >
                    <option value="">Tất cả tầng</option>
                    <option value="1">Tầng 1</option>
                    <option value="2">Tầng 2</option>
                    <option value="3">Tầng 3</option>
                    <option value="4">Tầng 4</option>
                    <option value="5">Tầng 5</option>
                    <option value="6">Tầng 6</option>
                    <option value="7">Tầng 7</option>
                </select>

                <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    <option value="AVAILABLE">Available</option>
                    <option value="OCCUPIED">Occupied</option>
                    <option value="CLEANING">Cleaning</option>
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

                <button onClick={getRooms}>
                    Lọc
                </button>

                <button onClick={resetFilters}>
                    Xóa lọc
                </button>

                {isAdmin && (
                    <button
                        onClick={() => {
                            setEditingRoom(null);
                            setShowForm(true);
                        }}
                    >
                        + Thêm phòng
                    </button>
                )}
            </div>
            {loading && <p>Đang tải danh sách phòng...</p>}
            <div className="legend">
                <span className="legend-item">
                    <span className="legend-box available"></span>
                    Available
                </span>

                <span className="legend-item">
                    <span className="legend-box occupied"></span>
                    Occupied
                </span>

                <span className="legend-item">
                    <span className="legend-box cleaning"></span>
                    Cleaning
                </span>
            </div>

            {Object.keys(roomsByFloor)
                .sort((a, b) => a - b)
                .map((floor) => (
                    <div className="floor" key={floor}>

                        <h2>Tầng {floor}</h2>

                        <div className="room-grid">

                            {roomsByFloor[floor].map((room) => (
                                <div
                                    key={room.id}
                                    className={`room-card ${getStatusClass(
                                        room.status
                                    )}`}
                                    onClick={() => setSelectedRoom(room)}
                                >
                                    <strong>
                                        {room.roomNumber}
                                    </strong>

                                    <span>
                                        {room.status}
                                    </span>
                                </div>
                            ))}

                        </div>
                    </div>
                ))}
            {showForm && (
                <RoomForm
                    key={editingRoom?.id ?? "new"}
                    room={editingRoom}
                    roomTypes={roomTypes}
                    onCancel={() => setShowForm(false)}
                    onSuccess={() => {
                        setShowForm(false);
                        getRooms();
                    }}
                />
            )}
            {selectedRoom && (
                <div className="room-detail">

                    <h2>Phòng {selectedRoom.roomNumber}</h2>

                    <p>
                        <strong>Tầng:</strong> {selectedRoom.floor}
                    </p>

                    <p>
                        <strong>Loại phòng:</strong>{" "}
                        {selectedRoom.roomTypeName}
                    </p>

                    <p>
                        <strong>Giá:</strong>{" "}
                        {selectedRoom.basePrice}
                    </p>

                    <p>
                        <strong>Trạng thái:</strong>{" "}
                        {selectedRoom.status}
                    </p>
                    {isAdmin && (
                        <>
                            <button
                                onClick={() => {
                                    setEditingRoom(selectedRoom);
                                    setShowForm(true);
                                    setSelectedRoom(null);
                                }}
                            >
                                Sửa phòng
                            </button>

                            <button
                                onClick={async () => {
                                    if (!window.confirm("Bạn có chắc muốn xóa phòng này?")) {
                                        return;
                                    }

                                    try {
                                        await api.delete(
                                            `/api/v1/rooms/${selectedRoom.id}`
                                        );

                                        alert("Xóa phòng thành công!");
                                        setSelectedRoom(null);
                                        getRooms();
                                    } catch (error) {
                                        console.log("Lỗi xóa phòng:", error);
                                        alert(
                                            error.response?.data ||
                                            "Không thể xóa phòng!"
                                        );
                                    }
                                }}
                            >
                                Xóa phòng
                            </button>
                        </>
                    )}


                    <h3>Đổi trạng thái</h3>

                    {selectedRoom.status === "AVAILABLE" && (
                        <button onClick={() => changeStatus("OCCUPIED")}>
                            → OCCUPIED
                        </button>
                    )}

                    {selectedRoom.status === "OCCUPIED" && (
                        <button onClick={() => changeStatus("CLEANING")}>
                            → CLEANING
                        </button>
                    )}

                    {selectedRoom.status === "CLEANING" && (
                        <button onClick={() => changeStatus("AVAILABLE")}>
                            → AVAILABLE
                        </button>
                    )}

                    <br />
                    <br />

                    <button onClick={() => setSelectedRoom(null)}>
                        Đóng
                    </button>

                </div>
            )}
        </div>
    );
}

export default RoomMatrix;
