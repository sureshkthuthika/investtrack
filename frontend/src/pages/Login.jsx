
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import authService from "../services/authService";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            await authService.login({ email, password });
            navigate("/dashboard");
        } catch (err) {
            setError(
                err.response?.data?.message || "Login failed. Check your credentials."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <form className="auth-card" onSubmit={handleSubmit}>
                <h1>InvestTrack</h1>
                <p>Sign in to manage your investment portfolio</p>

                {error && <div className="error-message">{error}</div>}

                <label>Email</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Enter your email"
                    required
                />

                <label>Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    required
                />

                <button type="submit" disabled={loading}>
                    {loading ? "Signing in..." : "Login"}
                </button>

                <p>
                    Don't have an account? <Link to="/register">Register</Link>
                </p>
            </form>
        </div>
    );
}

export default Login;
