import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';

const Navbar = ({ authenticated }) => {
    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = '/login'; // Redirect to login after logout
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo" aria-label="Go to Home">Bybud</Link>
                {authenticated ? (
                    <div className="navbar-links">
                        <Link to="/delivery" aria-label="View Deliveries">Deliveries</Link>
                        <Link to="/delivery/create" aria-label="Create a Delivery">Create Delivery</Link>
                        <button className="btn-logout" onClick={handleLogout} aria-label="Logout">
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="navbar-links">
                        <Link to="/login" aria-label="Login">Login</Link>
                        <Link to="/register" aria-label="Register">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

Navbar.propTypes = {
    authenticated: PropTypes.bool.isRequired,
};

export default Navbar;
