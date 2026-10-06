import { useState } from "react";
import api from "../services/api";

function Login({ onLogin }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        
        // Input validation
        if (!username.trim()) {
            setError("Vui lòng nhập username");
            return;
        }
        if (!password) {
            setError("Vui lòng nhập password");
            return;
        }
        if (password.length < 4) {
            setError("Password phải có ít nhất 4 ký tự");
            return;
        }

        setError("");
        setLoading(true);

        try {
            const response = await api.post("/api/v1/auth/login", {
                username: username.trim(),
                password: password,
            });

            const token = response.data.token;

            localStorage.setItem("token", token);

            setUsername("");
            setPassword("");
            alert("Đăng nhập thành công!");
            onLogin();
        } catch (error) {
            console.log("ERROR:", error);
            console.log("STATUS:", error.response?.status);
            console.log("DATA:", error.response?.data);

            const errorMsg = error.response?.data?.message || error.message || "Đăng nhập thất bại";
            setError(errorMsg);
            alert("Login lỗi: " + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Đăng nhập</h2>

            {error && <div style={{ color: "red", marginBottom: "10px" }}>{error}</div>}

            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    disabled={loading}
                />

                <br />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    disabled={loading}
                />

                <br />

                <button type="submit" disabled={loading}>
                    {loading ? "Đang đăng nhập..." : "Đăng nhập"}
                </button>
            </form>
        </div>
    );
}

export default Login;