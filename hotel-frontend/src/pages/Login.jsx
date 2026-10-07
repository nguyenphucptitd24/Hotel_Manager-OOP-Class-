import { useState } from "react";
import api from "../services/api";
import "./Login.css";

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
        <main className="login-page">
            <section className="login-panel">
                <div className="login-form-wrap">
                    <div className="login-heading">
                        <span className="login-eyebrow">HOTEL MANAGEMENT SYSTEM</span>
                        <h2>Đăng nhập</h2>
                        <p>Nhập thông tin tài khoản để tiếp tục.</p>
                    </div>

                    {error && (
                        <div className="login-error" role="alert">
                            <span aria-hidden="true">!</span>
                            {error}
                        </div>
                    )}

                    <form className="login-form" onSubmit={handleLogin}>
                        <div className="login-field">
                            <label htmlFor="login-username">Tên đăng nhập</label>
                            <input
                                id="login-username"
                                type="text"
                                placeholder="Nhập tên đăng nhập"
                                autoComplete="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                disabled={loading}
                            />
                        </div>

                        <div className="login-field">
                            <label htmlFor="login-password">Mật khẩu</label>
                            <input
                                id="login-password"
                                type="password"
                                placeholder="Nhập mật khẩu"
                                autoComplete="current-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                            />
                        </div>

                        <button className="login-submit" type="submit" disabled={loading}>
                            {loading ? (
                                <>
                                    <span className="login-spinner" aria-hidden="true" />
                                    Đang đăng nhập...
                                </>
                            ) : (
                                <>
                                    Đăng nhập
                                    <span aria-hidden="true">→</span>
                                </>
                            )}
                        </button>
                    </form>

                    <p className="login-help">Cần hỗ trợ? Vui lòng liên hệ quản trị viên hệ thống.</p>
                </div>
                <footer className="login-copyright">© {new Date().getFullYear()} Hospitality · Hotel Management</footer>
            </section>
        </main>
    );
}

export default Login;