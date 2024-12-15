import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { lazy, Suspense, useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import Loader from "./components/Loader";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify"; // Import ToastContainer
import "react-toastify/dist/ReactToastify.css"; // Import Toastify styles
import "./App.css";

// Lazy load pages
const HomePage = lazy(() => import("./pages/HomePage"));
const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const DeliveryList = lazy(() => import("./pages/DeliveryList"));
const CreateDelivery = lazy(() => import("./pages/CreateDelivery"));
const BidList = lazy(() => import("./pages/BidList"));

const App = () => {
    const [authenticated, setAuthenticated] = useState(!!localStorage.getItem("token"));

    useEffect(() => {
        const handleStorageChange = () => {
            setAuthenticated(!!localStorage.getItem("token"));
        };
        window.addEventListener("storage", handleStorageChange);
        return () => window.removeEventListener("storage", handleStorageChange);
    }, []);

    return (
        <Router>
            <div className="App">
                {/* Navbar displayed on all pages */}
                <Navbar authenticated={authenticated} />
                <main>
                    {/* Suspense fallback loader */}
                    <Suspense fallback={<Loader />}>
                        <Routes>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route
                                path="/delivery"
                                element={
                                    <ProtectedRoute>
                                        <DeliveryList />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/delivery/create"
                                element={
                                    <ProtectedRoute>
                                        <CreateDelivery />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/bids/:deliveryId"
                                element={
                                    <ProtectedRoute>
                                        <BidList />
                                    </ProtectedRoute>
                                }
                            />
                            {/* Fallback route for unmatched paths */}
                            <Route path="*" element={<Navigate to="/" replace />} />
                        </Routes>
                    </Suspense>
                </main>
                {/* ToastContainer added for notifications */}
                <ToastContainer
                    position="top-right"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                    theme="colored"
                />
            </div>
        </Router>
    );
};

export default App;
