import React from 'react';
import { useState } from "react";
import Login from "./Login";
import Register from "./Register";
import "../styles/HomePage.css";

const HomePage = () => {
    const [activeTab, setActiveTab] = useState("login");

    return (
        <div className="home-page">
            <h1>Welcome to ByBud</h1>
            <div className="tab-container">
                <button
                    className={`tab ${activeTab === "login" ? "active" : ""}`}
                    onClick={() => setActiveTab("login")}
                >
                    Login
                </button>
                <button
                    className={`tab ${activeTab === "register" ? "active" : ""}`}
                    onClick={() => setActiveTab("register")}
                >
                    Register
                </button>
            </div>
            <div className="tab-content">
                {activeTab === "login" ? <Login /> : <Register />}
            </div>
        </div>
    );
};

export default HomePage;
