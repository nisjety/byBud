import React, { useState } from "react";
import { login } from "../services/AuthService";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            const userData = await login(identifier, password);

            if (userData && Array.isArray(userData.roles)) {
                // Store user data and token in localStorage
                localStorage.setItem("userData", JSON.stringify(userData));
                localStorage.setItem("token", userData.accessToken);
                localStorage.setItem("userId", userData.userId);

                console.log("Login successful. Navigating to profile...");
                navigate("/profile"); // Navigate to the non-protected route
            } else {
                throw new Error("Invalid response format. Roles are missing.");
            }
        } catch (err) {
            console.error("Login Error:", err);
            const errorMessage =
                err.response?.data?.message || "Login failed. Please check your credentials.";
            setError(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <h2>Login</h2>
            {error && (
                <div role="alert" aria-live="assertive" style={{ color: "red", marginBottom: "1rem" }}>
                    {error}
                </div>
            )}
            <form onSubmit={handleSubmit}>
                <label htmlFor="identifier">Username or Email</label>
                <input
                    id="identifier"
                    type="text"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    required
                />
                <label htmlFor="password">Password</label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit" disabled={isLoading}>
                    {isLoading ? "Logging in..." : "Login"}
                </button>
            </form>
        </div>
    );
};

export default Login;
