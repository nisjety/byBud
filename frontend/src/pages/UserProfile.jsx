import React, { useEffect, useState } from "react";
import { getUserProfile, updateUserProfile } from "../services/UserService";

const UserProfile = () => {
    const [profile, setProfile] = useState(null);
    const [editable, setEditable] = useState(false);
    const [formData, setFormData] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const userId = localStorage.getItem("userId");
                if (!userId) throw new Error("User ID not found. Please log in again.");
                const data = await getUserProfile(userId);
                setProfile(data);
                setFormData(data);
            } catch (err) {
                setError(err.message || "Failed to fetch profile.");
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSaveChanges = async () => {
        try {
            const userId = localStorage.getItem("userId");
            const updatedProfile = await updateUserProfile(userId, formData);
            setProfile(updatedProfile);
            setEditable(false);
            setError(null);
        } catch (err) {
            setError(err.message || "Failed to update profile.");
        }
    };

    if (loading) return <p>Loading profile...</p>;
    if (error)
        return (
            <div role="alert" style={{ color: "red" }}>
                {error}
            </div>
        );

    return (
        <div>
            <h2>{editable ? "Edit Profile" : "Your Profile"}</h2>
            {editable ? (
                <div>
                    <label>
                        Username:
                        <input
                            type="text"
                            name="username"
                            value={formData.username || ""}
                            onChange={handleInputChange}
                        />
                    </label>
                    <label>
                        Full Name:
                        <input
                            type="text"
                            name="fullName"
                            value={formData.fullName || ""}
                            onChange={handleInputChange}
                            aria-label="Full Name"
                        />
                    </label>
                    <label>
                        Email:
                        <input
                            type="email"
                            name="email"
                            value={formData.email || ""}
                            onChange={handleInputChange}
                        />
                    </label>
                    <label>
                        Date of Birth:
                        <input
                            type="date"
                            name="dateOfBirth"
                            value={formData.dateOfBirth || ""}
                            onChange={handleInputChange}
                        />
                    </label>
                    <label>
                        Roles:
                        <input
                            type="text"
                            name="roles"
                            value={formData.roles?.join(", ") || ""}
                            onChange={(e) =>
                                setFormData({ ...formData, roles: e.target.value.split(",") })
                            }
                        />
                    </label>
                    <button onClick={handleSaveChanges}>Save Changes</button>
                    <button onClick={() => setEditable(false)}>Cancel</button>
                </div>
            ) : (
                <div role="region" aria-label="Your Profile">
                    <p>
                        <strong>Username:</strong> {profile.username}
                    </p>
                    <p>
                        <strong>Full Name:</strong> {profile.fullName}
                    </p>
                    <p>
                        <strong>Email:</strong> {profile.email}
                    </p>
                    <p>
                        <strong>Date of Birth:</strong> {profile.dateOfBirth}
                    </p>
                    <p>
                        <strong>Roles:</strong> {profile.roles?.join(", ")}
                    </p>
                    <button onClick={() => setEditable(true)}>Edit Profile</button>
                </div>
            )}
        </div>
    );
};

export default UserProfile;
