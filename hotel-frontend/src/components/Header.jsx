function Header({ activeView, isAdmin, isStaff, onChangeView, onLogout }) {
    const handleLogout = () => {
        localStorage.removeItem("token");
        onLogout();
    };

    return (
        <div className="header">
            <h2>Hotel Management</h2>

            <div className="header-actions">
                <button
                    className={activeView === "rooms" ? "active" : ""}
                    onClick={() => onChangeView("rooms")}
                >
                    Phòng
                </button>

                {isAdmin && (
                    <button
                        className={activeView === "room-types" ? "active" : ""}
                        onClick={() => onChangeView("room-types")}
                    >
                        Loại phòng
                    </button>
                )}

                {(isAdmin || isStaff) && (
                    <button
                        className={activeView === "customers" ? "active" : ""}
                        onClick={() => onChangeView("customers")}
                    >
                        Khách hàng
                    </button>
                )}

                {(isAdmin || isStaff) && (
                    <button
                        className={activeView === "reception" ? "active" : ""}
                        onClick={() => onChangeView("reception")}
                    >
                        Lễ tân
                    </button>
                )}

                {(isAdmin || isStaff) && (
                    <button
                        className={activeView === "dashboard" ? "active" : ""}
                        onClick={() => onChangeView("dashboard")}
                    >
                        Dashboard
                    </button>
                )}

                <button onClick={handleLogout}>
                    Đăng xuất
                </button>
            </div>
        </div>
    );
}

export default Header;
