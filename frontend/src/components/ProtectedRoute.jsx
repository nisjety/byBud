import { jwtDecode } from "jwt-decode";
import PropTypes from "prop-types";

const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem("token");

    if (token) {
        try {
            const decodedToken = jwtDecode(token); // Use jwtDecode
            const isTokenValid = decodedToken.exp * 1000 > Date.now();

            if (!isTokenValid) {
                localStorage.removeItem("token");
                window.location.href = "/login";
                return null;
            }
        } catch (error) {
            console.error("Invalid token", error);
            localStorage.removeItem("token");
            window.location.href = "/login";
            return null;
        }
        return children;
    }

    window.location.href = "/login";
    return null;
};

ProtectedRoute.propTypes = {
    children: PropTypes.node.isRequired,
};

export default ProtectedRoute;
