import React, { useState, useEffect } from "react";
import { register } from "../services/AuthService";
import { getRoles } from "../services/UserService";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        fullName: "",
        dateOfBirth: "",
        role: "", // No default role until fetched
    });
    const [roles, setRoles] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRoles = async () => {
            try {
                const rolesData = await getRoles();
                setRoles(rolesData);
                setFormData((prev) => ({
                    ...prev,
                    role: rolesData[0], // Default to the first role
                }));
            } catch (err) {
                console.error("Error fetching roles:", err.message);
                setError("Failed to fetch roles. Please try again later.");
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

        console.log("FormData before sending:", formData);

        try {
            const encodedData = new URLSearchParams(formData); // Convert to URL-encoded format
            await register(encodedData);
            toast.success("Registration successful!");
            navigate("/dashboard");
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed.");
        }
    };

    return (
        <div>
            <h2>Register</h2>
            {error && <div style={{ color: "red" }}>{error}</div>}
            <form onSubmit={handleSubmit}>
                <input name="username" value={formData.username} onChange={handleChange} placeholder="Username" />
                <input name="email" value={formData.email} onChange={handleChange} placeholder="Email" />
                <input name="password" value={formData.password} onChange={handleChange} placeholder="Password" />
                <input name="fullName" value={formData.fullName} onChange={handleChange} placeholder="Full Name" />
                <input name="dateOfBirth" value={formData.dateOfBirth} onChange={handleChange} type="date" />
                <select name="role" value={formData.role} onChange={handleChange}>
                    {roles.map((role) => (
                        <option key={role} value={role}>
                            {role}
                        </option>
                    ))}
                </select>
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;
