import { useState } from "react";
import Login from "./pages/Login";
import RoomMatrix from "./pages/RoomMatrix";
import RoomTypeManager from "./pages/RoomTypeManager";
import Header from "./components/Header";

function decodeJwtPayload(token) {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const paddedBase64 = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, "=");
    return JSON.parse(atob(paddedBase64));
}

function getCurrentUserRole() {
    const token = localStorage.getItem("token");

    if (!token) {
        return null;
    }

    try {
        const payload = decodeJwtPayload(token);
        const role = payload.role ?? payload.authorities?.[0] ?? null;

        if (typeof role === "string") {
            return role.replace(/^ROLE_/, "").toUpperCase();
        }

        if (role?.authority) {
            return role.authority.replace(/^ROLE_/, "").toUpperCase();
        }

        return null;
    } catch (error) {
        console.error("Error parsing JWT token:", error);
        return null;
    }
}

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(
        !!localStorage.getItem("token")
    );
    const [activeView, setActiveView] = useState("rooms");

    if (!isLoggedIn) {
        return <Login onLogin={() => setIsLoggedIn(true)} />;
    }

    const role = getCurrentUserRole();
    const isAdmin = role === "ADMIN";

    return (
        <>
            <Header
                activeView={activeView}
                isAdmin={isAdmin}
                onChangeView={setActiveView}
                onLogout={() => {
                    setActiveView("rooms");
                    setIsLoggedIn(false);
                }}
            />

            {activeView === "room-types" && isAdmin ? (
                <RoomTypeManager />
            ) : (
                <RoomMatrix role={role} />
            )}
        </>
    );
}

export default App;
