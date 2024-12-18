import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { lazy, Suspense, useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import Loader from "./components/Loader";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./App.css";

// Lazy load components for better performance
const HomePage = lazy(() => import("./pages/HomePage"));
const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const DeliveryList = lazy(() => import("./pages/DeliveryList"));
const CreateDelivery = lazy(() => import("./pages/CreateDelivery"));
const UserProfile = lazy(() => import("./pages/UserProfile"));
const CourierPage = lazy(() => import("./pages/CourierPage"));

const App = () => {
    const [authenticated, setAuthenticated] = useState(false);
    const [roles, setRoles] = useState([]);

    // Check authentication and fetch roles on component mount
    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem("userData") || "{}");
        if (userData && Array.isArray(userData.roles)) {
            setRoles(userData.roles);
            setAuthenticated(true);
        } else {
            setAuthenticated(false);
        }
    }, []);

    // Handle user logout
    const onLogout = () => {
        localStorage.clear();
        setAuthenticated(false);
        setRoles([]);
    };

    return (
        <Router>
            <Navbar authenticated={authenticated} onLogout={onLogout} />
            <main>
                <Suspense fallback={<Loader />}>
                    <Routes>
                        {/* Home Route */}
                        <Route
                            path="/"
                            element={
                                authenticated ? (
                                    // Redirect based on user role
                                    roles.includes("COURIER") ? (
                                        <Navigate to="/courier" replace />
                                    ) : (
                                        <Navigate to="/profile" replace />
                                    )
                                ) : (
                                    <HomePage />
                                )
                            }
                        />

                        {/* Authentication Routes */}
                        <Route path="/login" element={<Login setAuthenticated={setAuthenticated} />} />
                        <Route path="/register" element={<Register />} />

                        {/* Protected Routes */}
                        <Route
                            path="/delivery"
                            element={
                                <ProtectedRoute authenticated={authenticated}>
                                    <DeliveryList />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/delivery/create"
                            element={
                                <ProtectedRoute authenticated={authenticated}>
                                    <CreateDelivery />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/profile"
                            element={
                                <ProtectedRoute authenticated={authenticated}>
                                    <UserProfile />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/courier"
                            element={
                                <ProtectedRoute authenticated={authenticated}>
                                    <CourierPage />
                                </ProtectedRoute>
                            }
                        />

                        {/* Fallback Route for Undefined Paths */}
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </Suspense>
            </main>

            {/* Toast Notifications Container */}
            <ToastContainer
                position="top-right"
                autoClose={3000}
                hideProgressBar={false}
                newestOnTop
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="colored"
            />
        </Router>
    );
};

export default App;
