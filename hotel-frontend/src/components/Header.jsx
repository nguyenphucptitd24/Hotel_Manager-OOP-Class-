function Header({ activeView, isAdmin, onChangeView, onLogout }) {
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

                <button onClick={handleLogout}>
                    Đăng xuất
                </button>
            </div>
        </div>
    );
}

export default Header;
