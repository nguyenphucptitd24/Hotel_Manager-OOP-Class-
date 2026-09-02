import { useState } from "react";
import api from "../services/api";
import { getApiErrorMessage } from "../services/errors";

function getInitialFormState(room) {
    return {
        roomNumber: room?.roomNumber ?? "",
        floor: room?.floor ?? "",
        status: room?.status ?? "AVAILABLE",
        roomTypeId: room?.roomTypeId ?? "",
    };
}

function RoomForm({ room, roomTypes, onSuccess, onCancel }) {
    const initialFormState = getInitialFormState(room);
    const [roomNumber, setRoomNumber] = useState(initialFormState.roomNumber);
    const [floor, setFloor] = useState(initialFormState.floor);
    const [status, setStatus] = useState(initialFormState.status);
    const [roomTypeId, setRoomTypeId] = useState(initialFormState.roomTypeId);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const validateForm = () => {
        if (!roomNumber.trim()) {
            setError("Vui lòng nhập số phòng");
            return false;
        }
        if (!floor || floor < 1 || floor > 10) {
            setError("Vui lòng nhập tầng hợp lệ (1-10)");
            return false;
        }
        if (!roomTypeId) {
            setError("Vui lòng chọn loại phòng");
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setLoading(true);
        setError("");

        const data = {
            roomNumber: roomNumber.trim(),
            floor: Number(floor),
            status: status,
            roomType: {
                id: Number(roomTypeId)
            }
        };

        try {
            if (room) {
                await api.put(`/api/v1/rooms/${room.id}`, data);
                alert("Cập nhật phòng thành công!");
            } else {
                await api.post("/api/v1/rooms", data);
                alert("Thêm phòng thành công!");
            }

            onSuccess();

        } catch (error) {
            const errorMsg = getApiErrorMessage(error, "Không thể lưu phòng!");
            setError(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="room-form">

            <h2>
                {room ? "Sửa phòng" : "Thêm phòng"}
            </h2>

            {error && <div style={{ color: "red", marginBottom: "10px" }}>{error}</div>}

            <form onSubmit={handleSubmit}>

                <div>
                    <label>Số phòng</label>

                    <input
                        type="text"
                        value={roomNumber}
                        onChange={(e) => {
                            setRoomNumber(e.target.value);
                            setError("");
                        }}
                        disabled={loading}
                        required
                    />
                </div>

                <div>
                    <label>Tầng</label>

                    <input
                        type="number"
                        value={floor}
                        onChange={(e) => {
                            setFloor(e.target.value);
                            setError("");
                        }}
                        disabled={loading}
                        required
                    />
                </div>

                <div>
                    <label>Trạng thái</label>

                    <select
                        value={status}
                        onChange={(e) => {
                            setStatus(e.target.value);
                            setError("");
                        }}
                        disabled={loading}
                    >
                        <option value="AVAILABLE">
                            AVAILABLE
                        </option>

                        <option value="OCCUPIED">
                            OCCUPIED
                        </option>

                        <option value="CLEANING">
                            CLEANING
                        </option>
                    </select>
                </div>

                <div>
                    <label>Loại phòng</label>

                    <select
                        value={roomTypeId}
                        onChange={(e) => {
                            setRoomTypeId(e.target.value);
                            setError("");
                        }}
                        disabled={loading}
                        required
                    >
                        <option value="">
                            -- Chọn loại phòng --
                        </option>

                        {roomTypes.map((type) => (
                            <option
                                key={type.id}
                                value={type.id}
                            >
                                {type.name}
                            </option>
                        ))}
                    </select>
                </div>

                <br />

                <button type="submit" disabled={loading}>
                    {loading ? "Đang lưu..." : (room ? "Cập nhật" : "Thêm phòng")}
                </button>

                <button
                    type="button"
                    onClick={onCancel}
                    disabled={loading}
                >
                    Hủy
                </button>

            </form>
        </div>
    );
}

export default RoomForm;
