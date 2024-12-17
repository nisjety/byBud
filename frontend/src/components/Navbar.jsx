import { Link, useNavigate } from "react-router-dom";
import PropTypes from "prop-types";

const Navbar = ({ authenticated, onLogout }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout(); // Use the onLogout function passed as a prop
        navigate("/login");
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo" aria-label="Go to Home">
                    Bybud
                </Link>
                {authenticated ? (
                    <div className="navbar-links">
                        <Link
                            to="/delivery"
                            aria-label="View Deliveries"
                            aria-current={window.location.pathname === "/delivery" ? "page" : undefined}
                        >
                            Deliveries
                        </Link>
                        <Link
                            to="/delivery/create"
                            aria-label="Create a Delivery"
                            aria-current={window.location.pathname === "/delivery/create" ? "page" : undefined}
                        >
                            Create Delivery
                        </Link>
                        <button
                            className="btn-logout"
                            onClick={handleLogout}
                            aria-label="Logout"
                        >
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="navbar-links">
                        <Link
                            to="/login"
                            aria-label="Login"
                            aria-current={window.location.pathname === "/login" ? "page" : undefined}
                        >
                            Login
                        </Link>
                        <Link
                            to="/register"
                            aria-label="Register"
                            aria-current={window.location.pathname === "/register" ? "page" : undefined}
                        >
                            Register
                        </Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

Navbar.propTypes = {
    authenticated: PropTypes.bool.isRequired,
    onLogout: PropTypes.func.isRequired, // Mark onLogout as required
};

export default Navbar;
