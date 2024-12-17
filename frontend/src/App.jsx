import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { lazy, Suspense, useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import Loader from "./components/Loader";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./App.css";

const HomePage = lazy(() => import("./pages/HomePage"));
const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const UserProfile = lazy(() => import("./pages/UserProfile"));
const DeliveryList = lazy(() => import("./pages/DeliveryList"));
const CreateDelivery = lazy(() => import("./pages/CreateDelivery"));

const App = () => {
    const [authenticated, setAuthenticated] = useState(!!localStorage.getItem("token"));

    useEffect(() => {
        const handleStorageChange = () => {
            setAuthenticated(!!localStorage.getItem("token"));
        };
        window.addEventListener("storage", handleStorageChange);
        return () => window.removeEventListener("storage", handleStorageChange);
    }, []);

    const onLogout = () => {
        localStorage.clear();
        setAuthenticated(false);
    };

    return (
        <Router>
            <div className="App">
                <Navbar authenticated={authenticated} onLogout={onLogout} />
                <main>
                    <Suspense fallback={<Loader />}>
                        <Routes>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route path="/profile" element={<UserProfile />} />
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
                            <Route path="*" element={<Navigate to="/" replace />} />
                        </Routes>
                    </Suspense>
                </main>
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
