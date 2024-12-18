import React from "react";
import { Navigate } from "react-router-dom";
import PropTypes from "prop-types";

const ProtectedRoute = ({ children, authenticated }) => {
    if (!authenticated) {
        return <Navigate to="/login" replace />;
    }

    return children;
};

ProtectedRoute.propTypes = {
    children: PropTypes.node.isRequired,
    authenticated: PropTypes.bool.isRequired,
};

export default ProtectedRoute;
