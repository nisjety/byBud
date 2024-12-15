import React, { useState, useEffect } from "react";
import { register, getRoles } from "../services/AuthService";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        fullName: "",
        dateOfBirth: "",
        role: "ROLE_USER", // Default role
    });
    const [roles, setRoles] = useState(["ROLE_USER"]); // Default fallback role
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    // Fetch roles on mount
    useEffect(() => {
        const fetchRoles = async () => {
            try {
                const rolesData = await getRoles();
                setRoles(rolesData);
                if (!rolesData.includes(formData.role)) {
                    setFormData({ ...formData, role: rolesData[0] || "ROLE_USER" }); // Set the first role as default
                }
            } catch {
                setError("Failed to fetch roles. Using default roles.");
            }
        };
        fetchRoles();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        setError(null); // Clear error when the user modifies the form
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Input validation
        if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(formData.password)) {
            setError(
                "Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character."
            );
            return;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            setError("Please enter a valid email address.");
            return;
        }
        if (new Date().getFullYear() - new Date(formData.dateOfBirth).getFullYear() < 18) {
            setError("You must be at least 18 years old to register.");
            return;
        }

        setIsLoading(true);
        try {
            await register(formData);
            toast.success("Registration successful!");
            navigate("/login");
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <h2>Register</h2>
            {error && (
                <div
                    role="alert"
                    aria-live="assertive"
                    style={{ color: "red", marginBottom: "1rem" }}
                >
                    {error}
                </div>
            )}
            <form onSubmit={handleSubmit}>
                <label htmlFor="username">Username</label>
                <input
                    id="username"
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="password">Password</label>
                <input
                    id="password"
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="fullName">Full Name</label>
                <input
                    id="fullName"
                    type="text"
                    name="fullName"
                    placeholder="Full Name"
                    value={formData.fullName}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="dateOfBirth">Date of Birth</label>
                <input
                    id="dateOfBirth"
                    type="date"
                    name="dateOfBirth"
                    placeholder="Date of Birth"
                    value={formData.dateOfBirth}
                    onChange={handleChange}
                    required
                />
                <label htmlFor="role">Role</label>
                <select
                    id="role"
                    name="role"
                    value={formData.role}
                    onChange={handleChange}
                    required
                >
                    {roles.map((role) => (
                        <option key={role} value={role}>
                            {role.replace("ROLE_", "").toLowerCase()}
                        </option>
                    ))}
                </select>
                <button type="submit" disabled={isLoading}>
                    {isLoading ? "Registering..." : "Register"}
                </button>
            </form>
        </div>
    );
};

export default Register;
