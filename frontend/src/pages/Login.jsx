import React, { useState } from "react";
import PropTypes from "prop-types"; // Import PropTypes
import { login } from "../services/AuthService";
import { useNavigate } from "react-router-dom";

const Login = ({ setAuthenticated }) => {
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
            const response = await login(identifier, password);
            const { data } = response;

            if (data) {
                localStorage.setItem("userData", JSON.stringify(data)); // Store the full user data
                localStorage.setItem("roles", JSON.stringify(data.roles)); // Store roles as a JSON string
                localStorage.setItem("userId", data.id); // Store user ID
                setAuthenticated(true);

                // Redirect based on roles
                if (data.roles.includes("COURIER")) {
                    navigate("/courier");
                } else if (data.roles.includes("CUSTOMER")) {
                    navigate("/profile");
                } else {
                    navigate("/");
                }
            }
        } catch (err) {
            setError(err.response?.data?.message || "Login failed. Please check your credentials.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <h2>Login</h2>
            {error && <div style={{ color: "red" }}>{error}</div>}
            <form onSubmit={handleSubmit}>
                <label>
                    Username or Email:
                    <input
                        type="text"
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </label>
                <button type="submit" disabled={isLoading}>
                    {isLoading ? "Logging in..." : "Login"}
                </button>
            </form>
        </div>
    );
};

Login.propTypes = {
    setAuthenticated: PropTypes.func.isRequired,
};

export default Login;
