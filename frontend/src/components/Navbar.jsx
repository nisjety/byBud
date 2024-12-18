import { Link, useNavigate } from "react-router-dom";
import PropTypes from "prop-types";

const Navbar = ({ authenticated, onLogout }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout();
        navigate("/login", { replace: true });
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo" aria-label="Go to Home">
                    Bybud
                </Link>
                {authenticated ? (
                    <div className="navbar-links">
                        <Link to="/profile" aria-label="Profile">
                            Profile
                        </Link>
                        <Link to="/delivery" aria-label="Deliveries">
                            Deliveries
                        </Link>
                        <Link to="/delivery/create" aria-label="Create Delivery">
                            Create Delivery
                        </Link>
                        <button className="btn-logout" onClick={handleLogout} aria-label="Logout">
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="navbar-links">
                        <Link to="/login" aria-label="Login">
                            Login
                        </Link>
                        <Link to="/register" aria-label="Register">
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
    onLogout: PropTypes.func.isRequired,
};

export default Navbar;
