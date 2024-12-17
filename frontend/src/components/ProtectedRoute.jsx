import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import PropTypes from "prop-types";

const ProtectedRoute = ({ children }) => {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    if (token) {
        try {
            const decodedToken = jwtDecode(token);
            const isTokenValid = decodedToken.exp * 1000 > Date.now();

            if (!isTokenValid) {
                localStorage.removeItem("token");
                localStorage.removeItem("userData"); // Only clear relevant data
                navigate("/login", { replace: true });
                return null;
            }
        } catch (error) {
            console.error("Invalid token", error);
            localStorage.removeItem("token");
            localStorage.removeItem("userData");
            navigate("/login", { replace: true });
            return null;
        }
        return children;
    }

    navigate("/login", { replace: true });
    return null;
};

ProtectedRoute.propTypes = {
    children: PropTypes.node.isRequired,
};

export default ProtectedRoute;
