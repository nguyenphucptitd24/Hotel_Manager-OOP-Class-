import { useCallback, useEffect, useState } from "react";
import api from "../services/api";
import { getApiErrorMessage } from "../services/errors";
import "./RoomTypeManager.css";

const emptyForm = {
    name: "",
    basePrice: "",
    capacity: "",
    description: "",
};

function RoomTypeManager() {
    const [roomTypes, setRoomTypes] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [editingType, setEditingType] = useState(null);
    const [error, setError] = useState("");
    const [loadingList, setLoadingList] = useState(false);
    const [saving, setSaving] = useState(false);

    const loadRoomTypes = useCallback(async () => {
        try {
            setLoadingList(true);
            const response = await api.get("/api/v1/room-types");
            setRoomTypes(response.data);
        } catch (error) {
            setError(getApiErrorMessage(error, "Không thể tải loại phòng"));
        } finally {
            setLoadingList(false);
        }
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        loadRoomTypes();
    }, [loadRoomTypes]);

    const updateField = (field, value) => {
        setForm((currentForm) => ({
            ...currentForm,
            [field]: value,
        }));
        setError("");
    };

    const resetForm = () => {
        setForm(emptyForm);
        setEditingType(null);
        setError("");
    };

    const editRoomType = (roomType) => {
        setEditingType(roomType);
        setForm({
            name: roomType.name,
            basePrice: roomType.basePrice,
            capacity: roomType.capacity,
            description: roomType.description ?? "",
        });
        setError("");
    };

    const validateForm = () => {
        if (!form.name.trim()) {
            setError("Vui lòng nhập tên loại phòng");
            return false;
        }

        if (!form.basePrice || Number(form.basePrice) <= 0) {
            setError("Vui lòng nhập giá phòng hợp lệ");
            return false;
        }

        if (!form.capacity || Number(form.capacity) <= 0) {
            setError("Vui lòng nhập sức chứa hợp lệ");
            return false;
        }

        return true;
    };

    const saveRoomType = async (event) => {
        event.preventDefault();

        if (!validateForm()) {
            return;
        }

        const payload = {
            name: form.name.trim(),
            basePrice: Number(form.basePrice),
            capacity: Number(form.capacity),
            description: form.description.trim(),
        };

        try {
            setSaving(true);

            if (editingType) {
                await api.put(`/api/v1/room-types/${editingType.id}`, payload);
            } else {
                await api.post("/api/v1/room-types", payload);
            }

            resetForm();
            await loadRoomTypes();
        } catch (error) {
            setError(getApiErrorMessage(error, "Không thể lưu loại phòng"));
        } finally {
            setSaving(false);
        }
    };

    const deleteRoomType = async (roomType) => {
        if (!window.confirm(`Bạn có chắc muốn xóa loại phòng "${roomType.name}"?`)) {
            return;
        }

        try {
            setSaving(true);
            await api.delete(`/api/v1/room-types/${roomType.id}`);

            if (editingType?.id === roomType.id) {
                resetForm();
            }

            await loadRoomTypes();
        } catch (error) {
            setError(getApiErrorMessage(error, "Không thể xóa loại phòng"));
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="room-type-page">
            <h1>Quản lý loại phòng</h1>

            <div className="room-type-layout">
                <form className="room-type-form" onSubmit={saveRoomType}>
                    <h2>{editingType ? "Sửa loại phòng" : "Thêm loại phòng"}</h2>

                    {error && <div className="form-error">{error}</div>}

                    <label>
                        Tên loại phòng
                        <input
                            type="text"
                            value={form.name}
                            onChange={(event) => updateField("name", event.target.value)}
                            disabled={saving}
                            required
                        />
                    </label>

                    <label>
                        Giá cơ bản
                        <input
                            type="number"
                            min="1"
                            step="0.01"
                            value={form.basePrice}
                            onChange={(event) => updateField("basePrice", event.target.value)}
                            disabled={saving}
                            required
                        />
                    </label>

                    <label>
                        Sức chứa
                        <input
                            type="number"
                            min="1"
                            value={form.capacity}
                            onChange={(event) => updateField("capacity", event.target.value)}
                            disabled={saving}
                            required
                        />
                    </label>

                    <label>
                        Mô tả
                        <textarea
                            value={form.description}
                            onChange={(event) => updateField("description", event.target.value)}
                            disabled={saving}
                            rows="3"
                        />
                    </label>

                    <div className="form-actions">
                        <button type="submit" disabled={saving}>
                            {saving ? "Đang lưu..." : editingType ? "Cập nhật" : "Thêm"}
                        </button>

                        {editingType && (
                            <button type="button" onClick={resetForm} disabled={saving}>
                                Hủy sửa
                            </button>
                        )}
                    </div>
                </form>

                <div className="room-type-table-wrap">
                    {loadingList && <p>Đang tải dữ liệu...</p>}

                    <table className="room-type-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên</th>
                                <th>Giá cơ bản</th>
                                <th>Sức chứa</th>
                                <th>Mô tả</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roomTypes.map((roomType) => (
                                <tr key={roomType.id}>
                                    <td>{roomType.id}</td>
                                    <td>{roomType.name}</td>
                                    <td>{Number(roomType.basePrice).toLocaleString("vi-VN")}</td>
                                    <td>{roomType.capacity}</td>
                                    <td>{roomType.description}</td>
                                    <td>
                                        <button type="button" onClick={() => editRoomType(roomType)} disabled={saving}>
                                            Sửa
                                        </button>
                                        <button type="button" onClick={() => deleteRoomType(roomType)} disabled={saving}>
                                            Xóa
                                        </button>
                                    </td>
                                </tr>
                            ))}

                            {roomTypes.length === 0 && !loadingList && (
                                <tr>
                                    <td colSpan="6">Chưa có loại phòng</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default RoomTypeManager;
