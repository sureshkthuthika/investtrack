
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import authService from "../services/authService";

function Register() {
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
            await authService.register({ email, password });
            navigate("/");
        } catch (err) {
            setError(
                err.response?.data?.message || "Registration failed."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <form className="auth-card" onSubmit={handleSubmit}>
                <h1>Create Account</h1>
                <p>Start managing your investments with InvestTrack</p>

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
                    placeholder="Create a password"
                    minLength={8}
                    required
                />

                <button type="submit" disabled={loading}>
                    {loading ? "Creating account..." : "Register"}
                </button>

                <p>
                    Already registered? <Link to="/">Login</Link>
                </p>
            </form>
        </div>
    );
}

export default Register;
